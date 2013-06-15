/**
 * MathSketchController.java
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

package gui;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import model.Question;

import org.ladder.io.DOMInput;
import org.ladder.io.UnknownSketchFileTypeException;
import org.ladder.io.XMLFileFilter;
import org.xml.sax.SAXException;

import recognition.MathSketchRecognizer;

import gui.ISketchController;
import edu.tamu.core.gui.ISketchObserver;
import edu.tamu.core.gui.JSketchCanvas;
import edu.tamu.core.gui.SaveAskDialog;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.STranslationScope;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.formatenums.Format;


public class MathSketchController implements ISketchController, ISketchObserver {

	private JSketchCanvas canvas;

	private Stack<Stroke> undoStack;

	private SketchToolbar toolbar;

	// assignment
	protected File m_sketchFile;
	protected boolean m_isAssignmentSaved = true;

	// other gui components
	protected JFileChooser m_chooser = new JFileChooser();
	
	/**
	 * Input file reader
	 */
	DOMInput m_input = new DOMInput();

	public MathSketchController(JSketchCanvas sketchCanvas) {
		this(sketchCanvas,null);
		// document name
		m_sketchFile = null;
	}

	public MathSketchController(JSketchCanvas sketchCanvas, SketchToolbar toolbar) {
		this.canvas = sketchCanvas;
		this.toolbar = toolbar;
		undoStack = new Stack<Stroke>();
		// document name
		m_sketchFile = null;

	}
	
	public JSketchCanvas getSketchCanvas(){
		return canvas;
	}

	@Override
	public void undoLastStroke() {
		Sketch sketch = canvas.getSketch();
		Stroke last = sketch.getLastStroke();
		if (last != null) {
			sketch.remove(last);
			undoStack.push(last);
		}
		if(toolbar!=null){
			toolbar.setRedoEnabled(true);
			toolbar.setSaveEnabled(false);
			if(sketch.getStrokes().isEmpty()){
				toolbar.setUndoEnabled(false);
			}
		}

		canvas.repaint();

	}

	@Override
	public void redoLastStroke() {
		Sketch sketch = canvas.getSketch();
		if(!undoStack.isEmpty()){
			Stroke redoStroke = undoStack.pop();
			sketch.add(redoStroke);
		}
		if(toolbar!=null){
			toolbar.setUndoEnabled(true);
			toolbar.setSaveEnabled(false);
			if(undoStack.isEmpty()){
				toolbar.setRedoEnabled(false);

			}
		}
		canvas.repaint();

	}

	/**
	 * Note that this clear acts as a RESET, and it cannot be undone
	 */
	@Override
	public void clearSketch() {

		canvas.clear();
		toolbar.setClearEnabled(false);
		toolbar.setRedoEnabled(false);
		toolbar.setUndoEnabled(false);
		toolbar.setCheckEnalbled(false);
		toolbar.setSaveEnabled(false);
		undoStack.clear();
		canvas.repaint();

	}

	public void getPriorQueston(){

	}

	public void getNextQueston(){

	}
	
	public void disableNextQuestion(){
		toolbar.setNextEnabled(false);
	}

	@Override
	public Sketch getSketch() {		
		return canvas.getSketch();
	}

	public void setToolbar(SketchToolbar toolbar) {
		this.toolbar = toolbar;
	}

	@Override
	public void penDownEvent(Sketch arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void penUpEvent(Sketch sketch) {
		toolbar.setUndoEnabled(true);
		toolbar.setClearEnabled(true);
		toolbar.setCheckEnalbled(true);
		toolbar.setSaveEnabled(true);
		toolbar.setRedoEnabled(false);
		toolbar.setPlayEnabled(true);
		undoStack.clear();

	}
	
	/**
	 * Play the sketch
	 */
	public void playSketch(){
		Sketch sketch = canvas.getSketch();
		
		MoviePanel.Test(canvas.getSketch());
		
	}

	protected void resaveSketch() {

		Sketch sketch = canvas.getSketch();
		
		if(sketch.getShapes().isEmpty()){
			MathSketchRecognizer.runPaleo(sketch);
		}
		try {
			SimplTypesScope.serialize(canvas.getSketch(), m_sketchFile, Format.XML);
		} catch (SIMPLTranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_isAssignmentSaved = true;
	}

	/**
	 * Save the sketch as an XML file
	 */
	protected void saveSketchAs() {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		//get current date time with Date()
		Date date = new Date();

		Calendar cal = Calendar.getInstance();
		dateFormat.format(cal.getTime());

		File testDataFolder = new File("savingData");	
		String file_name = dateFormat.format(cal.getTime()).toString();
		File f = new File(testDataFolder + "/" + file_name + ".xml");


		m_sketchFile = f;
		resaveSketch();
	}
	
	


	@Override
	public void saveSketch() {
		// TODO Auto-generated method stub
		if (m_sketchFile != null && m_sketchFile.exists())
			resaveSketch();
		else
			saveSketchAs();
	}

	/**
	 * Load the pre-defined sketch
	 */
	
	@Override
	public void loadSketch() {
		File definedSketch = new File("config/predefinedSketch/star.xml");
		System.out.println(definedSketch.canRead());
		
		Sketch defined = null;
		try {
			defined = Sketch.deserialize(definedSketch);
			canvas.setSketch(defined);
		} catch (SIMPLTranslationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	@Override
	public void checkSketch() {

		// TODO Auto-generated method stub

	}

	@Override
	public void nextQuestion() {
		this.clearSketch();
		// TODO Auto-generated method stub
		
	}

}
