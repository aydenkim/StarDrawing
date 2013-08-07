/**
 * TayoukiSpeech.java
 * 
 * Revision History:<br>
 * Nov 9, 2011 fvides - File created
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import model.Question;
import model.agent.ReadExcel.ExcelContents;
import model.agent.MathSketchAgent.AudioState;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Class in charge of the speech and sound
 * 
 * @author fvides
 * 
 */
public class SpeechModule {

	private Map<String, String> soundMap = null;
	private Map<Integer, String> questionSoundMap = null;
	private Map<Integer, String> questionTextMap = null;

	private static final String SOUND_DIR = "sound/";

	public SpeechModule() {
		// TODO load sounds using xls...

	}

	public void initializeSoundMaps(ExcelContents contents){
		soundMap = new HashMap<String, String>();


		for (int i = 1; i < contents.cell2.size(); i++) {
			soundMap.put(contents.cell2.get(i), SOUND_DIR
					+ contents.cell4.get(i));
		}


	}

	public void initializeQuestionMaps(ExcelContents contents){
		questionSoundMap = new HashMap<Integer, String>();
		questionTextMap = new HashMap<Integer, String>();

		for(int i = 1 ; i < contents.cell1.size(); i++){
			questionSoundMap.put(i, SOUND_DIR + contents.cell3.get(i));
			questionTextMap.put(i, contents.cell2.get(i));
		}

	}
	/**
	 * Given a msgCode as defined in the constants in AgentResponse the agent
	 * will say it out loud
	 * 
	 * @param msg
	 */

	// TODO right now it is receiving an emotional state...is better to receive
	// a specific msgCode
	public void say(AudioState state, AgentEmotionalState msg, Question question) {

		if (questionSoundMap.containsKey(question.getQuestionUID())) {
			InputStream in = null;
			try {
				if (state == AudioState.QUESTION) {
					System.out.println(questionSoundMap
							.get(question.getQuestionUID()));
					in = new FileInputStream(questionSoundMap
							.get(question.getQuestionUID()));
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			// Create an AudioStream object from the input stream.
			AudioStream as = null;
			try {
				as = new AudioStream(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Use the static class member "player" from class AudioPlayer to
			// play
			// clip.
			AudioPlayer.player.start(as);

		}
	}

	public void say(String string){

		if(soundMap.containsKey(string)){
			InputStream in = null;
			try {

				in = new FileInputStream(soundMap.get(string));

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			// Create an AudioStream object from the input stream.
			AudioStream as = null;
			try {
				as = new AudioStream(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Use the static class member "player" from class AudioPlayer to
			// play
			// clip.
			AudioPlayer.player.start(as);

		}

	}

	public void say(AudioState state, String msgId) {

		if (soundMap.containsKey(msgId)) {
			InputStream in = null;
			try {
				if (state == AudioState.FEEDBACK) {
					in = new FileInputStream(soundMap.get(msgId));
				} 

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			// Create an AudioStream object from the input stream.
			AudioStream as = null;
			try {
				as = new AudioStream(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Use the static class member "player" from class AudioPlayer to
			// play
			// clip.
			AudioPlayer.player.start(as);
		}


	}

}
