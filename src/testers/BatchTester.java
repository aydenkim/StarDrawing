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

public class BatchTester {

	public static void main(String[] args){
		final File trainingDataFolder = new File("trainingData");
		final File testDataFolder = new File("/Users/skycris/Dropbox/childrenRecognizer/data/2012 winter/AGE8-F2");

		int[] incorrect = new int[16];
		double[] timeToDraw = new double[16];
		int[] pointsSize = new int[16];;
		int[] total = new int[16];
		double[] strokeLength = new double[16];
		List<Point> points = new ArrayList<Point>();


		List<Stroke> strokes = new ArrayList<Stroke>();
		MathSketchRecognizer recognizer = null;

		Calendar now = Calendar.getInstance();

		long milliseconds1 = now.getTimeInMillis();

		// Create training set by reading through all the training files.
		double timeGap = 0.0;
		double strokePathLength = 0.0;
		
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

				/*for (File innerfile : outerfile.listFiles())
				{
					if(!".DS_Store".equals(innerfile.getName())){
						//int index = innerfile.getParent().indexOf("/");
						//String expectedShape = innerfile.getParent().substring(index+1);*/
						try {
							Sketch sketch = Sketch.deserialize(outerfile);
							List<Shape> shapes = sketch.getShapes();
							
							points.clear();
							
							for(Shape shape : shapes){
								points.addAll(shape.getPoints());
							}
				            
							double minTime = points.get(0).getTime();
							double maxTime = points.get(0).getTime();
							
							for(int i = 1 ; i < points.size(); i++){
								if(minTime > points.get(i).getTime()){
									minTime = points.get(i).getTime();
								}
								if(maxTime < points.get(i).getTime()){
									maxTime = points.get(i).getTime();
								}
							}
							timeGap = timeGap + maxTime - minTime;
							
							
							recognizer = new MathSketchRecognizer();
							if(sketch.getStrokes().isEmpty()){
								for(Shape shape : sketch.getShapes()){
									strokes.addAll(shape.getStrokes());
								}
							}

							if(!strokes.isEmpty()){
								sketch.setStrokes(strokes);
							}
							
							for(Stroke stroke : strokes){
								strokePathLength = strokePathLength + stroke.getPathLength();
							}
							

							System.out.println("timeGap = " + timeGap);
							System.out.println("strokePathLength " + strokePathLength);
							
							/*
							recognizer.setSketch(sketch);
							IRecognitionResult result = recognizer.recognize(sketch);
							System.out.println("expected shape " + expectedShape);
							Shape bestMatchedShape = result.getBestShape();
							
							// time calculating
							if(expectedShape.equals("one")){
								timeToDraw[1] = timeToDraw[1] + timeGap;
								strokeLength[1] = strokeLength[1] + strokePathLength; 
								pointsSize[1] = pointsSize[1] + points.size();
							}else if(expectedShape.equals("two")){
								timeToDraw[2] = timeToDraw[2] + timeGap;
								strokeLength[2] = strokeLength[2] + strokePathLength; 
								pointsSize[2] = pointsSize[2] + points.size();
							}else if(expectedShape.equals("three")){
								timeToDraw[3] = timeToDraw[3] + timeGap;
								strokeLength[3] = strokeLength[3] + strokePathLength; 
								pointsSize[3] = pointsSize[3] + points.size();
							}else if(expectedShape.equals("four")){
								timeToDraw[4] = timeToDraw[4] + timeGap;
								strokeLength[4] = strokeLength[4] + strokePathLength; 
								pointsSize[4] = pointsSize[4] + points.size();
							}else if(expectedShape.equals("five")){
								timeToDraw[5] = timeToDraw[5] + timeGap;
								strokeLength[5] = strokeLength[5] + strokePathLength; 
								pointsSize[5] = pointsSize[5] + points.size();
							}else if(expectedShape.equals("six")){
								timeToDraw[6] = timeToDraw[6] + timeGap;
								strokeLength[6] = strokeLength[6] + strokePathLength; 
								pointsSize[6] = pointsSize[6] + points.size();
							}else if(expectedShape.equals("seven")){
								timeToDraw[7] = timeToDraw[7] + timeGap;
								strokeLength[7] = strokeLength[7] + strokePathLength; 
								pointsSize[7] = pointsSize[7] + points.size();
							}else if(expectedShape.equals("eight")){
								timeToDraw[8] = timeToDraw[8] + timeGap;
								strokeLength[8] = strokeLength[8] + strokePathLength; 
								pointsSize[8] = pointsSize[8] + points.size();
							}else if(expectedShape.equals("nine")){
								timeToDraw[9] = timeToDraw[9] + timeGap;
								strokeLength[9] = strokeLength[9] + strokePathLength; 
								pointsSize[9] = pointsSize[9] + points.size();
							}else if(expectedShape.equals("zero")){
								timeToDraw[0] = timeToDraw[0] + timeGap;
								strokeLength[0] = strokeLength[0] + strokePathLength; 
								pointsSize[0] = pointsSize[0] + points.size();
							}else if(expectedShape.equals("A")){
								timeToDraw[10] = timeToDraw[10] + timeGap;
								strokeLength[10] = strokeLength[10] + strokePathLength; 
								pointsSize[10] = pointsSize[10] + points.size();
							}else if(expectedShape.equals("B")){
								timeToDraw[11] = timeToDraw[11] + timeGap;
								strokeLength[11] = strokeLength[11] + strokePathLength; 
								pointsSize[11] = pointsSize[11] + points.size();
							}else if(expectedShape.equals("C")){
								timeToDraw[12] = timeToDraw[12] + timeGap;
								strokeLength[12] = strokeLength[12] + strokePathLength; 
								pointsSize[12] = pointsSize[12] + points.size();
							}else if(expectedShape.equals("D")){
								timeToDraw[13] = timeToDraw[13] + timeGap;
								strokeLength[13] = strokeLength[13] + strokePathLength; 
								pointsSize[13] = pointsSize[13] + points.size();
							}else if(expectedShape.equals("E")){
								timeToDraw[14] = timeToDraw[14] + timeGap;
								strokeLength[14] = strokeLength[14] + strokePathLength; 
								pointsSize[14] = pointsSize[14] + points.size();
							}else if(expectedShape.equals("F")){
								timeToDraw[15] = timeToDraw[15] + timeGap;
								strokeLength[15] = strokeLength[15] + strokePathLength; 
								pointsSize[15] = pointsSize[15] + points.size();
							}
							
							if(!expectedShape.endsWith(bestMatchedShape.getInterpretation().label)){
								if(expectedShape.equals("one")){
									incorrect[1] = incorrect[1] + 1;									
								}else if(expectedShape.equals("two")){
									incorrect[2] = incorrect[2] + 1;						
								}else if(expectedShape.equals("three")){
									incorrect[3] = incorrect[3] + 1;									
								}else if(expectedShape.equals("four")){
									incorrect[4] = incorrect[4] + 1;								
								}else if(expectedShape.equals("five")){
									incorrect[5] = incorrect[5] + 1;							
								}else if(expectedShape.equals("six")){
									incorrect[6] = incorrect[6] + 1;									
								}else if(expectedShape.equals("seven")){
									incorrect[7] = incorrect[7] + 1;									
								}else if(expectedShape.equals("eight")){
									incorrect[8] = incorrect[8] + 1;									
								}else if(expectedShape.equals("nine")){
									incorrect[9] = incorrect[9] + 1;								
								}else if(expectedShape.equals("zero")){
									incorrect[0] = incorrect[0] + 1;								
								}else if(expectedShape.equals("A")){
									incorrect[10] = incorrect[10] + 1;									
								}else if(expectedShape.equals("B")){
									incorrect[11] = incorrect[11] + 1;								
								}else if(expectedShape.equals("C")){
									incorrect[12] = incorrect[12] + 1;									
								}else if(expectedShape.equals("D")){
									incorrect[13] = incorrect[13] + 1;									
								}else if(expectedShape.equals("E")){
									incorrect[14] = incorrect[14] + 1;
								}else if(expectedShape.equals("F")){
									incorrect[15] = incorrect[15] + 1;
								}
								
								//System.out.println("not equals    " + innerfile.toString() + "   recognized as " + bestMatchedShape.getInterpretation().label);
							}else{
								
							}
							*/


						} catch (SIMPLTranslationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


				//	}
				//}

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
			System.out.println("Time taken of " + i + " is " + timeToDraw[i]/total[i] + " point size " + pointsSize[i]/total[i] + " path length " + strokeLength[i]/total[i]);
		}
		for(int i = 0; i < total.length; i++){
			total_cnt = total_cnt + total[i];
		}

		System.out.println("total correctness " + (total_cnt - total_incorrectness)/total_cnt + " total = " + total_cnt);

	}
}

