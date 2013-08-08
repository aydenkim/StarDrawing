package model;

import edu.tamu.core.sketch.Sketch;

/**
 * Recording student's answer
 * @author Ayden Kim
 *
 */
public class Answer {
   int currentQuestionNumber;
   long time;
   long errorRate;
   Sketch sketch;
   
   public Answer(){
	   
   }
   
   public Answer(int currentQuestionNumber){
	   this.currentQuestionNumber = currentQuestionNumber;
   }
   
   public void setCurrentQuestionNumber(int number){
	   this.currentQuestionNumber = number;
   }
   
   public int getCurrentQuestionNumber(){
	   return currentQuestionNumber;
   }
   
   public void setTime(long time){
	   this.time = time;
   }
   
   public void setErrorRate(long time){
	   this.errorRate = errorRate;
   }
   
   public long getTime(){
	   return time;
   }
   
   public long getErrorRate(){
	   return errorRate;
   }
   
   public void setSketch(Sketch sketch){
	   this.sketch = sketch.clone();
   }
   
   public Sketch getSketch(){
	   return sketch;
   }
   
   
}
