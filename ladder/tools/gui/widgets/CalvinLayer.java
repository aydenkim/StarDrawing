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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.xml.parsers.ParserConfigurationException;

import org.ladder.core.config.LadderConfig;
import org.ladder.core.sketch.IShape;
import org.ladder.core.sketch.IStroke;
import org.ladder.tools.StrokeManager;
import org.ladder.tools.gui.Layer;
import org.ladder.tools.gui.menus.SortedMenu;
import org.xml.sax.SAXException;

import edu.tamu.deepGreen.DeepGreenSketchRecognizer;
import edu.tamu.deepGreen.SIDCLookup;
import edu.tamu.recognition.IRecognitionResult;
import edu.tamu.recognition.IRecognitionResultListener;
import edu.tamu.recognition.RecognitionManager;
import edu.tamu.recognition.constraint.CALVIN;
import edu.tamu.recognition.constraint.IConstraint;
import edu.tamu.recognition.constraint.domains.DomainDefinition;
import edu.tamu.recognition.constraint.domains.ShapeDefinition;
import edu.tamu.recognition.constraint.domains.io.DomainDefinitionInputDOM;
import edu.tamu.recognition.paleo.PaleoConfig;
import edu.tamu.recognition.paleo.PaleoSketchRecognizer;
import edu.tamu.recognition.recognizer.OverTimeException;
import edu.tamu.tools.gui.event.BuildShapeDataEvent;
import edu.tamu.tools.gui.event.BuildShapeEvent;
import edu.tamu.tools.gui.event.BuildShapeEventListener;
import edu.tamu.tools.gui.event.PossibleShapesEvent;

