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
package org.ladder.tools.gui.menus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * This class simply overrides the JMenu class to allow the easy display
 * of menu items alphabetically according to the attached text.
 * @author tracy
 *
 */
public class SortedMenu extends JMenu {

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -5107698940349529023L;

	public SortedMenu(String string) {
		super(string);
	}

	/**
	 * When adding in a JMenuItem, it inserts them in a sorted order
	 * according to the text of the JMenuItem.
	 * If assumes that things in the list are already sorted, else
	 * it may insert into the wrong point.
	 * @param item the JMenuItem to be inserted alphabetically
	 * @return the inserted JMenuItem
	 */
	public JMenuItem add (JMenuItem item){
	  boolean inserted = false;
	  //traverses through each item and inserts in the appropriate location
	  for(int pos = 0; pos < super.getItemCount(); pos++){
		 JMenuItem nextitem = super.getItem(pos);
		 if(nextitem.getText().compareToIgnoreCase(item.getText()) > 0){
		   super.insert(item, pos);		   
		   inserted = true;
		   break;
		 }
	  }
	  if (!inserted){
		super.add(item);
	  }
      return item;		
	}	
}
