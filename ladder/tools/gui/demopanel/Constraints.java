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

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class Constraints extends GridBagConstraints
{
	/*** constructor ***/
	private Constraints(Builder builder)
	{
		gridx 		= builder.gridx;
		gridy 		= builder.gridy;
		
		anchor 		= builder.anchor;
		fill		= builder.fill;
		gridheight 	= builder.gridheight;
		gridwidth 	= builder.gridwidth;
		ipadx 		= builder.ipadx;
		ipady 		= builder.ipady;
		insets		= builder.insets;
		weightx		= builder.weightx;
		weighty		= builder.weighty;
	}
	
	public Constraints(int gridx, int gridy)
	{
		this.gridx 		= gridx;
		this.gridy 		= gridy;
	}
	
	/*** inner class ***/
	public static class Builder
	{
		/*** constructor ***/
		// Defaults the cell to grid position (0, 0).
		public Builder()
		{	this(0, 0);	}
		
		// Specifies the cell containing the leading edge of the component's display area, where the first cell in a row has gridx=0.
		// Specifies the cell at the top of the component's display area, where the topmost cell has gridy=0.
		public Builder(int gridx, int gridy)
		{	this.gridx = gridx;	this.gridy = gridy;	}
		
		/*** builder ***/
		public Constraints build()
		{	return new Constraints(this);		}

		/*** setters ***/
		// Used when the component is smaller than its display area. It determines where, within the display area, to place the component.
		public Builder anchor(int value)
		{	anchor = value; return this;		}
		
		// Used when the component's display area is larger than the component's requested size. It determines whether to resize the component, and if so, how. 
		public Builder fill(int value)
		{	fill = value; return this;			}
		
		// Specifies the number of cells in a row for the component's display area. 
		public Builder gridheight(int value)
		{	gridheight = value;	return this;	}
		
		// Specifies the number of cells in a column for the component's display area. 
		public Builder gridwidth(int value)
		{	gridwidth = value;	return this;	}
		
		// Specifies the internal padding of the component, how much space to add to the minimum width of the component. The width of the component is at least its minimum width plus (ipadx * 2) pixels. 
		public Builder ipadx(int value)
		{	ipadx = value;	return this;		}
		
		// Specifies the internal padding, that is, how much space to add to the minimum height of the component. The height of the component is at least its minimum height plus (ipady * 2) pixels. 
		public Builder ipady(int value)
		{	ipady = value;	return this;		}
		
		// Specifies the external padding of the component, the minimum amount of space between the component and the edges of its display area.
		public Builder insets(Insets value)
		{	insets = value;	return this;		}

		// Specifies how to distribute extra horizontal space.
		public Builder weightx(int value)
		{	weightx = value;	return this;	}
		
		// Specifies how to distribute extra vertical space.
		public Builder weighty(int value)
		{	weighty = value;	return this;	}
		
		/*** fields ***/
		// required fields
		private final int gridx;
		private final int gridy;
		
		// optional fields (set to default fields)
		private int anchor			= GridBagConstraints.CENTER;
		private int fill			= GridBagConstraints.NONE;
		private int gridheight		= 1;
		private int gridwidth		= 1;
		private int ipadx			= 0;
		private int ipady			= 0;
		private int weightx			= 0;
		private int weighty			= 0;
		private Insets insets		= new Insets(0, 0, 0, 0);
	}
	
	private static final long serialVersionUID = 1L;
}