public class CalvinLayer extends Layer implements BuildShapeEventListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7302106874728480031L;
	
	private SRLMenuBar m_menuBar = new SRLMenuBar(true);
	
	private SRLToolPalette m_toolPalette = new SRLToolPalette("Calvin Tools",
	        5, 2);
	
	private static final String name = "CalvinLayer";
	
	private String domainFilename;
	
	private List<JMenuItem> disabled = new ArrayList<JMenuItem>();
	
	// private CALVIN calvin;
	private RecognitionManager m_rec;
	
	// private SRLStatusBar status;
	
	/**
	 * The last time that a call to recognize() was made, for pruning the
	 * results to only those things that have changed since then
	 */
	private long m_lastRecognitionTime = 0;
	
	/**
	 * Location of the COA domain description
	 */
	private static final String S_COA_DOMAIN_DESCRIPTION = LadderConfig
	        .getProperty(LadderConfig.DOMAIN_DESC_LOC_KEY)
	                                                       + LadderConfig
	                                                               .getProperty(LadderConfig.DEFAULT_LOAD_DOMAIN_KEY);
	
	/**
	 * List of recognition results (n-best lists)
	 */
	private List<IRecognitionResult> m_bestList = new ArrayList<IRecognitionResult>();
	
	/**
	 * Low-level recognizer
	 */
	private PaleoSketchRecognizer m_lowLevelRecognizer;
	
	/**
	 * Paleo configuration to use
	 */
	private PaleoConfig m_paleoConfig;
	
	private SRLToolPalette m_possibleShapes;
	
	
	/**
	 * 
	 */
	public CalvinLayer(StrokeManager strokeManager) {
		super(strokeManager);
		loadDomain(S_COA_DOMAIN_DESCRIPTION);
		initializeMenuBar();
		initializeToolPalette();
		setOpaque(false);
		setDrawColor(Color.BLUE);
		
		// Primitive recognizer
		m_paleoConfig = PaleoConfig.deepGreenConfig();
		m_lowLevelRecognizer = new PaleoSketchRecognizer(m_paleoConfig);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.tools.gui.Layer#registerYourselfAsMouseListener()
	 */
	@Override
	public void registerYourselfAsMouseListener() {
		// add listeners
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
		
		JMenuItem openDomain = new JMenuItem("Open Domain");
		JMenuItem reloadDomain = new JMenuItem("Reload Domain");
		
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
		
		openDomain.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				String s = getDomainStringFromDialog();
				if (!s.equals("")) {
					loadDomain(s);
				}
				
			}
		});
		
		reloadDomain.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				loadDomain(domainFilename);
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
		file.add(openDomain);
		file.add(reloadDomain);
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
		});
		
		SortedMenu debugShape = new SortedMenu("Debug Shape");
		
		// Test Shape Chooser
		DomainDefinition domain = m_rec.getDomain();
		List<ShapeDefinition> shapeDefs = domain.getShapeDefinitions();
		int numShapeDefs = shapeDefs.size();
		for (int i = 0; i < numShapeDefs; i++) {
			JMenuItem tmpMenuItem = new JMenuItem(shapeDefs.get(i).getName());
			tmpMenuItem.setFont(new Font("Big Menu", Font.PLAIN, 8));
			debugShape.add(tmpMenuItem);
			tmpMenuItem.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					JMenuItem mi = (JMenuItem) e.getSource();
					setDebugShape(mi.getText());
					m_statusBar
					        .setStatus(mi.getText() + " set as debug shape.");
				}
			});
		}
		m_menuBar.add(debugShape);
	}
	

	private String getDomainStringFromDialog() {
		String s = (String) JOptionPane.showInputDialog(this,
		        "Load a domain definition file:\n", "Load Domain Definition",
		        JOptionPane.PLAIN_MESSAGE, null, null, "");
		
		// If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
			return s;
		}
		else {
			return "";
		}
	}
	

	private void loadDomain(String domain) {
		domainFilename = domain;
		
		// CALVIN
		try {
			DomainDefinition coaSymbols;
			coaSymbols = new DomainDefinitionInputDOM()
			        .readDomainDefinitionFromFile(domainFilename);
			
			m_rec = new RecognitionManager(coaSymbols);
			m_rec.getCalvin().addBuildShapeEventListener(this);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.tools.gui.Layer#clear()
	 */
	public void clear() {
		m_selected.clear();
		m_bufferedGraphics = null;
		
		m_bestList.clear();
		labelLocations.clear();
		m_rec.clear();
		
		refreshScreen();
	}
	

	private void initializeToolPalette() {
		JButton recognize = new JButton("Recognize");
		recognize.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recognize();
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
		
		JButton debug = new JButton("Debug All");
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_rec.getCalvin().toggleDebug();
			}
			
		});
		m_toolPalette.addButton(debug);
		
		final JComboBox debugBox = new JComboBox();
		debugBox.addItem("");
		for (ShapeDefinition shapeDef : m_rec.getDomain().getShapeDefinitions()) {
			debugBox.addItem(shapeDef.getName());
		}
		debugBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				String deb = debugBox.getSelectedItem().toString();
				setDebugShape(deb);
				m_statusBar.setStatus(deb + " set as debug shape.");
			}
		});
		m_toolPalette.add(debugBox);
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
	

	// @Override
	// public void setStatusBar(SRLStatusBar status) {
	// this.m_statusBar = status;
	// }
	
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
		// trySelection(arg0);
		super.setFromX(arg0.getX());
		super.setFromY(arg0.getY());
		showContextMenu(arg0);
	}
	

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (super.isDragged()) {
			multipleSelection();
		}
		else {
			trySelection(arg0);
		}
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
		
		JMenuItem undo = new JMenuItem("Undo");
		JMenuItem redo = new JMenuItem("Redo");
		JMenuItem clear = new JMenuItem("Clear");
		JMenuItem recognize = new JMenuItem("Recognize");
		
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear();
			}
			
		});
		
		recognize.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recognize();
			}
			
		});
		
		// contextMenu.add(undo); // Not really Implementing undo/redo
		// contextMenu.add(redo);
		
		contextMenu.add(recognize);
		contextMenu.add(clear);
		
	}
	

	/**
	 * Display beautified shapes for the strokes on screen.
	 */
	private void recognize() {
		List<IStroke> strokes = m_strokeManager.getStrokes();
		if (m_selected.size() > 0)
			strokes = m_selected;
		addStrokes(strokes);
		m_bestList = m_rec.recognize();
		refreshScreen();
	}
	

	@Override
	public void mouseDragged(MouseEvent arg0) {
		this.dragging(arg0);
		
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
		// To avoid null pointer exception
		if (m_bufferedGraphics == null) {
			m_bufferedGraphics = new BufferedImage(getWidth(), getHeight(),
			        BufferedImage.TYPE_INT_ARGB);
		}
		
		// Paint m_bestList results if they exist
		for (IRecognitionResult res : m_bestList) {
			res.sortNBestList();
			for (IShape shape : res.getNBestList()) {
				String str = shape.getLabel() + ": ";
				if (String.valueOf(shape.getConfidence()).length() >= 4)
					str += String.valueOf(shape.getConfidence())
					        .substring(0, 4);
				else
					str += String.valueOf(shape.getConfidence());
				labelShape(m_bufferedGraphics.getGraphics(), shape, str);
			}
		}
		
		if (super.getSelectionRect() != null) {
			System.out.println("Painting the rect.");
			Rectangle2D.Double rect = super.getSelectionRect();
			m_bufferedGraphics.getGraphics().setColor(Color.BLACK);
			m_bufferedGraphics.getGraphics().drawRect((int) rect.getX(),
			        (int) rect.getY(), (int) rect.getWidth(),
			        (int) rect.getHeight());
			
		}
		
		for (IStroke s : m_selected) {
			s.setColor(Color.ORANGE);
			paintStroke(m_bufferedGraphics.getGraphics(), s);
			s.setColor(Color.BLACK);
		}
		
		this.repaint();
	}
	

	/**
	 * Add a stroke to the recognition pool of strokes that need to be
	 * recognized
	 * 
	 * @param stroke
	 *            stroke to add
	 */
	public void addStroke(IStroke stroke) {
		
		// List<IShape> addedShapes = new ArrayList<IShape>();
		// m_lowLevelRecognizer.setStroke(stroke);
		// IRecognitionResult primitiveResults =
		// m_lowLevelRecognizer.recognize();
		// //System.out.println(primitiveResults);
		// IShape bestPrimitive = primitiveResults.getBestShape();
		// //System.out.println(bestPrimitive);
		// // complex or polyline fit - add sub shapes instead
		// if (bestPrimitive.getSubShapes().size() > 0) {
		// for (IShape s : bestPrimitive.getSubShapes()) {
		// calvin.submitForRecognition(s);
		// addedShapes.add(s);
		// }
		// }
		// else {
		// calvin.submitForRecognition(bestPrimitive);
		// addedShapes.add(bestPrimitive);
		// }
		
		m_rec.addStroke(stroke);
	}
	

	/**
	 * Add a list of stroke to the recognition pool of strokes that need to be
	 * recognized
	 * 
	 * @param strokes
	 *            list of strokes to add
	 */
	public void addStrokes(List<IStroke> strokes) {
		for (IStroke stroke : strokes) {
			addStroke(stroke);
		}
	}
	

	/**
	 * Set the shape to get debug information for.
	 * 
	 * @param shape
	 */
	public void setDebugShape(String shape) {
		m_rec.getCalvin().setDebugShape(shape);
	}
	

	@Override
	public void handleBuildShape(BuildShapeEvent e) {
		if (e instanceof BuildShapeDataEvent) {
			BuildShapeDataEvent bsde = (BuildShapeDataEvent) e;
			SRLToolPalette stp = new SRLToolPalette("Building Shape :: "
			                                        + bsde.getShapeDef()
			                                                .getName());
			
			List<String> candidates = new ArrayList<String>();
			for (IShape s : bsde.getCandidates()) {
				String id = s.getID().toString();
				id = id.substring(id.length() - 5, id.length() - 1);
				candidates.add(s.getLabel() + ":" + id);
			}
			
			List<String> constraints = new ArrayList<String>();
			for (IConstraint c : bsde.getConstraints()) {
				constraints.add(c.getName());
			}
			
			MatrixDisplay constraintsDisplay = new MatrixDisplay(candidates,
			        constraints, bsde.getConstraintsMap(),
			        "Candidates To Constraints");
			MatrixDisplay componentsDisplay = new MatrixDisplay(candidates,
			        bsde.getComponents(), bsde.getComponentsMap(),
			        "Candidates To Components");
			stp
			        .setLayout(new BoxLayout(stp.getContentPane(),
			                BoxLayout.Y_AXIS));
			stp.setSize(new Dimension(800, 800));
			stp.setLocation(getX(), getY());
			stp.add(constraintsDisplay);
			stp.add(componentsDisplay);
			stp.pack();
			m_possibleShapes = stp;
			stp.setVisible(true);
		}
		
	}
	

	@Override
	public void handlePossibleShapes(BuildShapeEvent e) {
		if (e instanceof PossibleShapesEvent) {
			PossibleShapesEvent pse = (PossibleShapesEvent) e;
			if (!pse.isNone()
			    && pse.getShapes().size() > 1
			    && (m_possibleShapes == null || !m_possibleShapes.getTitle()
			            .contains(pse.getForShape()))) {
				SRLToolPalette stp = new SRLToolPalette("Possible Shapes :: "
				                                        + pse.getForShape());
				JList list = new JList(pse.getShapes().toArray());
				JScrollPane sp = new JScrollPane(list);
				stp.setLayout(new BorderLayout());
				stp.setSize(new Dimension(200, 200));
				stp.setLocation(getX(), getY());
				stp.add(sp);
				// stp.pack();
				m_possibleShapes = stp;
				stp.setVisible(true);
			}
		}
		
	}
}
