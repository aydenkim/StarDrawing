package testers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import recognition.MathSketchRecognizer;

import ecologylab.serialization.SIMPLTranslationException;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.IRecognitionResult;

public class ScribbleBatchTester {
	/**
	 * entropy set from Akshay
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static String getEntropyValue(Point p0, Point p1, Point c){


		if((p0.x == c.x) || (p1.x == c.x)  ){
			//return Double.MAX_VALUE;
		}
		double AB = c.distance(p0);
		double BC = c.distance(p1);

		Point vector1 = new Point(p0.x - c.x, p0.y - c.y);
		Point vector2 = new Point(p1.x - c.x, p1.y - c.y);

		double factor2 = (p1.x - c.x)*(p0.x - c.x);
		double factor3 = (p1.y - c.y)*(p0.y - c.y);
		double result = Math.acos(    (factor3+factor2) /(AB*BC));

		double angle = Math.toDegrees(result);

		return getEntropy(angle);

	}

	public static String getEntropy(double angle){
		if(angle >= 0 && angle < 30){
			return "A";
		}else if(angle >= 30 && angle < 60){
			return "B";
		}else if(angle >= 60 && angle < 90){
			return "C";
		}else if(angle >= 90 && angle < 120){
			return "D";
		}else if(angle >= 120 && angle < 150){
			return "E";
		}else if(angle >= 150 && angle <= 180){
			return "F";
		}

		return "X";
	}

	public static void main(String[] args) throws SIMPLTranslationException{
		final File trainingDataFolder = new File("/Users/skycris/Documents/workspace/Mechanix Scribble/208/2633");


 

		List<Stroke> strokes = new ArrayList<Stroke>();
		MathSketchRecognizer recognizer = null;

		Calendar now = Calendar.getInstance();
		List<Shape> shapes = new ArrayList<Shape>();
		List<Point> points = new ArrayList<Point>();
		List<String> entropy = new ArrayList<String>();

		long milliseconds1 = now.getTimeInMillis();

		// Create training set by reading through all the training files.

		for (File outerfile : trainingDataFolder.listFiles())
		{
			if(!".DS_Store".equals(outerfile.getName())) // this is a mac problem
			{

				Sketch sketch = Sketch.deserialize(outerfile);
				points = sketch.getPoints();
				if(points.size() > 0){
					for(Shape shape : shapes){
						 // get entropy value
						 for(int i = 1; i < points.size() - 1; i++){
				               entropy.add(getEntropyValue(points.get(i-1),points.get(i+1), points.get(i)));
				         }
						 
						 Set<String> stringSet = new HashSet<String>();

				          for(String s : entropy){
				               stringSet.add(s);
				          }

				          Iterator itr = stringSet.iterator();

				          Double[] countString = new Double[stringSet.size()];
				          for(int i = 0; i < countString.length; i++){
				               countString[i] = 0.0;
				          }
				          List<String> uniqueString = new ArrayList<String>();

				          while(itr.hasNext()){
				               uniqueString.add((String) itr.next());
				          }

				          for(String s : entropy){
				               for(int i = 0; i < uniqueString.size(); i++){
				                    if(uniqueString.get(i).equals(s)){
				                         countString[i] = countString[i] + 1;
				                    }
				               }
				          }
				         
				           double[] probability = new double[entropy.size()];
				         for (int i=0; i < countString.length; i++){
				                 probability[i] = countString[i]/(entropy.size()*1.0);
				         }

				         double sum = 0;
				         for (int i=0; i < countString.length;i++){
				                 if (probability[i] != 0)
				                      sum += probability[i] * Math.log(probability[i]);
				         }

				         System.out.println("entropy " + sum * (-1));
				         
				         // entropy end
						 

					}
				}

			}
		}

		now = Calendar.getInstance();

		long milliseconds2 = now.getTimeInMillis();

		long difference = milliseconds2 - milliseconds1;

	}
}

