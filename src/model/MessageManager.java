package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.agent.AgentEmotionalState;
import model.agent.ReadExcel;
import model.agent.SpeechModule;
import model.agent.ReadExcel.ExcelContents;


public class MessageManager {

	private InteractionState currentState;

	private MessageMap messageMap = null;
	
	private ExcelContents contents = null;
	
	public MessageManager(InteractionState initialInteractionState) {
		currentState = initialInteractionState;
		loadMap();
	}

	private void loadMap() {
		// TODO Auto-generated method stub

		ReadExcel excel = new ReadExcel();
		excel.setInputFile("config/message1.xls");
		try {
			excel.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contents = excel.getContents();
		
		messageMap = new MessageMap();
		for(int i = 1; i < contents.getCell1().size(); i++){	
			messageMap.messageId.add(contents.getCell1().get(i));
			messageMap.emotion.add(AgentEmotionalState.valueOf(contents.getCell2().get(i)));
			messageMap.txtMessage.add(contents.getCell3().get(i));
			messageMap.audio.add(contents.getCell4().get(i));
		}
        
		System.out.println("Done");
	}

	/**
	 * Given a message family or type (e.g. CORRECT) and if necessary some
	 * additional parameters this method will return an appropriate message code
	 * based on the current state and history
	 * 
	 * @param msgFamily
	 * @param parameters
	 * @return
	 */
	public String getAppropiateMessage(MessageFamily msgFamily) {
		// TODO...
		AgentEmotionalState currentEmotion = currentState
				.getEmotionalState();
		
		for(int i = 0; i < messageMap.emotion.size(); i++){
			if(messageMap.emotion.get(i) == currentEmotion){
				if(messageMap.messageFamily.get(i) == msgFamily){
					return messageMap.messageId.get(i);
				}
			}
		}
		
		return null;
		//return msgIdMap.get(msgFamily).get(currentEmotion);

	}

	/**
	 * Given a message code returns the complete text
	 * 
	 * @param msgCode
	 * @return
	 */
	public String translate(String msgCode, Map<String, String> parameters) {
		
		for(int i = 0 ; i < messageMap.messageId.size(); i++){
			if(messageMap.messageId.get(i) == msgCode){
				return messageMap.txtMessage.get(i);
			}
		}

		return null;
	}
	
	class MessageMap{
		List<String> messageId = new ArrayList<String>();
		List<MessageFamily> messageFamily = new ArrayList<MessageFamily>();
		List<AgentEmotionalState> emotion = new ArrayList<AgentEmotionalState>();
		List<String> txtMessage = new ArrayList<String>();
		List<String> audio = new ArrayList<String>();
	}
	
	public ExcelContents getExcelContents(){
		return contents;
	}

}
