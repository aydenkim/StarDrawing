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
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.ladder.core.sketch.IShape;
import org.ladder.core.sketch.IStroke;
import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.Point;
import org.ladder.tools.StrokeManager;
import org.ladder.tools.gui.Layer;

import edu.tamu.recognition.IRecognitionResult;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import edu.tamu.recognition.paleo.StrokeFeatures;
import edu.tamu.tools.graph.Plot;

public class IntersectionSegmentLayer extends Layer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5061615625702771401L;
	
	/**
	 * Radius of the points
	 */
	private static final double S_POINT_RADIUS = 5.0;
	
	private SRLMenuBar m_menuBar = new SRLMenuBar(true);
	
	private SRLToolPalette m_toolPalette = new SRLToolPalette("Paleo Tools");
	
	private static final String name = "IntersectionSegmentLayer";
	
	/**
	 * Map of recognized strokes
	 */
	private HashMap<IStroke, Boolean> m_recognized = new HashMap<IStroke, Boolean>();
	
	/**
	 * Paleo configuration to use
	 */
	private PaleoConfig m_paleoConfig = new PaleoConfig();
	
	private List<IShape> m_shapes = new ArrayList<IShape>();
	
	/**
	 * things recognized as Line by Paleo
	 */
	private List<IShape> m_lines = new ArrayList<IShape>();
	/**
	 * segmentation points for each shape
	 */
	private HashMap<IShape,ArrayList<IPoint>> m_segpoints = new HashMap<IShape,ArrayList<IPoint>>();
	
	
	/**
	 * 
	 */
	public IntersectionSegmentLayer(StrokeManager strokeManager) {
		super(strokeManager);
		
		m_paleoConfig = PaleoConfig.deepGreenConfig();
		initializeMenuBar();
		initializeToolPalette();
		
		setOpaque(false);
		setDrawColor(Color.GREEN);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.tools.gui.Layer#registerYourselfAsMouseListener()
	 */
	@Override
	public void registerYourselfAsMouseListener() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	

	/**
	 * Get the Name
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	

	private void initializeMenuBar() {
		m_menuBar = new SRLMenuBar(true);
		
		JMenuItem save = m_menuBar.getSaveItem();
		JMenuItem open = m_menuBar.getOpenItem();
		JMenuItem close = m_menuBar.getCloseItem();
		
		JMenuItem redo = m_menuBar.getRedoItem();
		JMenuItem undo = m_menuBar.getUndoItem();
		
		save.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		open.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		undo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				undo();
			}
		});
		
		redo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				redo();
			}
		});
		
		JMenu file = m_menuBar.getFileMenu();
		JMenuItem clear = new JMenuItem("Clear");
		file.add(clear);
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
	}
	

	/**
	 * Clear the panel of all strokes
	 */
	public void clear() {
		m_shapes.clear();
		m_selected.clear();
		m_recognized.clear();
		m_bufferedGraphics = null;
		m_lines.clear();
		m_segpoints.clear();
		refreshScreen();
	}
	

	private void initializeToolPalette() {
		JButton recognize = new JButton("Beautify");
		recognize.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				beautify();
			}
			
		});
		m_toolPalette.addButton(recognize);
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
			
		});
		m_toolPalette.addButton(clear);
	}
	

	@Override
	public SRLMenuBar getMenuBar() {
		return m_menuBar;
	}
	

	@Override
	public List<SRLToolPalette> getToolPalettes() {
		List<SRLToolPalette> list = new ArrayList<SRLToolPalette>();
		list.add(m_toolPalette);
		return list;
	}
	

	@Override
	public void redo() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void setStatusBar(SRLStatusBar status) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void setStrokeManager(StrokeManager strokeManager) {
		m_strokeManager = strokeManager;
	}
	

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}
	

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void mousePressed(MouseEvent arg0) {
		trySelection(arg0);
		showContextMenu(arg0);
	}
	

	@Override
	public void mouseReleased(MouseEvent arg0) {
		trySelection(arg0);
		showContextMenu(arg0);
	}
	

	/**
	 * Return a contextually significant popup menu for Paleo features based on
	 * where they are clicking.
	 * 
	 * @param e
	 * @return
	 */
	protected void buildContextMenu(MouseEvent e) {
		contextMenu = new JPopupMenu();
		
		JMenuItem direction = new JMenuItem("Show Direction Graph");
		JMenuItem curvature = new JMenuItem("Show Curvature Graph");
		JMenuItem clear = new JMenuItem("Clear");
		JMenuItem beautify = new JMenuItem("Beautify");
		
		direction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				plotDirection();
			}
			
		});
		
		curvature.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				plotCurvature();
			}
			
		});
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
			
		});
		
		beautify.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				beautify();
			}
			
		});
		
		// contextMenu.add(undo); // Not really Implementing undo/redo
		// contextMenu.add(redo);
		
		if (clickedOnAStroke(e) || m_selected.size() > 0) {
			contextMenu.add(direction);
			contextMenu.add(curvature);
		}
		contextMenu.add(beautify);
		contextMenu.add(clear);
		
	}
	

	/**
	 * Display beautified shapes for the strokes on screen.
	 */
	private void beautify() {
		List<IStroke> strokes = m_strokeManager.getStrokes();
		if (m_selected.size() > 0)
			strokes = m_selected;
		for (int i = 0; i < strokes.size(); i++) {
			if (m_recognized.containsKey(strokes.get(i)))
				continue;
			PaleoSketchRecognizer paleo = new PaleoSketchRecognizer(
			        m_paleoConfig);
			paleo.setStroke(strokes.get(i));
			IRecognitionResult shapes = paleo.recognize();
			m_recognized.put(strokes.get(i), true);
			if (shapes.getNumInterpretations() > 0) {
				// System.out.println("");
				// for (IShape s : shapes.get(0).getNBestList())
				// System.out.println(s.getLabel());
				IShape best = shapes.getBestShape();
				m_shapes.add(best);
				if (best.getLabel().equals("Line"))
				{
					m_lines.add(best);
				}
				
				
				// System.out.println("");
			}
		}
		
		long startTime = System.currentTimeMillis();
		
		segmentIntersection();
		
		long endTime = System.currentTimeMillis();
		System.err.println("Time: " + (endTime-startTime) + "ms");
		refreshScreen();
	}
	
	private void segmentIntersection()
	{
		for (int i = 0; i < m_lines.size(); ++i)
		{
			IStroke is = m_lines.get(i).getFirstStroke();
			IPoint[] ip = {is.getFirstPoint(), is.getLastPoint()};
			
			for (int j = i+1; j < m_lines.size(); ++j)
			{
				IStroke js = m_lines.get(j).getFirstStroke();
				IPoint[] jp = {js.getFirstPoint(), js.getLastPoint()};
				/* this math taken from http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/ */
				double d1 = ((jp[1].getY()-jp[0].getY()) * (ip[1].getX()-ip[0].getX()));
				double d2 = ((jp[1].getX()-jp[0].getX()) * (ip[1].getY()-ip[0].getY()));
				
				double d = d1-d2;
				/* parallel case */
				if (d == 0) continue;
				double ui = (((jp[1].getX()-jp[0].getX()) * (ip[0].getY()-jp[0].getY())) - 
						       ((jp[1].getY()-jp[0].getY()) * (ip[0].getX()-jp[0].getX())) ) / d;
				
				double uj = (((ip[1].getX()-ip[0].getX()) * (ip[0].getY()-jp[0].getY())) - 
						      ((ip[1].getY()-ip[0].getY()) * (ip[0].getX()-jp[0].getX()))) / d;
				
				IPoint intersection = new Point(
						ip[0].getX()+ui*(ip[1].getX()-ip[0].getX()), 
						ip[0].getY()+ui*(ip[1].getY()-ip[0].getY()));
				
				/* if I is intersected */
				if (ui > .1 && ui < .9 && uj > -.1 && uj < 1.1) 
				{
					if (!m_segpoints.containsKey(m_lines.get(i)))
						m_segpoints.put(m_lines.get(i),new ArrayList<IPoint>());
					m_segpoints.get(m_lines.get(i)).add(intersection);
				}
				/* if J is intersected */
				if (uj > .1 && uj < .9 && ui > -.1 && ui < 1.1) 
				{
					if (!m_segpoints.containsKey(m_lines.get(j)))
						m_segpoints.put(m_lines.get(j),new ArrayList<IPoint>());
					m_segpoints.get(m_lines.get(j)).add(intersection);
				}
			}
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	/**
	 * Refresh the draw panel (re-draw all visible elements of the sketch
	 * object)
	 */
	protected void refreshScreen() {
		Map<IStroke, Boolean> paintedStrokes = new HashMap<IStroke, Boolean>();
		// To avoid null pointer exception
		if (m_bufferedGraphics == null) {
			m_bufferedGraphics = new BufferedImage(getWidth(), getHeight(),
			        BufferedImage.TYPE_INT_ARGB);
		}
		
		// paint all shapes in the sketch
		for (IShape s : m_shapes) {
			s.setColor(this.getDrawColor());
			//paintShape(m_bufferedGraphics.getGraphics(), s, paintedStrokes);
			//labelShape(m_bufferedGraphics.getGraphics(), s, s.getLabel());
			
			// add strokes to list that has been painted
			for (IStroke st : s.getStrokes())
				paintedStrokes.put(st, true);
		}
		
		for (IStroke s : m_selected) {
			s.setColor(Color.ORANGE);
			paintStroke(m_bufferedGraphics.getGraphics(), s);
		}
		
		for (ArrayList<IPoint> al : m_segpoints.values() )
			for (IPoint ip : al)
				paintPoint(m_bufferedGraphics.getGraphics(), ip, Color.magenta);
		
		this.repaint();
	}
	
	/**
	 * Paints the given point onto the graphics
	 * 
	 * 
	 * @param g
	 *            Graphics object to paint to
	 * @param p
	 *            Point to paint
	 * @param c
	 *            Color to paint the point
	 */
	protected void paintPoint(Graphics g, IPoint p, Color c) {
		
		// if (m_paintedPoints.containsKey(p))
		// return;
		
		g.setColor(c);
		
		g.fillOval((int) (p.getX() - S_POINT_RADIUS),
		        (int) (p.getY() - S_POINT_RADIUS),
		        (int) (S_POINT_RADIUS * 2.0), (int) (S_POINT_RADIUS * 2.0));
	}
	

	/**
	 * Plot curvature graph of strokes
	 */
	public void plotCurvature() {
		for (int i = m_selected.size() - 1; i >= 0; i--) {
			StrokeFeatures sf = new StrokeFeatures(m_selected.get(i),
			        m_paleoConfig.getHeuristics().FILTER_DIR_GRAPH);
			Plot plot = new Plot("Stroke " + i + " Curvature");
			plot
			        .addLine(sf.getLengthSoFar2nd(), sf.getCurvature(),
			                Color.black);
			plot.plot();
		}
	}
	

	/**
	 * Plot speed graph of strokes
	 */
	public void plotDirection() {
		for (int i = m_selected.size() - 1; i >= 0; i--) {
			StrokeFeatures sf = new StrokeFeatures(m_selected.get(i),
			        m_paleoConfig.getHeuristics().FILTER_DIR_GRAPH);
			Plot plot = new Plot("Stroke " + i + " Direction");
			plot.addLine(sf.getLengthSoFar(), sf.getDir(), Color.black);
			plot.plot();
		}
	}
	
}
