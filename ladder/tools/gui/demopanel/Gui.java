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
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public abstract class Gui
{
	/*** primary methods ***/
	public final void run()
	{
		buildFrame();
	}
	
	protected final JFrame buildFrame()
	{
        // build the frame
		JFrame frame = new JFrame();
        frame.getContentPane().setBackground(Color.WHITE);		// set background color to white
        frame.setLayout(new GridBagLayout());					// set layout manager to GridBagLayout
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 	// exit program when frame is closed
        
        addContent(frame);										// add the panels to frame
        
        frame.pack();											// size frame
        frame.setVisible(true);									// show frame
        frame.setLocationRelativeTo(null);						// center frame
        
        return frame;
	}
	
	protected void addContent(JFrame frame)
	{
		;
	}
	
	/*** auxiliary methods ***/
	public final static String getImagesDirLoc()
	{
		String dirLoc = "";
		
		try {
			dirLoc = new File(".").getCanonicalPath();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return dirLoc + "/src/org/ladder/tools/gui/demopanel/images/";
	}
	
	
	
	/*** fields ***/
	public static final String IMAGES_DIR_LOC = getImagesDirLoc();
}