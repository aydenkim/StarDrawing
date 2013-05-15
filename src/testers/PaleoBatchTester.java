package testers;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import recognition.MathSketchRecognizer;

import ecologylab.serialization.SIMPLTranslationException;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.IRecognitionResult;

public class PaleoBatchTester {

	public static void main(String[] args){
		final File trainingDataFolder = new File("trainingData");
		final File testDataFolder = new File("childrenData");

		int[] incorrect = new int[16];
		int[] total = new int[16];


		List<Stroke> strokes = new ArrayList<Stroke>();
		MathSketchRecognizer recognizer = null;

		Calendar now = Calendar.getInstance();

		long milliseconds1 = now.getTimeInMillis();

		// Create training set by reading through all the training files.

		for (File outerfile : testDataFolder.listFiles())
		{
			if(!".DS_Store".equals(outerfile.getName())) // this is a mac problem
			{

				int index = outerfile.getName().indexOf("/");
				String expectedShape = outerfile.getName().substring(index+1);
				if(expectedShape.equals("one")){
					total[1] = outerfile.listFiles().length;
				}else if(expectedShape.equals("two")){
					total[2] = outerfile.listFiles().length;
				}else if(expectedShape.equals("three")){
					total[3] = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("four")){
					total[4]  = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("five")){
					total[5] = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("six")){
					total[6]  = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("seven")){
					total[7]  = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("eight")){
					total[8]  = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("nine")){
					total[9]  = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("zero")){
					total[0] = outerfile.listFiles().length;
				}
				else if(expectedShape.equals("A")){
					total[10] = outerfile.listFiles().length;
				}else if(expectedShape.equals("B")){
					total[11]  = outerfile.listFiles().length;
				}else if(expectedShape.equals("C")){
					total[12]  = outerfile.listFiles().length;
				}else if(expectedShape.equals("D")){
					total[13]  = outerfile.listFiles().length;
				}else if(expectedShape.equals("E")){
					total[14]  = outerfile.listFiles().length;
				}else if(expectedShape.equals("F")){
					total[15]  = outerfile.listFiles().length;
				}

				for (File innerfile : outerfile.listFiles())
				{
					if(!".DS_Store".equals(innerfile.getName())){
						//int index = innerfile.getParent().indexOf("/");
						//String expectedShape = innerfile.getParent().substring(index+1);
						try {
							Sketch sketch = Sketch.deserialize(innerfile);
							
							recognizer = new MathSketchRecognizer();
							System.out.println("Shape name " + outerfile.getName());
							recognizer.runPaleoForFeature(sketch, sketch.getShapes());
							
							

						} catch (SIMPLTranslationException e) {
							System.out.println(innerfile.getName());
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					}
				}

			}
		}

		now = Calendar.getInstance();
		
		long milliseconds2 = now.getTimeInMillis();

		long difference = milliseconds2 - milliseconds1;

		System.out.println("time takes " + difference);

		Double total_incorrectness = 0.0;
		Double total_cnt = 0.0;

		for(int i = 0; i < total.length; i++){
			total_incorrectness = total_incorrectness + incorrect[i];
			System.out.println("correctness of " + i + "  is " +  (Double.parseDouble(Integer.toString(total[i] - incorrect[i])) / total[i])  + " correct is " + (total[i]- incorrect[i]) + "  total is " + total[i] );
		}
		for(int i = 0; i < total.length; i++){
			total_cnt = total_cnt + total[i];
		}

		System.out.println("total correctness " + (total_cnt - total_incorrectness)/total_cnt);



		/*System.out.println("Correctness of one " + Double.parseDouble(Integer.toString(one_correct)) / 20.0);
		System.out.println("Correctness of two " + Double.parseDouble(Integer.toString(two_correct)) / 20.0);
		System.out.println("Correctness of three " + Double.parseDouble(Integer.toString(three_correct)) / 20.0);
		System.out.println("Correctness of four " + Double.parseDouble(Integer.toString(four_correct)) / 20.0);
		System.out.println("Correctness of five " + Double.parseDouble(Integer.toString(five_correct)) / 20.0);
		System.out.println("Correctness of six " + Double.parseDouble(Integer.toString(six_correct)) / 20.0);
		System.out.println("Correctness of seven " + Double.parseDouble(Integer.toString(seven_correct)) / 20.0);
		System.out.println("Correctness of eight " + Double.parseDouble(Integer.toString(eight_correct)) / 20.0);
		System.out.println("Correctness of nine " + Double.parseDouble(Integer.toString(nine_correct)) / 20.0);
		System.out.println("Correctness of zero " + Double.parseDouble(Integer.toString(zero_correct)) / 20.0);
		System.out.println("Correctness of A " + Double.parseDouble(Integer.toString(A_correct)) / 20.0);
		System.out.println("Correctness of B " + Double.parseDouble(Integer.toString(B_correct)) / 13.0);
		System.out.println("Correctness of C " + Double.parseDouble(Integer.toString(C_correct)) / 20.0);
		System.out.println("Correctness of E " + Double.parseDouble(Integer.toString(E_correct)) / 19.0);

		int total = one_total+three_total+two_total+four_total+five_total+six_total+seven_total+eight_total+nine_total+zero_total;
		int total_correct = one_correct+three_correct+two_correct+four_correct+five_correct+six_correct+seven_correct+eight_correct+nine_correct+zero_correct+A_correct+B_correct+C_correct+E_correct;
		System.out.println("Total accuracy " + Double.parseDouble(Integer.toString(total_correct)) / Double.parseDouble(Integer.toString(272)) + " total = " + total + " correctness = " + total_correct);*/
	}
}

