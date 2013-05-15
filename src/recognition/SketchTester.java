package recognition;
/**
 * SketchTester.java
 * 
 * Revision History:<br>
 * Feb 17, 2009 bpaulson - File created
 * 
 * <p>
 * 
 * <pre>
 * This work is released under the BSD License:
 * (C) 2008 Sketch Recognition Lab, Texas A&amp;M University (hereafter SRL @ TAMU)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&amp;M University 
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ladder.core.config.LadderConfig;
import org.ladder.io.DOMInput;
import org.ladder.io.ShapeDirFilter;
import org.ladder.io.XMLFileFilter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoFeatureExtractor;
import edu.tamu.recognition.paleo.StrokeFeatures;
import edu.tamu.recognition.paleo.multistroke.MultiStrokePaleoRecognizer;

/**
 * Trainer for the neural network (creates the ARFF file)
 * 
 * @author Ayden Kim
 */
public class SketchTester {

	/**
	 * Training data
	 */
	Instances m_data = null;

	/**
	 * Paleo configuration
	 */
	PaleoConfig m_paleoConfig = PaleoConfig.deepGreenConfig();

	/**
	 * Train data file location
	 */

	File m_trainData = null;

	/**
	 * Test data file location
	 */
	File m_testData = null;

	/**
	 * Sketch object
	 */
	Sketch m_sketch = new Sketch();

	/**
	 * Input file reader
	 */
	DOMInput m_input = new DOMInput();

	/**
	 * Set of unlabeled files encountered
	 */
	Set<String> m_unlabeledExamples = new TreeSet<String>();

	/**
	 * List of unknown primitive labels encountered (primitive is key, list of
	 * file names is value)
	 */
	Map<String, List<String>> m_unknownPrimitives = new HashMap<String, List<String>>();

