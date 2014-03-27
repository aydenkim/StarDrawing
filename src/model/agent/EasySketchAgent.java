/**
 * Agent.java
 * 
 * Revision History:<br>
 * Nov 3, 2011 fvides - File created
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

package model.agent;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import common.CSVWrite;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import model.Answer;
import model.AnswerChecker;
import model.InteractionState;
import model.MessageFamily;
import model.MessageManager;
import model.Question;
import model.StateManager;
import model.agent.ReadExcel.ExcelContents;
import recognition.EasySketchRecognizer;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.formatenums.Format;
import edu.tamu.core.gui.ISketchObserver;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.IRecognitionResult;
import gui.EasySketchController;
import gui.EasySketchGUI;
import common.IntersectionSegmenter;

/**
 * The EasySketchAgent is the main contribution of the IUI project and contains the
 * AI of the interactive agent that gives instructions and feedback to the kid
 * 
 * @author fvides
 * 
 */
public class EasySketchAgent implements ISketchObserver {

	public static final String QUESTION_IMG_FOLDER = "img/questions/";

	// The agent panel is the view (GUI) of the agent, it should be set from
	// outside

	private EasySketchGUI gui = null;

	// model that holds the interaction state with the user global for the
	// program
	private InteractionState currentState;

	// age and gender classifier
	public J48 tree = new J48(); 
	public RandomForest forest = new RandomForest();

	// the recognizer is the one in charge of processing the sketch and
	// recognizing shapes from it
	private EasySketchRecognizer recognizer = new EasySketchRecognizer(this);


	// The answer checker takes the recognition output and the current question
	// and determines if it is correct
	private AnswerChecker answerChecker = new AnswerChecker();

	// The state controller is used to determine the next state of interaction
	// based on the events
	private StateManager stateController;

	// The message controller is used to determine the most appropriate message
	// based on the state and recognition results
	private MessageManager messageController;

	// The SpeechModule says the messages out loud (handles the audio)
	private SpeechModule speechHelper = new SpeechModule();

	// A list of all the questions that were loaded
	private List<Question> questions = new ArrayList<Question>();

	// keep track of where we are in the question list
	private int currentQuestionNumber = 0;

	private WriteExcel writeExcel;
	private WritableSheet excelSheet;
	public List<Answer> studentAnswers = new ArrayList<Answer>();

	List<String> expectedShapes;
	WorkbookSettings wbSettings;
	WritableWorkbook workbook;

	Map<String,Long> summaryMap =new HashMap<String, Long>();

	private AgentThread agentThread = null;
	private boolean isPenDown = false;

	//ArrayList<ArrayList<String>> listOlists = new ArrayList<ArrayList<String>>();

	final ArrayList<ArrayList<Stroke>> predefinedStrokes = new ArrayList<ArrayList<Stroke>>();  // predefined stroke

	public enum AudioState {
		FEEDBACK, QUESTION
	}

	public EasySketchAgent(InteractionState initialInteractionState) {
		currentState = initialInteractionState;
		stateController = new StateManager(initialInteractionState);
		messageController = new MessageManager(initialInteractionState);
		initializeMessages();
		initializeClassifier();
	}

