package model;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.core.sketch.Sketch;
import model.agent.AgentEmotionalState;

/**
 * This class represents the current state of the interaction. A state diagram
 * will help determining what is the next state. And based on the current state
 * the views will be updated and the agent will take action
 * 
 * @author Paco
 * 
 */
public class InteractionState {

	/**
	 * Defines if the agent is currently happy, sad, angry or in other mood.
	 */
	private AgentEmotionalState tayoukiEmotionalState;

	/**
	 * The current question that is being asked to the user
	 */
	private Question currentQuestion;

	/**
	 * an integer that defines the amount of help that is given to the user. The
	 * integer will range from 0 to 10 being 0 the least help and 10 the maximum
	 * help. The particular semantics of what each help level means will be
	 * determined by the question itself. E.g. 0: Draw a house, 10: Here is how
	 * you should draw a house (animated gif).
	 */
	private int helpLvl;

	/**
	 * The current drawing of the user
	 */
	private Sketch userSketch;

	/**
	 * Determine how many times a timeout has occurred in the system (how much
	 * time has passed without user input)
	 */
	private int timeoutCount;

	private List<InteractionObserver> observers;

	public InteractionState() {
		super();
		observers = new ArrayList<InteractionObserver>();
		currentQuestion = null;
		userSketch = null;
		helpLvl = 0;		
		timeoutCount = 0;
		tayoukiEmotionalState = AgentEmotionalState.NEUTRAL;
	}

	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public int getHelpLvl() {
		return helpLvl;
	}

	public void incrementHelpLvl() {
		helpLvl++;
	}
	public void setHelpLvl(int helpLvl) {
		this.helpLvl = helpLvl;
	}

	public Sketch getUserSketch() {
		return userSketch;
	}

	public void setUserSketch(Sketch userSketch) {
		this.userSketch = userSketch;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	public void incrementTimeoutCount() {
		this.timeoutCount++;
	}

	public void resetTimeoutCount() {
		this.timeoutCount = 0;
	}

	public AgentEmotionalState getTayoukiEmotionalState() {
		return tayoukiEmotionalState;
	}

	public void setTayoukiEmotionalState(
			AgentEmotionalState tayoukiEmotionalState) {
		this.tayoukiEmotionalState = tayoukiEmotionalState;
		notifyObservers();
	}
	
	public void addInteractionObserver(InteractionObserver observer){
		observers.add(observer);
	}
	
	private void notifyObservers(){
		for (InteractionObserver observer : observers) {
			observer.stateChanged(this);
		}
	}

}
