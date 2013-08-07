/**
 * QuestionImagePanel.java
 * 
 * Revision History:<br>
 * Dec 17, 2011 Ayden - File created
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

import model.Question;

public class QuestionImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7675127760442487202L;

	private Question currentQuestion;

	public static final Color BACK_COLOR = new Color(255, 255, 255); // 152, 251, 152
	
	public static final Color TITLE_COLOR = new Color(123, 101, 238); // 255, 235, 205  250, 128, 114

	private JLabel image = null;
	private JPanel instructionPanel = new JPanel();

	private int helpLevel = 0;

	public QuestionImagePanel() {
		super();

		initializeGUI();
	}

	private void initializeGUI() {

		JLabel title = new JLabel("Questions");
		title.setForeground(Color.black);
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		title.setFont(titleFont);

		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(TITLE_COLOR);
		titlePanel.add(title);

		titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				getPreferredSize().height));
		titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		ImageIcon redoButtonIcon = new ImageIcon(
				"img/questions/star.png");

		image = new JLabel(redoButtonIcon);

		JButton imgBtn = new JButton(redoButtonIcon);

		
		instructionPanel.setLayout(new BoxLayout(instructionPanel,
				BoxLayout.Y_AXIS));
		instructionPanel.setBackground(BACK_COLOR);
		instructionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		instructionPanel.add(image);
		instructionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		setLayout(new BorderLayout());

		add(titlePanel, BorderLayout.NORTH);
		add(instructionPanel, BorderLayout.CENTER);

	}

	/**
	 * Set the current question and update QuestionImagePanel
	 * 
	 * @param question
	 *            : current question
	 */
	public void setCurrentQuestion(Question question) {
		currentQuestion = question;
		updateInstructionLabel();

	}

	public void setHelpLevel(int helpLevel) {
		this.helpLevel = helpLevel;
	}
	
	public void updateInstructionLabel(){
		if(!currentQuestion.getAllImages().isEmpty() && currentQuestion.getImage(helpLevel) != null){
			System.out.println(currentQuestion.getImage(helpLevel).toString());
			image.setIcon(currentQuestion.getImage(helpLevel));
		}
		
		this.repaint();
	}
	
	public JLabel getImage(){
		return image;
	}
}
