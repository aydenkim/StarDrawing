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
package org.ladder.tools.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.ladder.tools.gui.Layer;
import org.ladder.tools.gui.LayerManager;
import org.ladder.tools.gui.event.LayerChangeEvent;
import org.ladder.tools.gui.event.LayerChangeEventListener;

public class LayerWidget extends JPanel implements LayerChangeEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 216174810601508167L;
	private final Layer layer;
	private final LayerManager manager;
	private JButton visible;
	private JButton active;
	private JLabel name;
	private JPanel m_container;
	
	public LayerWidget(Layer l, LayerManager lm) {
		super();
		
		layer = l;
		manager = lm;
		
		m_container = new JPanel();
		m_container.setLayout(new BoxLayout(m_container, BoxLayout.X_AXIS));
		
		name = new JLabel(l.getName());
		name.addMouseListener(new MouseListener() {

			/* (non-Javadoc)
             * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(MouseEvent e) {
            	manager.activateLayer(layer.getName());
            }

			/* (non-Javadoc)
             * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseEntered(MouseEvent e) {
            }

			/* (non-Javadoc)
             * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseExited(MouseEvent e) {
            }

			/* (non-Javadoc)
             * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
             */
            @Override
            public void mousePressed(MouseEvent e) {
            }

			/* (non-Javadoc)
             * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseReleased(MouseEvent e) {
            }
			
		});
		
		visible = new JButton("Hide");
		
		active = new JButton("Activate");
		if (manager.getActiveLayer() == layer) {
			setBorder(BorderFactory.createTitledBorder("Active Layer"));
			active.setEnabled(false);
		}
		visible.addActionListener(new ActionListener() {
			
			private boolean b_visible = false;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				manager.setVisible(layer.getName(), b_visible);
				if (b_visible) {
					((JButton)arg0.getSource()).setText("Hide");
				} else {
					((JButton)arg0.getSource()).setText("Show");
				}
				b_visible  = ! b_visible;
				
			}
			
		});
		
		active.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				manager.activateLayer(layer.getName());
			}
			
		});
						
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		name.setPreferredSize(new Dimension(150, 35));
		name.setAlignmentX(0.5f);
		visible.setPreferredSize(new Dimension(75, 35));
		active.setPreferredSize(new Dimension(75, 35));
		
		setPreferredSize(new Dimension(180, 80));
		
		add(Box.createVerticalStrut(3));
		add(name);
		add(Box.createVerticalGlue());
		m_container.add(visible);
		m_container.add(Box.createHorizontalStrut(3));
		m_container.add(active);
		add(m_container);
		add(Box.createVerticalStrut(3));
		
		manager.addLayerChangeEventListener(this);
	}

	@Override
	public void changeLayers(LayerChangeEvent lce) {
		if (layer == lce.getActiveLayer()) {
			active.setEnabled(false);
			setBorder(BorderFactory.createTitledBorder("Active Layer"));
		} else if (layer == lce.getOldLayer()) {
			active.setEnabled(true);
			setBorder(null);
		}
		this.repaint();
	}
}
