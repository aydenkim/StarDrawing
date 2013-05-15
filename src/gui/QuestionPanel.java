/**
 * QuestionPanel.java
 * 
 * Revision History:<br>
 * Dec 17, 2011 Ayden Kim - File created
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

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.tamu.core.sketch.Shape;

import model.Question;

public class QuestionPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7675127760442487203L;
	private QuestionImagePanel questionImagePanel;
	
	private Question currentQuestion;
	protected JTextArea m_questionText = null;

	public QuestionPanel() {
		super();

		initializeGUI();
	}

	private void initializeGUI() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(10, 0));
		
		questionImagePanel = new QuestionImagePanel();
		
		m_questionText = new JTextArea(3, 55);
		m_questionText.setFont(new Font("Tahoma", Font.PLAIN, 20));
		m_questionText.setLineWrap(true);
		m_questionText.setWrapStyleWord(true);
		m_questionText.setEditable(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(m_questionText,BorderLayout.LINE_START);

	}

	/**
	 * Set the current question and update InstructionPanel
	 * 
	 * @param question
	 *            : current question
	 */
	public void setCurrentQuestion(Question question) {
		currentQuestion = question;
		m_questionText.setText(currentQuestion.getTextInstructions(0));
		questionImagePanel.setCurrentQuestion(question);
	}
	
	public QuestionImagePanel getQuestionImagePanel(){
		return questionImagePanel;
	}
	
	public void setQuestionText(String text){
		m_questionText.setText(text);
	}
	
	

}
