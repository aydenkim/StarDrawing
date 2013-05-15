/**
 * TayokiRecognizer.java
 * 
 * Revision History:<br>
 * Nov 9, 2011 fvides - File created
 *
 * <p>
 * <pre>
 * This work is released under the BSD License:
 * (C) 2011 Sketch Recognition Lab, Texas A&M University (hereafter SRL @ TAMU)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&M University 
 *       nor the names of its contributors may be used to endorse or promote 
 *       products derived from this software without specific prior written 
 *       permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */

package recognition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ladder.io.XMLFileFilter;

import model.agent.MathSketchAgent;

import shapes.AbstractShape;
import shapes.DigitEight;
import shapes.DigitFive;
import shapes.DigitFour;
import shapes.DigitNine;
import shapes.DigitOne;
import shapes.DigitSeven1;
import shapes.DigitSeven2;
import shapes.DigitSix;
import shapes.DigitThree;
import shapes.DigitTwo;
import shapes.DigitZero;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import recognition.ClosedShapeSimilarityConstraint;

import ecologylab.serialization.SIMPLTranslationException;
import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.SContainer;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.IRecognitionResult;
import edu.tamu.recognition.RecognitionResult;

import edu.tamu.recognition.constraint.constrainable.ConstrainableLine;
import edu.tamu.recognition.constraint.constrainable.ConstrainableShape;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoFeatureExtractor;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import edu.tamu.recognition.paleo.StrokeFeatures;
import edu.tamu.recognition.paleo.multistroke.MultiStrokePaleoRecognizer;
import edu.tamu.recognition.recognizer.IRecognizer;
import gui.MathSketchGUI;

/**
 * Main recognizer
 * 
 * @author Ayden Kim
 * 
 */
