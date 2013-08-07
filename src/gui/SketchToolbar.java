/**
 * SketchToolbar.java
 * 
 * Revision History:<br>
 * Nov 8, 2011 fvides - File created
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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import gui.ISketchController;

public class SketchToolbar extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6652340186152118749L;

	private JButton btnUndoStroke;
	private JButton btnRedoStroke;
	private JButton btnClearSketch;
	private JButton btnCheckSketch;
	private JButton btnSaveSketch;
	private JButton btnLoadSketch;
	private JButton btnNext;
	private JButton btnPlay;
	
	private ISketchController controller;
	

	public SketchToolbar(ISketchController controller) {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout(30, 0));
		
		this.controller = controller;
		//controller.loadSketch();
		ImageIcon undoButtonIcon = new ImageIcon("img/icons/undo-icon.png");
		btnUndoStroke = new JButton(undoButtonIcon);
		btnUndoStroke.setToolTipText("Undo last stroke");
		btnUndoStroke.addActionListener(this);

		ImageIcon redoButtonIcon = new ImageIcon("img/icons/redo-icon.png");
		btnRedoStroke = new JButton(redoButtonIcon);
		btnRedoStroke.setToolTipText("Redo last stroke");
		btnRedoStroke.addActionListener(this);

		ImageIcon clearButtonIcon = new ImageIcon("img/icons/clear-icon.png");
		btnClearSketch = new JButton(clearButtonIcon);
		btnClearSketch.setToolTipText("Clear Sketch");
		btnClearSketch.addActionListener(this);
		
		
		
		ImageIcon saveButtonIcon = new ImageIcon("img/icons/save50.png");
		btnSaveSketch = new JButton(saveButtonIcon);
		btnSaveSketch.setToolTipText("Save Sketch");
		
		ImageIcon nextButtonIcon = new ImageIcon("img/icons/forward.png");
		btnNext = new JButton(nextButtonIcon);
		btnNext.setToolTipText("Next Question");
		
		ImageIcon playIcon = new ImageIcon("img/icons/play.png");
		btnPlay = new JButton(playIcon);
		btnPlay.setToolTipText("Play the sketch");
		
		ImageIcon checkButtonIcon = new ImageIcon("img/icons/checkShape.png");
		btnCheckSketch = new JButton(checkButtonIcon);
		btnCheckSketch.setToolTipText("Check Sketch");
		btnCheckSketch.addActionListener(this);
		
		
		//btnSaveSketch.addActionListener(this);
		
		btnClearSketch.setEnabled(false);
		btnUndoStroke.setEnabled(false);
		btnRedoStroke.setEnabled(false);
		btnCheckSketch.setEnabled(false);
		btnSaveSketch.setEnabled(false);
		//btnLoadSketch.setEnabled(true);
		btnNext.setEnabled(true);
		btnPlay.setEnabled(false);

		//setBackground(QuestionImagePanel.BACK_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		//add(btnUndoStroke);
		//add(btnRedoStroke);
		add(btnClearSketch);
		//add(btnSaveSketch);	
		add(btnNext);
		//add(btnPlay);
		//add(btnCheckSketch);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnUndoStroke) {
			controller.undoLastStroke();

		} else if (e.getSource() == btnRedoStroke) {
			controller.redoLastStroke();

		} else if (e.getSource() == btnClearSketch) {
			controller.clearSketch();
		} else if (e.getSource() == btnCheckSketch) {
			controller.checkSketch();
		} else if (e.getSource() == btnSaveSketch) {
			controller.saveSketch();
		} else if (e.getSource() == btnNext) {
			controller.nextQuestion();
		} else if (e.getSource() == btnPlay) {
			controller.playSketch();
		}

	}
	
	public JButton getCheckButton(){
		return btnCheckSketch;
	}
	
	public JButton getSaveButton(){
		return btnSaveSketch;
	}
	
	public JButton getNextButton(){
		return btnNext;
	}
	
	public JButton getPlayButton(){
		return btnPlay;
	}
	
	public void setUndoEnabled(boolean enable){
		btnUndoStroke.setEnabled(enable);
	}
	
	public void setRedoEnabled(boolean enable){
		btnRedoStroke.setEnabled(enable);
	}
	
	public void setClearEnabled(boolean enable){
		btnClearSketch.setEnabled(enable);
	}
	
	public void setCheckEnalbled(boolean enable){
		btnCheckSketch.setEnabled(enable);
	}
	
	public void setSaveEnabled(boolean enable){
		btnSaveSketch.setEnabled(enable);
	}
	
	public void setNextEnabled(boolean enable){
		btnNext.setEnabled(enable);
	}
	
	public void setPlayEnabled(boolean enable){
		btnPlay.setEnabled(enable);
	}


}
