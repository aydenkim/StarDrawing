/**
 * Question.java
 * 
 * Revision History:<br>
 * Nov 3, 2011 fvides - File created
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

package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import edu.tamu.core.sketch.Sketch;

public class Question {


	private int questionUID;
	private String expectedShapeName;
	private List<ImageIcon> images = new ArrayList<ImageIcon>();
	private List<String> textInstructions;
	private List<String> helpInstructions;
	private Sketch definedSketch; // defined sketch for following

	public int getQuestionUID() {
		return questionUID;
	}

	public void setQuestionUID(int questionUID) {
		this.questionUID = questionUID;
	}

	public String getExpectedShapeName() {
		return expectedShapeName;
	}

	public void setExpectedShapeName(String expectedShapeName) {
		this.expectedShapeName = expectedShapeName;
	}

	public Sketch getDefinedSketch(){
		return  definedSketch;
	}

    public void setDefinedSketch(Sketch sketch){
    	definedSketch = sketch;
    }

	@Override
	public boolean equals(Object o) {
		if (o instanceof Question
				&& ((Question) o).getQuestionUID() == this.questionUID)
			return true;
		return false;

	}

	@Override
	public int hashCode() {
		return new Integer(questionUID).hashCode();
	}

	/**
	 * Returns an image with instructions according to the specified help level.
	 * If the help level is greater than the available help it will return the
	 * maximum help available.
	 * 
	 * @param helpLevel
	 * @return
	 */
	public ImageIcon getImage(int helpLevel) {
		return images.get(helpLevel);
	}

	public List<ImageIcon> getAllImages() {
		return images;
	}

	public void setImages(List<ImageIcon> images) {
		this.images = images;
	}

	/**
	 * Returns a text with instructions according to the specified help level.
	 * If the help level is greater than the available help it will return the
	 * maximum help available.
	 * 
	 * @param helpLevel
	 *            This should be an integer that ranges from 0 to 10 being 0 the
	 *            least help and 10 the maximum help. The particular semantics
	 *            of what each help level means will be determined by the
	 *            question itself. E.g. 0: Draw a house, 10: Here is how you
	 *            should draw a house (animated gif).
	 * @return
	 */
	public String getTextInstructions(int helpLevel) {
		if (helpLevel < textInstructions.size())
			return textInstructions.get(helpLevel);
		else
			return textInstructions.get(textInstructions.size() - 1);
	}

	/**
	 * Returns the number of available help levels for this questions
	 * 
	 * @return
	 */
	public int getAvailableHelpLevels() {
		return textInstructions.size();
	}

	public List<String> getAllTextInstructions() {
		return textInstructions;
	}

	public void setTextInstructions(List<String> textInstructions) {
		this.textInstructions = textInstructions;
	}

	public void setHelpInstructions(List<String> helpInstructions){
		this.helpInstructions = helpInstructions;
	}

	public String getHelpInstructions(int helpLevel) {
		if (helpLevel < helpInstructions.size())
			return helpInstructions.get(helpLevel);
		else
			return helpInstructions.get(helpInstructions.size() - 1);
	}

}
