package FormatManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.ladder.io.DOMOutput;
import org.ladder.io.SketchFileType;

import recognition.MathSketchRecognizer;

import ecologylab.serialization.SIMPLTranslationException;
import edu.tamu.core.sketch.SContainer;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;

public class XMLManager {
	static PaleoSketchRecognizer paleo = null;
	
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
	
	/**
	 * @param args
	 * @throws SIMPLTranslationException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws SIMPLTranslationException, FileNotFoundException, ParserConfigurationException, IOException {
		// TODO Auto-generated method stub
		DOMOutput out = new DOMOutput();
		
		File innerfile = new File("/Users/skycris/Develop/workspace/math-sketch/trainingData/A/23335.xml");
		File outfile = new File("/Users/skycris/Develop/workspace/math-sketch/test.xml");
		
		Sketch sketch = Sketch.deserialize(innerfile);
		
		
		PaleoConfig config = PaleoConfig.allOff();
		config.setLineTestOn(true);
		config.setCircleTestOn(true);
		config.setSpiralTestOn(true);
		config.setPolylineTestOn(true);
		config.setSquareTestOn(true);

		paleo = new PaleoSketchRecognizer(config);
		
		runPaleo(sketch);
		
		List<Shape> shapes = sketch.getShapes();
		
		out.toFile(sketch.toISketch(), SketchFileType.SRL, outfile);
		
	}

}