	public void initializeClassifier(){
		try {
			forest = recognizer.trainClassifier();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		// init agent thread
		agentThread = new AgentThread();
		Thread t1 = new Thread(agentThread);
		t1.start();

		// lastly the GUI is started by the agent
		gui = new EasySketchGUI(this);
		currentState.addInteractionObserver(gui.getInstructionPanel());
		gui.setVisible(true);

		try {
			try {
				loadQuestions();

			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public EasySketchGUI getMathSketchGUI(){
		return gui;
	}

	public int getCurrentQuestionNumber(){
		return currentQuestionNumber;
	}

	/**
	 * advance to next sequential question; loop back to 0 after last question.
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public void retrieveNextQuestion() throws WriteException, IOException {

		currentQuestionNumber++;

		/**
		 * End of the session
		 * 
		 */
		if (currentQuestionNumber >= questions.size()) { // if it has no more question, then finish.

			/**
			 * Just say it is finished
			 */
			ImageIcon finalIcon;

			gui.getQuestionPanel().setQuestionText("You finished the learning!");
			gui.getInstructionPanel().getInstructions().setText("You finished the learning!");
			gui.getSketchController().disableNextQuestion();
			finalIcon = new ImageIcon(
					QUESTION_IMG_FOLDER + "hurray.jpeg");


			gui.getQuestionPanel().getQuestionImagePanel().getImage().setIcon(finalIcon);
			speechHelper.say("FINISH");
			gui.repaint();

			/**
			 * Saving a student's answer to a map
			 */
			for(int i = 0; i < studentAnswers.size(); i++){
				summaryMap.put("DrawingTime"+i, studentAnswers.get(i).getTime());
			}

			/**
			 * Saving similarity of student's drawing
			 */
			for(int i = 0; i < studentAnswers.size(); i++){
				summaryMap.put("Similarity"+i, studentAnswers.get(i).getSimilarity());
			}


			// time difference between slow sketch and original sketch without limitation
			summaryMap.put("TimeDifference", studentAnswers.get(2).getTime() - studentAnswers.get(0).getTime());

			// intersection number
			summaryMap.put("IntersectionNumber", studentAnswers.get(1).getIntersectionNumber());

			Calendar cal = Calendar.getInstance();	
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String file_name = dateFormat.format(cal.getTime()).toString();

			/**
			 * Create a specific folder for the user
			 */
			File file_dir = new File("savingData/" + file_name);
			file_dir.mkdirs();

			CSVWrite.generateCsvFile(file_dir+ "/" + file_name+".csv" , summaryMap);

			/**
			 * Saving sketch files
			 */
			for(int i = 0; i < studentAnswers.size(); i++){

				if(studentAnswers.get(i).getSketch().getShapes().isEmpty()){
					EasySketchRecognizer.runPaleo(studentAnswers.get(i).getSketch());
				}

				try {
					File m_sketchFile = new File(file_dir+ "/" + file_name + "_" + i + ".xml");
					SimplTypesScope.serialize(studentAnswers.get(i).getSketch(), m_sketchFile, Format.XML);
				} catch (SIMPLTranslationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}else{ 
			Question currentQuestion = questions.get(currentQuestionNumber);

			currentState.setCurrentQuestion(currentQuestion);
			gui.getQuestionPanel().setCurrentQuestion(currentQuestion);

			if(currentQuestionNumber != 0){

				Sketch definedSketch = currentQuestion.getDefinedSketch();


				gui.getSketchController().getSketchCanvas().setSketch(currentQuestion.getDefinedSketch());
				Sketch sketch = gui.getSketchController().getSketchCanvas().getSketch();
				sketch.setColor(Color.RED);

				gui.repaint();
			}
			//gui.getInstructionPanel().setCurrentQuestion(currentQuestion);

			answerChecker.setCurrentQuestion(currentQuestion);
		}
		//speechHelper.say(AudioState.QUESTION,
		//		currentState.getTayoukiEmotionalState(), currentQuestion);
	}

	/**
	 * initialize maps using mapping
	 */
	private void initializeMessages() {
		ExcelContents contents = messageController.getExcelContents();
		speechHelper.initializeSoundMaps(contents);
	}

	// class is used to run the agent in the background
	class AgentThread implements Runnable {

		boolean penEvent = false;
		int tCount = 0;

		public void run() {
			System.out.println("Agent started...");
		}

	}

	public void processPenEvent() throws IOException, WriteException {

		if (isPenDown) {
			// The user is drawing ...do nothing for the moment, maybe a
			// different face
		} else {
			// The user finished drawing lets attempt recognition
			if (currentState.getUserSketch() != null) {

				Double resultLabel = 0.0;

				try {
					resultLabel = recognizer.recognizeLabel(currentState.getUserSketch());


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String feedbackTxt = null;
				if(resultLabel == 0.0){
					feedbackTxt = "Yay~!!You are learning!";
					gui.getFeedbackPanel().displayResponse(feedbackTxt,
							false);
				}else{
					feedbackTxt = "You are really good at this!";
					gui.getFeedbackPanel().displayResponse(feedbackTxt,
							true);
				}
				/*

				recognizer.submitForRecognition(currentState.getUserSketch()); 


				Sketch temp1 = currentState.getUserSketch();

				recognizer.setSketch(currentState.getUserSketch());

				Sketch temp = currentState.getUserSketch();

				List<Stroke> strokes = temp.getStrokes();

				/**
				 * Measure student's drawing time
				 */
				
				/*
				Long time = (long) 0.0;




				Long time1 = strokes.get(0).getPoints().get(0).getTime();
				Long time2;

				if(strokes.size() > 1){ // if the sketch has more than 1 stroke
					Stroke stroke = strokes.get(strokes.size()-1);
					time2 = stroke.getTime();

				}else{
					time2 = strokes.get(0).getPoints().get(strokes.get(0).getPoints().size()-1).getTime();
				}
				time = time2 - time1;

				//if(temp.getShapes().isEmpty()){
				EasySketchRecognizer.runPaleo(temp);
				//}

				try {
					File m_sketchFile = new File("savingData/"+ "test"  + ".xml");
					SimplTypesScope.serialize(temp, m_sketchFile, Format.XML);
				} catch (SIMPLTranslationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				strokes = new ArrayList<Stroke>();

				for(int i = 1; i < temp.getShapes().size(); i++){
					Stroke tempStroke = new Stroke(temp.getShape(i).getPoints());
					strokes.add(tempStroke);
				}

				Answer answer = new Answer(this.currentQuestionNumber);
				if(temp.getShapes() != null){

					recognizer.calculateSimilarity(temp.getShapes());

					IntersectionSegmenter is = new IntersectionSegmenter();
					int intersection = 0;
					for(ArrayList preStrokes: predefinedStrokes){
						intersection += is.setStrokes(preStrokes, strokes);
						//answer.setIntersectionNumber(is.setStrokes(preStrokes, strokes));
					}

					answer.setIntersectionNumber(intersection);
				}
				System.out.println("Drawing Time: " + time);

				answer.setTime(time);
				answer.setSketch(temp);

				this.studentAnswers.add(answer);
				*/
			}
		}

	}

	private void processTimeout() {
		stateController.processEvent(StateManager.EventType.TIMEOUT);

	}

	public void definePredefinedSketch(){
		File definedSketch = new File("config/predefinedSketch/star.xml");
		Sketch defined = null;
		try {
			defined = Sketch.deserialize(definedSketch);

			// initialize predefinedStroke
			if(predefinedStrokes.size() == 0){
				List<Point> points = new ArrayList<Point>();
				int i = 1;
				for(Shape shape: defined.getShapes()){
					ArrayList<Stroke> preStrokes = new ArrayList<Stroke>();

					if(shape.getShapes().size() > 1){
						//if(i == 2){
						for(Shape subShape: shape.getShapes()){
							if(subShape.getShapes().size() > 1){
								for(Shape subsubShape : subShape.getShapes()){
									preStrokes.add(subsubShape.getStrokes().get(0));
								}
							}else{
								preStrokes.add(subShape.getStrokes().get(0));
							}
							//predefinedStroke.add(subShape.getStrokes().get(0));
						}
						//}
						//i++;

					}
					predefinedStrokes.add(preStrokes);
				}
			}

		} catch (SIMPLTranslationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Initialize question. Plan audio
	 * 
	 * @param question
	 * @throws IOException
	 * @throws WriteException 
	 */
	public void loadQuestions() throws IOException, WriteException {

		ExcelContents contents;
		ReadExcel excel = new ReadExcel();
		excel.setInputFile("config/questions.xls");
		excel.read();
		contents = excel.getContents();
		speechHelper.initializeQuestionMaps(contents);
		expectedShapes = new ArrayList<String>();
		System.out.println("loadQuestions");
		definePredefinedSketch();
		//new Stroke(allPoints);


		// TODO support multiple images and multiple instructions depending on
		// the help level
		for (int i = 1; i < contents.cell1.size(); i++) {
			if(!contents.cell1.get(i).isEmpty()){
				Question question = new Question();
				question.setQuestionUID(i);
				List<String> instructions = new ArrayList<String>();
				instructions.add(contents.cell2.get(i));
				question.setTextInstructions(instructions);
				question.setExpectedShapeName(contents.cell3.get(i));

				expectedShapes.add(contents.cell3.get(i));

				// add the image to the question if there is any...
				if (contents.cell4.get(i) != null
						&& !contents.cell4.get(i).isEmpty()) {
					ImageIcon img = new ImageIcon(QUESTION_IMG_FOLDER
							+ contents.cell4.get(i));
					List<ImageIcon> images = new ArrayList<ImageIcon>();
					images.add(img);
					question.setImages(images);
				}

				List<String> helpInstructions = new ArrayList<String>();
				helpInstructions.add(contents.cell5.get(i));
				question.setHelpInstructions(helpInstructions);

				// load predefined sketch if the question is not an exercise
				if(question.getQuestionUID() != 1){

					File definedSketch = new File("config/predefinedSketch/star.xml");

					Sketch defined = null;
					try {
						defined = Sketch.deserialize(definedSketch);
					} catch (SIMPLTranslationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					question.setDefinedSketch(defined);
				}

				questions.add(question);
			}
		}

		currentQuestionNumber = -1;
		retrieveNextQuestion();

	}

	@Override
	public void penUpEvent(Sketch sketch) {
		currentState.setUserSketch(sketch);
		isPenDown = false;
		agentThread.penEvent = true;

	}

	@Override
	public void penDownEvent(Sketch sketch) {
		isPenDown = true;
		agentThread.penEvent = true;

	}

	public InteractionState getCurrentState(){
		return currentState;
	}

	public SpeechModule getSpeechHelper(){
		return speechHelper;
	}


	public EasySketchGUI getGUI(){
		return gui;
	}


}