	/**
	 * Default Constructor
	 * 
	 * @throws Exception
	 */
	public SketchTester() throws Exception {

		String[] options = new String[1];
		options[0] = "-U";            // unpruned tree
		J48 tree = new J48();         // new instance of tree
		tree.setOptions(options);     // set the options



		BufferedReader reader = new BufferedReader(
				new FileReader("/Users/skycris/Dropbox/childrenRecognizer/data/ToddlerAnd7.arff"));
		Instances trainSet = new Instances(reader);
		reader.close();
		// setting class attribute
		trainSet.setClassIndex(trainSet.numAttributes() - 1);
		System.out.println(trainSet.numAttributes() - 1);

		tree.buildClassifier(trainSet);   // build classifier

		//trainSet.setClassIndex(trainSet.numAttributes() - 1);
		// get the list of individual shape directories
		m_trainData = new File("/Users/skycris/Dropbox/childrenRecognizer/data/2012 winter/");
		// get the list of individual shape directories
		File[] shapeDirs = m_trainData.listFiles(new ShapeDirFilter());

		// loop through all shape directories
		for (File shapeDir : shapeDirs) {
			System.out.println(shapeDir.toString());
			File[] shapeFiles = shapeDir.listFiles(new XMLFileFilter());

			// loop through all shape files in the directory
			for (File shapeFile : shapeFiles) {
				trainShapeWithoutLabel(shapeFile);

				ArffSaver testSaver = new ArffSaver();
				testSaver.setInstances(m_data);
				testSaver.setFile(new File("/Users/skycris/Develop/4kids.arff"));
				testSaver.writeBatch();

				reader = new BufferedReader(
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

				// create copy
				Instances labeled = new Instances(unlabeled);

				int num = labeled.numInstances();
				// label instances
				for (int i = 0; i < labeled.numInstances(); i++) {
					Instance ins = labeled.instance(i);
					double clsLabel = tree.classifyInstance(labeled.instance(i));
					if(clsLabel == 0.0){
						System.out.println("Toddler");
					}else{
						System.out.println("School Age");
					}
				}

			}
			//}
		}



	}

	/**
	 * Trains with shapes in the shape file
	 * 
	 * @param shapeFile
	 *            shape file to train
	 * @throws Exception
	 */
	private void trainShape(File shapeFile) throws Exception {

		m_sketch = Sketch.deserialize(shapeFile);
		List<Stroke> strokes = new ArrayList<Stroke>();

		/**
		 * Combine all shape into one stroke
		 */
		for(Shape shape : m_sketch.getRecursiveShapes()){
			strokes.addAll(shape.getStrokes());
		}

		Stroke stroke = MultiStrokePaleoRecognizer.combineStrokes(strokes);
		int NumPoint = stroke.getNumPoints();
		stroke.setInterpretation("Toddler", 1.0);

		if (stroke.getNumPoints() > 1) {
			//System.out.println("Analyzing shape " + i + " of "
			//	+ shapeFile.getName());
			PaleoFeatureExtractor pfe = new PaleoFeatureExtractor(
					new StrokeFeatures(stroke, false), m_paleoConfig);
			pfe.computeFeatureVector();

			// make sure data set exists
			if (m_data == null) {
				m_data = pfe.getDataset();
			}

			m_data.add(pfe.getInstance(stroke.getInterpretation().label
					.trim()));
		}

	}

	/**
	 * Trains with shapes in the shape file
	 * 
	 * @param shapeFile
	 *            shape file to train
	 * @throws Exception
	 */
	private void trainShapeWithoutLabel(File shapeFile) throws Exception {

		m_sketch = Sketch.deserialize(shapeFile);
		List<Stroke> strokes = new ArrayList<Stroke>();

		/**
		 * Combine all shape into one stroke
		 */
		for(Shape shape : m_sketch.getRecursiveShapes()){
			strokes.addAll(shape.getStrokes());
		}

		Stroke stroke = MultiStrokePaleoRecognizer.combineStrokes(strokes);
		int NumPoint = stroke.getNumPoints();
		stroke.setInterpretation("?", 1.0);

		if (stroke.getNumPoints() > 1) {
			PaleoFeatureExtractor pfe = new PaleoFeatureExtractor(
					new StrokeFeatures(stroke, false), m_paleoConfig);
			pfe.computeFeatureVector();

			// make sure data set exists
			if (m_data == null) {
				m_data = pfe.getDataset();
			}

			m_data.add(pfe.getInstanceWithoutLabel(stroke.getInterpretation().label
					.trim()));
		}

	}



	/**
	 * Checks to make sure the given label is a valid PaleoSketch primitive
	 * 
	 * @param label
	 *            label to check
	 * @return true if valid, else false
	 */
	private boolean isValidLabel(String label) {
		for (int i = 0; i < m_paleoConfig.getShapesTurnedOn().size(); i++) {
			String prim = m_paleoConfig.getShapesTurnedOn().get(i);

			// startsWith is a check for Polyline (x)
			if (prim.compareTo(label) == 0 || label.startsWith(prim + " "))
				return true;
		}
		return false;
	}

	/**
	 * Convert a string label into an integer
	 * 
	 * @param label
	 *            label
	 * @return integer version of label
	 * @throws Exception
	 *             if bad label
	 */
	/*
	 * private int labelToInt(String label) throws Exception { label =
	 * label.trim(); if (label.equals(Fit.ARC)) return 0; if
	 * (label.equals(Fit.ARROW)) return 1; if (label.equals(Fit.CIRCLE)) return
	 * 2; if (label.equals(Fit.COMPLEX)) return 3; if (label.equals(Fit.CURVE))
	 * return 4; if (label.equals(Fit.DIAMOND)) return 5; if
	 * (label.equals(Fit.DOT)) return 6; if (label.equals(Fit.ELLIPSE)) return
	 * 7; if (label.equals(Fit.GULL)) return 8; if (label.equals(Fit.HELIX))
	 * return 9; if (label.equals(Fit.INFINITY)) return 10; if
	 * (label.equals(Fit.LINE)) return 11; if (label.equals(Fit.NBC)) return 12;
	 * if (label.equals(Fit.POLYGON)) return 13; if (label.equals(Fit.POLYLINE))
	 * return 14; if (label.equals(Fit.RECTANGLE)) return 15; if
	 * (label.equals(Fit.SPIRAL)) return 16; if (label.equals(Fit.SQUARE))
	 * return 17; if (label.equals(Fit.WAVE)) return 18; throw new
	 * Exception("bad label: [" + label + "]"); }
	 */

	/**
	 * Converts ARFF instances into a format readable by LibSVM and outputs it
	 * to file
	 * 
	 * @param filename
	 *            output file name
	 * @param data
	 *            ARFF instances (the data)
	 * @throws IOException
	 *             if output fails
	 */
	public static void libSVMToFile(String filename, Instances data)
	throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < data.numInstances(); i++) {
			Instance inst = data.instance(i);
			double label = inst.value(inst.numValues() - 1);
			writer.write(label + "\t");
			for (int a = 0; a < inst.numValues() - 1; a++) {
				double value = inst.value(a);
				if (Double.isInfinite(value) || Double.isNaN(value))
					value = mean(data.attributeToDoubleArray(a));
				writer.write((a + 1) + ":" + value);
				if (a < inst.numValues() - 2)
					writer.write(" ");
			}
			writer.newLine();
		}
	}

	/**
	 * Calculate the mean of an array of values (skips inf and NaN)
	 * 
	 * @param values
	 * @return
	 */
	public static double mean(double[] values) {
		double sum = 0.0;
		double num = 0.0;
		for (int i = 0; i < values.length; i++) {
			if (Double.isInfinite(values[i]) || Double.isNaN(values[i]))
				continue;
			sum += values[i];
			num++;
		}
		return sum / num;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SketchTester t = new SketchTester();
	}

}
