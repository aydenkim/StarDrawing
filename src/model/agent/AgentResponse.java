package model.agent;

public class AgentResponse {

	public AgentResponse(AgentEmotionalState emotion, String message,
			boolean correctAnswer) {
		super();
		this.emotion = emotion;
		this.message = message;
		this.correctAnswer = correctAnswer;
	}

	public AgentResponse() {
		super();
		this.emotion = AgentEmotionalState.START;
		this.message = "";
		this.correctAnswer = false;
	}

	private AgentEmotionalState emotion;

	private String message;

	private boolean correctAnswer;

	public AgentEmotionalState getEmotion() {
		return emotion;
	}

	public void setEmotion(AgentEmotionalState emotion) {
		this.emotion = emotion;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(boolean correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

}
