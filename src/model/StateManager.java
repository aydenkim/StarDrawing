package model;

import java.util.LinkedList;

import model.agent.AgentEmotionalState;

public class StateManager {

	public static enum EventType {
		CORRECT_ANSW, INCORRECT_ANSW, TIMEOUT, PEN_DOWN
	}

	// The history is mantained in a list
	public LinkedList<EventType> history;

	private InteractionState currentState = null;

	public StateManager(InteractionState initialInteractionState) {
		currentState = initialInteractionState;
		history = new LinkedList<StateManager.EventType>();
	}

	public void processEvent(EventType e) {
		history.push(e);
		if (e.equals(EventType.TIMEOUT))
			currentState.incrementTimeoutCount();
		else
			currentState.resetTimeoutCount();

		// TODO consider doing this using a mapping loaded from a file instead
		// of a mess of ifs

		AgentEmotionalState currentEmotion = currentState
				.getEmotionalState();
		/*if (currentEmotion.equals(AgentEmotionalState.NEUTRAL)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.IMPATIENT);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.HAPPY);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.CONFUSED);
			}
		}

		else if (currentEmotion.equals(AgentEmotionalState.CONFUSED)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.NEUTRAL);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.HAPPY);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.DISSAPOINTED);
			}
		}

		else if (currentEmotion.equals(AgentEmotionalState.DISSAPOINTED)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.NEUTRAL);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.NEUTRAL);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.ANGRY);
			}
		}

		else if (currentEmotion.equals(AgentEmotionalState.ANGRY)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.DISSAPOINTED);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.NEUTRAL);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.ANGRY);
			}
		}

		else if (currentEmotion.equals(AgentEmotionalState.HAPPY)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.NEUTRAL);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.EXCITED);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.CONFUSED);
			}
		}
		
		else if (currentEmotion.equals(AgentEmotionalState.EXCITED)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.HAPPY);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.EXCITED);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.CONFUSED);
			}
		}
		
		else if (currentEmotion.equals(AgentEmotionalState.IMPATIENT)) {
			if (e.equals(EventType.TIMEOUT)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.IMPATIENT);
			} else if (e.equals(EventType.CORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.HAPPY);
			} else if (e.equals(EventType.INCORRECT_ANSW)) {
				currentState
						.setTayoukiEmotionalState(AgentEmotionalState.CONFUSED);
			}
		}*/


	}

	public LinkedList<EventType> getHistory() {
		return history;
	}

}
