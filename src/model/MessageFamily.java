package model;

/**
 * This enum gives the message handler the base message family or type to
 * translate to a concrete message
 * 
 * @author Paco
 * 
 */
public enum MessageFamily {
	UNDEFINED, CORRECT, TIMEOUT, UNKNOWN_SHAPE, INCORRECT_SHAPE, NEW_QUESTION;
	
	public static final String PARAM_EXPECTED_SHAPE = "expectedShape";
	
	public static final String PARAM_FOUND_SHAPE = "foundShape";
	
	public static final String PARAM_NEW_QUESTION_TXT = "newQuestionText";
	
	
}
