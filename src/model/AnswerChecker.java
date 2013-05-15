package model;

import java.util.HashMap;
import java.util.Map;

import edu.tamu.recognition.IRecognitionResult;

/**
 * This class is responsible of taking the results thrown by the recognizer and
 * the current question to be evaluated and translating them into a
 * messageFamily and its parameters if any. To ensure consistency each new
 * question should reset the checker or use a new instance of it.
 * 
 * @author Paco
 * 
 */
public class AnswerChecker {

	private Question currentQuestion = null;

	private IRecognitionResult currentRecognitionResult = null;

	private MessageFamily resultMessage = MessageFamily.UNDEFINED;

	private Map<String, String> messageParameters = new HashMap<String, String>();

	private boolean isCorrect = false;

	public AnswerChecker() {
		super();

	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public void setCurrentRecognitionResult(
			IRecognitionResult currentRecognitionResult) {
		this.currentRecognitionResult = currentRecognitionResult;
	}

	/**
	 * This method takes as an input the current question and the current result
	 * as thrown by the recognizer. It compares it and gives as a result a
	 * messageTypeID and additional parameters for the message handler. It also
	 * determines if the answer is correct or not.
	 * 
	 * @throws Exception
	 *             the call of this method expects that both the recognition
	 *             result and the question are set before calling it.
	 */
	public void checkAnswers() throws Exception {
		if (currentQuestion == null) {
			throw new Exception(
					"The question was not set in the answer checker before attempting to check the answer");
		}

		if (currentRecognitionResult == null) {
			throw new Exception(
					"There is no recognition result set in the answer checker before attempting to check the answer");
		}

		String shapeName = currentQuestion.getExpectedShapeName();
		messageParameters.put(MessageFamily.PARAM_EXPECTED_SHAPE, shapeName);

		if (currentRecognitionResult.getBestShape() == null
				|| currentRecognitionResult.getNumInterpretations() == 0) {
			isCorrect = false;
			resultMessage = MessageFamily.UNKNOWN_SHAPE;

		} else if (currentRecognitionResult.getBestShape().getInterpretation().label
				.equals(shapeName)) {
			isCorrect = true;
			resultMessage = MessageFamily.CORRECT;

		}

		else {
			isCorrect = false;
			messageParameters
					.put(MessageFamily.PARAM_FOUND_SHAPE,
							currentRecognitionResult.getBestShape()
									.getInterpretation().label);
			resultMessage = MessageFamily.INCORRECT_SHAPE;
		}

	}

	/**
	 * Resets the attributes to the original values. Equivalent to making a new
	 * AnswerChecker.
	 */
	public void reset() {
		isCorrect = false;
		resultMessage = MessageFamily.UNDEFINED;
		currentQuestion = null;
		currentRecognitionResult = null;
		messageParameters.clear();
	}

	public MessageFamily getResultMessage() {
		return resultMessage;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public Map<String, String> getMessageParameters() {
		return messageParameters;
	}

}