public class MathSketchRecognizer implements
IRecognizer<Sketch, IRecognitionResult> {

	private static PaleoSketchRecognizer paleo;
	private static List<Stroke> recognizedStroke = new ArrayList<Stroke>();
	private final DirectionManager directionManager;

	XMLFileFilter xmlFilter = new XMLFileFilter();

	/**
	 * Contains a map where the keys are the name of the expected shape (dir
	 * name) and maps to the list of files that correspond
	 */
	private Map<String, List<File>> shapeMap;

	private final static double CONSTRAINT_CONFIDENCE = .55;
	private final static double DISTANCE_THRESHOLD = 40;
	private final static double DISTANCE = 30;
	private List<AbstractShape> recognizers = new ArrayList<AbstractShape>();
	private MathSketchGUI gui = null;
	private MathSketchAgent agent = null;
	public J48 tree = new J48();         // new instance of tree
	
	/**
	 * Training data
	 */
	Instances m_data = null;
	
	/**
	 * Paleo configuration
	 */
	PaleoConfig m_paleoConfig = PaleoConfig.deepGreenConfig();
	


	private Sketch sketch;

	public MathSketchRecognizer(MathSketchAgent agent) {
		this.agent = agent;
		PaleoConfig config = PaleoConfig.allOff();
		config.setLineTestOn(true);
		config.setCircleTestOn(true);
		//config.setSpiralTestOn(true);
		config.setPolylineTestOn(true);
		//config.setSquareTestOn(true);
		paleo = new PaleoSketchRecognizer(config);
		directionManager = new DirectionManager();
		initializeRecognizer();

	}

	public MathSketchRecognizer() {
		PaleoConfig config = PaleoConfig.allOff();
		config.setLineTestOn(true);
		//config.setCircleTestOn(true);
		//config.setSpiralTestOn(true);
		config.setPolylineTestOn(true);
		//config.setSquareTestOn(true);
		paleo = new PaleoSketchRecognizer(config);
		directionManager = new DirectionManager();
		initializeRecognizer();
	}




	@Override
	public IRecognitionResult recognize() {
		IRecognitionResult resultPaleo = paleo.recognize();

		// print result for debug purposes-----
		Shape recognizedShape = resultPaleo.getBestShape();
		String paleoShapeName = recognizedShape.getInterpretation().label;

		System.out.println("Paleo recognized a " + paleoShapeName);
		// ----

		return resultPaleo;
	}

	public void initializeRecognizer(){
		recognizers.add(new DigitZero());
		recognizers.add(new DigitOne());
		recognizers.add(new DigitTwo());
		recognizers.add(new DigitThree());
		recognizers.add(new DigitFour());
		recognizers.add(new DigitSix());
		recognizers.add(new DigitFive());
		recognizers.add(new DigitSeven1());
		//recognizers.add(new DigitSeven2());
		recognizers.add(new DigitEight());
		recognizers.add(new DigitNine());
	}

	public static void runPaleo(Sketch sketch) {
		// the bits we want to run through paleo
		List<Stroke> strokes = sketch.getStrokes();
		Collections.sort(strokes, Stroke.getTimeComparator());

		for (Stroke s : strokes) {
			paleo.setStroke(s);
			Shape best = paleo.recognize().getBestShape();

			System.out.println("paleo says:\t " + best.getInterpretation());
			prepareAndAdd(best, sketch);
		}
		sketch.removeAll(strokes);
	}

	public static void runPaleoForFeature(Sketch sketch, List<Shape> compare_shape) {
		Stroke stroke = new Stroke();
		for(Shape shape : compare_shape){
			for(Point point : shape.getPoints()){
				stroke.addPoint(point);
			}
		}
		paleo.setStroke(stroke);
		Shape best = paleo.recognize().getBestShape();

		System.out.println("paleo says:\t " + best.getInterpretation());
		prepareAndAdd(best, sketch, compare_shape);
		sketch.remove(stroke);
	}
	public static void runPaleo(Sketch sketch, List<Shape> compare_shape) {
		// the bits we want to run through paleo

		List<Stroke> strokes = sketch.getStrokes();
		Collections.sort(strokes, Stroke.getTimeComparator());

		for (Stroke s : strokes) {
			paleo.setStroke(s);
			Shape best = paleo.recognize().getBestShape();

			System.out.println("paleo says:\t " + best.getInterpretation());
			prepareAndAdd(best, sketch, compare_shape);
		}
		sketch.removeAll(strokes);
	}

	/**
	 * make label lower case, break shape up into atomic components.
	 * 
	 * @param shape
	 * @param container
	 */
	public static void prepareAndAdd(Shape shape, SContainer container, List<Shape> compare_shape) {
		if (shape.getShapes().isEmpty()) {
			shape.getInterpretation().label = shape.getInterpretation().label
			.toLowerCase();
			//compare_shape.add(shape);
			container.add(shape);
		}
		else {
			for (Shape sub : shape.getShapes())

				prepareAndAdd(sub, container, compare_shape);
		}
	}

	/**
	 * make label lower case, break shape up into atomic components.
	 * 
	 * @param shape
	 * @param container
	 */
	public static void prepareAndAdd(Shape shape, SContainer container) {
		if (shape.getShapes().isEmpty()) {
			shape.getInterpretation().label = shape.getInterpretation().label
			.toLowerCase();
			container.add(shape);
		}
		else {
			for (Shape sub : shape.getShapes())

				prepareAndAdd(sub, container);
		}
	}

	private void loadMap(File dir) {
		File[] filesAndDirs = dir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File entry : filesDirs) {
			if (entry.isFile() && xmlFilter.accept(entry)) {
				String shapeName = dir.getName();
				List<File> shapeFiles = shapeMap.get(shapeName);
				if (shapeFiles == null)
					shapeFiles = new ArrayList<File>();
				shapeFiles.add(entry);
				shapeMap.put(shapeName, shapeFiles);
			} else if (entry.isDirectory()) {
				loadMap(entry);
			}
		}
	}

	/**
	 * Using template matching recognition
	 * @param sketch
	 * @return
	 */
	public Double[]  recognizeTemplateMatching(Sketch sketch, List<String> NBestMatch){

		String message = null;

		List<Stroke> strokes = sketch.getStrokes();
		List<Shape> compare_shape = new ArrayList<Shape>();

		if(sketch.getShapes().isEmpty()){
			runPaleo(sketch, compare_shape);
		}
		strokes = sketch.getStrokes();
		List<Shape> shapes = sketch.getShapes();


		final File testDataFolder = new File("trainingData");
		Sketch template_sketch = null;
		//compare_shape = sketch.getShapes();
		List<Shape> list = null;

		List<String> name = new ArrayList<String>();
		List<Double> confidences = new ArrayList<Double>();
		List<Double> confidences1 = new ArrayList<Double>();
		HashMap<Double, String> bestMatchMap = new HashMap<Double, String>();
		HashMap<Double, String> bestMatchMHD = new HashMap<Double, String>();

		// Create training set by reading through all the training files.

		for (File outerfile : testDataFolder.listFiles())
		{
			if(!".DS_Store".equals(outerfile.getName())) // this is a mac problem
			{

				for (File innerfile : outerfile.listFiles())
				{
					try {
						if(!innerfile.getName().startsWith(".")) {
							template_sketch = Sketch.deserialize(innerfile);
							list = template_sketch.getShapes();
							Shape res = new Shape();
							res.addAll(list);

							Shape compare = new Shape();

							if(!compare_shape.isEmpty()){
								compare.addAll(compare_shape);
							}else{
								compare.addAll(sketch.getShapes());
							}

							Shape s1 = res;
							Shape s2 = compare;

							List<Point> points1 = s1.getPoints();

							List<Point> points = s2.getPoints();

							ConstrainableShape cs1 = new ConstrainableShape(s1);
							ConstrainableShape cs2 = new ConstrainableShape(s2);

							ClosedShapeSimilarityConstraint similarity = new ClosedShapeSimilarityConstraint();


							/**
							 * add
							 */

							Shape comp1 = cs1.getParentShape();
							Shape comp2 = cs2.getParentShape();


							BoundingBox b1 = comp1.getBoundingBox();
							BoundingBox b2 = comp2.getBoundingBox();

							similarity.translateToImage(comp1);
							List<Point> p1 = similarity.tailorPoints(comp1);
							List<Point> p2 = similarity.tailorPoints(comp2);

							/**
							 * end
							 */

							double confidence = similarity.solve(p1, p2);
							//double confidence1 = similarity.solveHD(p1, p2);
							//double confidence2 = similarity.solve(p1, p2);
							// double confidence2 = similarity.solveMHD(p1,p2);
							//System.out.println("innerfile.getParent() = " + innerfile.getParent());
							//System.out.println("confidence = " + confidence);
							// confidence = (confidence + confidence1 + confidence2)/3;
							confidences.add(confidence);
							//confidences1.add(confidence1);

							bestMatchMap.put(confidence, innerfile.getParent());
							//bestMatchMHD.put(confidence1, innerfile.getParent());

						}
					} catch (SIMPLTranslationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		Double[] lengths = new Double[confidences.size()];


		for(int i = 0; i < confidences.size(); i++){
			lengths[i] = confidences.get(i);
			//System.out.println(lengths[i]);
		}
		Arrays.sort(lengths);

		for(int i = confidences.size()-1; i > confidences.size()-2; i--){
			System.out.println(lengths[i]);
		}

		NBestMatch.add(bestMatchMap.get(lengths[lengths.length-1]));
		NBestMatch.add(bestMatchMap.get(lengths[lengths.length-2]));


		strokes = sketch.getStrokes();


		return lengths;
	}
	
	/**
	 * Recognize label by classifier
	 * @param sketch
	 * @return
	 * @throws Exception
	 */

	public Double recognizeLabel(Sketch sketch) throws Exception{
		double clsLabel = 0.0;
		trainShapeWithoutLabel(sketch);
/*
		ArffSaver testSaver = new ArffSaver();
		testSaver.setInstances(m_data);
		testSaver.setFile(new File("/Users/skycris/Develop/4kids.arff"));
		testSaver.writeBatch();

		BufferedReader reader = new BufferedReader(
				new FileReader("/Users/skycris/Develop/4kids.arff"));
		Instances testSet = new Instances(reader);
		reader.close();

		// setting class attribute
		// load unlabeled data
		Instances unlabeled = new Instances(
				new BufferedReader(
						new FileReader("/Users/skycris/Develop/4kids.arff")));

		// set class attribute
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
*/
   
		m_data.setClassIndex(m_data.numAttributes() - 1);

		// label instances
		for (int i = 0; i < m_data.numInstances(); i++) {
			Instance ins = m_data.instance(i);
			clsLabel = tree.classifyInstance(m_data.instance(i));
		}
		
		return clsLabel;

	}

	/**
	 * Trains with shapes in the shape file
	 * 
	 * @param shapeFile
	 *            shape file to train
	 * @throws Exception
	 */
	private void trainShapeWithoutLabel(Sketch currentSketch) throws Exception {
		
		List<Stroke> strokes = currentSketch.getStrokes();

		List<Shape> compare_shape = new ArrayList<Shape>();

		if(currentSketch.getShapes().isEmpty()){
			runPaleo(currentSketch, compare_shape);
		}
		List<Shape> shapes = currentSketch.getShapes();
		for (Shape shape: shapes){
			strokes.addAll(shape.getStrokes());
		}

		Stroke stroke = MultiStrokePaleoRecognizer.combineStrokes(strokes);
		int NumPoint = stroke.getNumPoints();
		stroke.setInterpretation("?", 1.0);

		if (stroke.getNumPoints() > 1) {
			PaleoFeatureExtractor pfe = new PaleoFeatureExtractor(
					new StrokeFeatures(stroke, false), m_paleoConfig);
			pfe.computeFeatureVector();  // making features

			// make sure data set exists
			if (m_data == null) {
				m_data = pfe.getDataset();
			}

			m_data.add(pfe.getInstanceWithoutLabel(stroke.getInterpretation().label
					.trim()));
		}

	}

	/**
	 * Using feature based recognition
	 * @param sketch
	 * @return
	 */
	public RecognitionResult recognize(Sketch sketch){

		RecognitionResult recResult = new RecognitionResult();


		List<String> NBestMatch = new ArrayList<String>();
		// try template matching







		Double confidence[] = recognizeTemplateMatching(sketch, NBestMatch);
		System.out.println("NBestMatch = " +  NBestMatch.toString());

		String bestMatch = "";

		bestMatch = NBestMatch.get(0).substring(13);


		


		Shape newShape = new Shape();
		for(Stroke stroke : sketch.getStrokes()){
			newShape.add(stroke);
		}
		newShape.setLabel(bestMatch);
		recResult.addShapeToNBestList(newShape);
		List<Stroke> strokes = sketch.getStrokes();

		List<Shape> shapes = sketch.getShapes();
		sketch.removeAll(shapes);


		/**
		 * low-level recognizer (Geometric recognizer)
		 */


		/*List<recognition.StrokeComponent> components = new ArrayList<recognition.StrokeComponent>();
		for(Stroke stroke : strokes){
			if(recognizedStroke.isEmpty()){
				recognizedStroke.add(stroke);
				components = directionManager.getDirection(stroke);
				createNewShapes(components, sketch,stroke);
			}
			else if(!recognizedStroke.contains(stroke)){

				recognizedStroke.add(stroke);
				components = directionManager.getDirection(stroke);
				System.out.println(components.toString());
				createNewShapes(components, sketch, stroke);
			}
		}



		shapes = sketch.getShapes();



		/**
		 * high-level recognizer (Geometric recognizer)
		 */
		/*List<Shape> groupShapes = new ArrayList<Shape>();
		//List<Shape> shapeComponents = new ArrayList<Shape>(); deleted

		List<List<Shape>> shapeComponents = new ArrayList<List<Shape>>();
		shapeComponents.addAll(combineShapes1(shapes));

		if(shapes.isEmpty()){
			//return false;
		}else{
			shapeComponents.addAll(combineShapes1(shapes)); // find the shapes which are connected
			//shapeComponents.addAll(combineShapes(shapes)); // find the shapes which are connected deleted
		}

		//System.out.println("shapeComponents.size() = " + shapeComponents.size());
		for(List<Shape> combinedShape : shapeComponents){
			for(int i = 0 ; i < recognizers.size(); i++){
				Shape newShape1 = recognizers.get(i).recognize(shapes);
				if(newShape1 != null){
					if(!bestMatch.equals(newShape1.getInterpretation().label)){
						System.out.println("different!!");
					}
				}
				/*if(newShape != null){ deleted
				sketch.removeAll(shapes);
				sketch.add(newShape);

			}*/
		/*}
		}

		agent.getMathSketchGUI().getBackgroundPanel().setHighlightStrokes((ArrayList<Stroke>) recognizedStroke);
		for(Shape shape : sketch.getShapes()){
			agent.getMathSketchGUI().getBackgroundPanel().setHighlightShape(shape);
		}*/

		return recResult;


	}

	/**
	 * find the shapes which are connected
	 * @param shapes - shapes in sketch
	 * @return
	 */
	public static  List<List<Shape>>  combineShapes1(List<Shape> shapes){
		List<List<Shape>> combinedShapes = new ArrayList<List<Shape>>();
		List<Shape> temporaryShapes = new ArrayList<Shape>();
		List<Point> point1 = new ArrayList<Point>();
		List<Point> point2 = new ArrayList<Point>();
		List<Point> temp = shapes.get(0).getPoints();
		
		if(shapes.size() == 1){
			temporaryShapes.add(shapes.get(0));
			combinedShapes.add(temporaryShapes);
			return combinedShapes;
		}

		for(int i = 0; i < shapes.size() - 1; i++){
			Shape shape = shapes.get(i);
			if(temporaryShapes.isEmpty())
				temporaryShapes.add(shape);
			point1 = shape.getPoints();
			for(int j = i+1; j < shapes.size(); j++){
				Shape shape1 = shapes.get(j);

				BoundingBox box = shape.getBoundingBox(); 
				point2 = shape1.getPoints();				

				for(Point comp1 : point1){
					for(Point comp2 : point2){						
						//System.out.println(comp1.distance(comp2));
						if(comp1.distance(comp2) <= DISTANCE){
							if(!temporaryShapes.contains(shape))
								temporaryShapes.add(shape);
							if(!temporaryShapes.contains(shape1))
								temporaryShapes.add(shape1);
						}
					}
				}


			}

			if(i == shapes.size()-2){
				combinedShapes.add(temporaryShapes);
			}

		}


		System.out.println("combinedShapes = " + combinedShapes.toString());
		return combinedShapes;

	}

	/**
	 * find the shapes which are connected
	 * @param shapes - shapes in sketch
	 * @return
	 */
	public static List<Shape>  combineShapes(List<Shape> shapes){
		List<Shape> combinedShapes = new ArrayList<Shape>();

		List<Point> points1 = new ArrayList<Point>();
		List<Point> points2 = new ArrayList<Point>();

		Shape compare1 = null;
		Shape compare2 = null;
		boolean check = true;

		for(int i = 0; i < shapes.size() ; i++){
			compare1 = shapes.get(i);
			for(int j = 1; j < shapes.size(); j++){
				compare2 = shapes.get(j);
				if(!compare1.equals(compare2)){
					for(Shape compare : combinedShapes){
						if(compare.equals(compare2)){
							check =false;
						}		
					}

					if(check){
						// check the distance between each point in shape
						List<Point> tempPoint = compare1.getPoints();
						List<Point> tempPoint1 = compare2.getPoints();
						//System.out.println("distance = " + tempPoint.get(tempPoint.size()-1).distance(tempPoint1.get(0)));
						double distance = tempPoint.get(tempPoint.size()-1).distance(tempPoint1.get(0));
						if(distance <= DISTANCE){
							if(!combinedShapes.contains(compare1)) combinedShapes.add(compare1);
							if(!combinedShapes.contains(compare2)) combinedShapes.add(compare2);
							break;
						}
						check = true;
					}
				}
			}
		}

		return combinedShapes;
	}

	

	public Direction checkLoop(StrokeComponent component, Shape shape, List<Point> points){
		List<Point> shape_points = shape.getPoints(); 
		List<Integer> compared_points = new ArrayList<Integer>();
		double length = shape.getFirstStroke().getPathLength();
		Point start_point = null; 
		Point end_point = null; 
		Shape new_shape = null;
		double index_threshold = Math.floor(shape_points.size() * 0.2); // shape_points.size() * 0.15; 5
		double distance_threshold = 20.0; //length / 10;
		double minimum = Double.MAX_VALUE;

		
		for(int i = 0; i < shape_points.size() ; i++){
			start_point = shape_points.get(i);
			for(int j = i + (int)Math.floor(index_threshold) ; j < shape_points.size(); j++){
				end_point = shape_points.get(j);
				if(!compared_points.contains(i) && !compared_points.contains(j)){
					if(minimum > start_point.distance(end_point)){
						minimum = start_point.distance(end_point);
					}
					//System.out.println(start_point.distance(end_point));
					if(start_point.distance(end_point) <= distance_threshold){
						if(compared_points.size() == 0){
							if(Math.abs(i-j) > 2){
								compared_points.add(i);
								compared_points.add(j);
								//System.out.println("angle = " + component.points_angles.get(i) + " and " + component.points_angles.get(j));
								i = i + (int)Math.floor(index_threshold);
							}
						}else{
							if(compared_points.get(compared_points.size()-1) - j >= index_threshold){

								compared_points.add(i);
								compared_points.add(j);
								//System.out.println("angle = " + component.points_angles.get(i) + " and " + component.points_angles.get(j));
								i = i + (int)Math.floor(index_threshold);

							}
						}
					}		
				}
			}


		}


		//System.out.println("minimum = " + minimum);
		if(compared_points.size() >= 2)
			return Direction.loop;
		else
			return null;
		//return new_shape;
	}


	/**
	 * create new shapes using stroke components
	 * @param components - have points and directions
	 * @param sketch
	 */
	public void createNewShapes(List<StrokeComponent> components, Sketch sketch, Stroke stroke){
		Stroke newStroke = null;
		Point p1 = null;
		Point p2 = null;



		/*List<StrokeComponent> new_components = checkDividedShape(components, sketch, stroke);
		if(new_components.size() != components.size()){ // it means the shape can be divided into more than two shapes
			components.clear();
			components = new_components;
		} */

		List<Point> points = stroke.getPoints();
		for(int i = 0; i < components.size(); i++){
			StrokeComponent component = components.get(i);
			newStroke = new Stroke();
			//make a new stroke
			for(int j = component.points_index.get(0) ; j <= component.points_index.get(1); j++){		
				newStroke.addPoint(points.get(j));
			}
			Shape shape = new Shape();
			shape.add(newStroke);
			String direction = component.direction.toString();


			// check if down or up will contain loop
			if(direction.equals("down") || (direction.equals("up"))){
				// new_shape = checkLoop(component, shape, points);

				Direction new_direction = checkLoop(component, shape, points);
				if(!direction.equals(new_direction) && new_direction != null)
					component.direction = new_direction;
			}
			shape.setLabel(component.direction.toString());
			sketch.add(shape);
			System.out.println("added");
		}

		for(StrokeComponent component : components){
			//System.out.println(component.direction.toString());	
		}

	}

	/**
	 * Check if the shape can be divided into two shapes
	 * @param components
	 * @param sketch2
	 * @param stroke
	 */
	private List<StrokeComponent>  checkDividedShape(List<StrokeComponent> components,
			Sketch sketch2, Stroke stroke) {

		List new_shapes = new ArrayList<Shape>();
		List<StrokeComponent> tempComponents = new ArrayList<StrokeComponent>();
		StrokeComponent newComponent = null;
		StrokeComponent newComponent1 = null;
		List<Integer> point_index = null;
		for(StrokeComponent component : components){
			tempComponents.add(component);
		}

		Stroke compareStroke = new Stroke();
		List<Point> points = stroke.getPoints();


		for(int i = 0 ; i < components.size() ; i++){
			StrokeComponent component = components.get(i);
			Point endPoint = points.get(component.points_index.get(1));
			String direction = component.direction.toString();

			for(int j = component.points_index.get(0) ; j < (component.points_index.get(1) / 2); j++){		
				compareStroke.addPoint(points.get(j));
			}

			// check if the curved line can be divided into a curved line and a loop
			if(direction.equals("down") || direction.equals("up")){
				//check the distance between first point and end point
				if(points.get(component.points_index.get(0)).distance(points.get(component.points_index.get(1))) > DISTANCE){
					for(int j = 0; j < compareStroke.getPoints().size(); j++){
						Point point = compareStroke.getPoints().get(j);
						if(point.distance(endPoint) <= 10){
							//System.out.println("divide distance = " + point.distance(endPoint) + " point = " + point + " end point = " + endPoint);

							tempComponents.remove(i);
							newComponent = new StrokeComponent();
							newComponent.points_index.add(0);
							newComponent.points_index.add(j+1);

							Point tempPoint = points.get(newComponent.points_index.get(1));
							newComponent.direction = component.direction;
							tempComponents.add(newComponent);

							newComponent1 = new StrokeComponent();
							newComponent1.points_index.add(j+2);
							newComponent1.points_index.add(compareStroke.getPoints().size()-1);
							newComponent1.direction = Direction.loop;
							tempComponents.add(newComponent1);	
							break;
						}

					}
				}else{


				}
			}


		}

		return tempComponents;



	}


	@Override
	public void submitForRecognition(Sketch sketch) {

		setSketch(sketch);

	}

	public Sketch getSketch() {
		return sketch;
	}

	public void setSketch(Sketch sketch) {
		this.sketch = sketch;
		paleo.setStroke(sketch.getLastStroke());
	}

	/**
	 * Make classifier
	 * @throws Exception 
	 */

	public J48 trainClassifier() throws Exception{

		String[] options = new String[1];
		options[0] = "-U";            // unpruned tree
		
		try {
			tree.setOptions(options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     // set the options



		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new FileReader("/Users/skycris/Dropbox/childrenRecognizer/data/ToddlerAnd7.arff"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instances trainSet = null;
		try {
			trainSet = new Instances(reader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reader.close();
		// setting class attribute
		trainSet.setClassIndex(trainSet.numAttributes() - 1);
		System.out.println(trainSet.numAttributes() - 1);

		tree.buildClassifier(trainSet);   // build classifier
		return tree;


	}
}
