/*******************************************************************************
 *  Revision History:<br>
 *  SRL Member - File created
 *
 *  <p>
 *  <pre>
 *  This work is released under the BSD License:
 *  (C) 2011 Sketch Recognition Lab, Texas A&M University (hereafter SRL @ TAMU)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Sketch Recognition Lab, Texas A&M University 
 *        nor the names of its contributors may be used to endorse or promote 
 *        products derived from this software without specific prior written 
 *        permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  </pre>
 *  
 *******************************************************************************/
package org.ladder.tools.gui.demopanel;

import java.awt.*;
import java.io.File;

import javax.swing.*;

public class TestGui extends Gui
{
	/*** primary methods ***/
	protected void addContent(JFrame frame)
	{
		Constraints c;
		
		// ---------------------------
		// initialize your panels here
		// ---------------------------
		JPanel greenPanel = getGreenPanel();	// get the green panel
		JPanel greyPanel = getGreyPanel();		// get the grey panel

		
		
		// -------------------------
        // position your panels here
		// -------------------------
		
		// place the green panel at grid position (0, 0)
        c = new Constraints.Builder(0, 0).build();
        frame.add(greenPanel, c);
        
        // place the green panel at grid position (0, 1)
        c = new Constraints.Builder(0, 1).build();
        frame.add(greyPanel, c);
	}
	
	private JPanel getGreenPanel()
	{
		Constraints c;
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		
		c = new Constraints.Builder().build();
		panel.add(new JLabel(new ImageIcon(GREEN_IMAGE_FILE_LOC)), c);
		
		return panel;
	}
	
	private JPanel getGreyPanel()
	{
		Constraints c;
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.WHITE);
		
		c = new Constraints.Builder().build();
		panel.add(new JLabel(new ImageIcon(GREY_IMAGE_FILE_LOC)), c);
		
		return panel;
	}
	
	/*** auxiliary methods ***/
	
	
	
	/*** main method ***/
	public static void main(String[] args)
	{
		Gui gui = new TestGui();
		gui.run();
	}
	
	
	
	/*** fields ***/
	public static final String GREEN_IMAGE_FILE_LOC = IMAGES_DIR_LOC + "green.png";
	public static final String GREY_IMAGE_FILE_LOC = IMAGES_DIR_LOC + "grey.png";
}