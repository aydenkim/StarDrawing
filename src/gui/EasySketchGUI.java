package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jxl.write.WriteException;
import model.InteractionState;
import model.agent.AgentEmotionalState;
import model.agent.EasySketchAgent;
import ecologylab.serialization.SIMPLTranslationException;
import edu.tamu.core.gui.BackgroundImagePanel;
import edu.tamu.core.gui.JSketchCanvas;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;

/**
 * EasySketchGUI.java
 * 
 * 
 * <p>
 * 
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

public class EasySketchGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -69181062632290270L;

	public static final int DEFAULT_STROKE_WIDTH = 3;

	private JSketchCanvas sketchCanvas;
	private QuestionPanel questionPanel;
	private SketchToolbar sketchToolbar;
	private BackgroundImagePanel m_backgroundPanel;
	private JPanel navigationPanel;
	private static EasySketchAgent agent;
	private static boolean InstructorMode = true;

	private FeedbackPanel feedbackPanel;
	JMenuBar menuBar = new JMenuBar();

	private InstructionPanel instructionPanel;
	private EasySketchAgent mathsketch;
	private static EasySketchController sketchControl;

	public EasySketchGUI(EasySketchAgent mathsketch) {
		super();
		this.mathsketch = mathsketch;

		// now the graphics
		initLookAndFeel();
		initializeGUI();

	}

	private void initLookAndFeel() {
		try {
			UIManager
			.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}

	private void initializeGUI() {

		sketchCanvas = new JSketchCanvas();
		sketchCanvas.setPreferredSize(new Dimension(800, 700));
		sketchCanvas.setStrokeWidth(DEFAULT_STROKE_WIDTH);
		//sketchCanvas.setBackgroundImage("img/backgrounds/blackboard.jpg");

		// -----------------------------------------
		// Mode menu
		JMenu modeMenu = new JMenu("Mode");
		modeMenu.setMnemonic(KeyEvent.VK_H);

		// change to student mode
		JMenuItem studentMenuItem = new JMenuItem("Student Mode");
		studentMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		studentMenuItem.setMnemonic(KeyEvent.VK_H);
		studentMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InstructorMode = false;
			}
		});

		// change to instructor mode
		JMenuItem instructorMenuItem = new JMenuItem("Instructor Mode");
		instructorMenuItem.setMnemonic(KeyEvent.VK_A);
		instructorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InstructorMode = true;
			}
		});


		modeMenu.add(studentMenuItem);
		modeMenu.add(instructorMenuItem);
		//menuBar.add(modeMenu);

		//////////////////////////////////////////////////////////////

		sketchControl = new EasySketchController(sketchCanvas);
		sketchToolbar = new SketchToolbar(sketchControl, InstructorMode);
		sketchControl.setToolbar(sketchToolbar);

		instructionPanel = new InstructionPanel();
		questionPanel = new QuestionPanel();

		feedbackPanel = new FeedbackPanel();
		m_backgroundPanel = new BackgroundImagePanel();

		// both ways
		sketchCanvas.addObserver(mathsketch);
		sketchCanvas.addObserver(sketchControl);

		JPanel instructionsPanel = new JPanel(new BorderLayout());
		instructionsPanel.add(instructionPanel, BorderLayout.CENTER);
		instructionsPanel.add(questionPanel.getQuestionImagePanel(), BorderLayout.NORTH);

		JPanel workspacePanel = new JPanel(new BorderLayout());
		sketchToolbar.add(feedbackPanel);
		sketchToolbar.add(m_backgroundPanel);

		sketchToolbar.getNextButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					agent.processPenEvent(); // for test
					if(agent.getCurrentQuestionNumber() != 0){ // we will not check the test drawing

						if(agent.getCurrentState().getUserSketch().getStrokes().size() > 0){
							List<Stroke> strokes = agent.getCurrentState().getUserSketch().getStrokes();
							//sketchControl.saveSketchAs();
							agent.processPenEvent();
							sketchControl.clearSketch(); // clear the sketch
							agent.retrieveNextQuestion();
							agent.getCurrentState().setEmotionalState(AgentEmotionalState.SUBMIT);
							agent.getSpeechHelper().say("SUBMIT");

						}else{
							agent.getGUI().getFeedbackPanel().displayResponse("Please draw something", false);	
							agent.getCurrentState().setEmotionalState(AgentEmotionalState.NOTDONE);
							agent.getSpeechHelper().say("NOTDONE");
						}
					}else{
						sketchControl.clearSketch(); // clear the sketch
						agent.retrieveNextQuestion();
					}

				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		sketchToolbar.getPlayButton().addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				sketchControl.playSketch();
			}

		});

		sketchToolbar.getCheckButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							agent.processPenEvent();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (WriteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

		sketchToolbar.getSaveButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						sketchControl.saveSketchAs();
					}
				});
		
		sketchToolbar.getOpenButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						sketchControl.getToolbar().setPlayEnabled(true);
						sketchControl.openSketch();
					}
				});

		navigationPanel = new JPanel(new BorderLayout());
		navigationPanel.setOpaque(false);
		//setJMenuBar(menuBar);
		navigationPanel.add(questionPanel,BorderLayout.LINE_START);
		navigationPanel.add(sketchToolbar, BorderLayout.PAGE_END);

		workspacePanel.add(navigationPanel,BorderLayout.NORTH);
		//workspacePanel.add(menuBar,BorderLayout.CENTER);
		workspacePanel.add(sketchCanvas, BorderLayout.CENTER);
		//workspacePanel.add(sketchCanvas);
		
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				instructionsPanel, workspacePanel);
		mainSplitPane.setOneTouchExpandable(false);
		//setJMenuBar(menuBar);
		//add(navigationPanel, BorderLayout.PAGE_START);
		//add(workspacePanel, BorderLayout.CENTER);
		
		this.add(mainSplitPane);

		// ----
		int width = 1150;
		int height = 1100; // 800

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screenSize.width / 2) - (width / 2); // Center horizontally.
		int Y = (screenSize.height / 2) - (height / 2); // Center vertically.

		
		this.setBounds(X, Y, width, height);
		this.setPreferredSize(new Dimension(width, height));

		// center the jframe on screen
		this.setLocationRelativeTo(null);

		this.setMinimumSize(new Dimension(300, 300));
		this.setTitle("EasySketch"); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setJMenuBar(menuBar);
		this.pack();

	}

	/**
	 * return question panel
	 * 
	 * @return
	 */
	public QuestionPanel getQuestionPanel() {
		return questionPanel;
	}

	public EasySketchController getSketchController() {
		return sketchControl;
	}

	public JSketchCanvas getSketchCanvas(){
		return sketchCanvas;
	}

	/**
	 * return feedback panel
	 * 
	 * @param args
	 */
	public FeedbackPanel getFeedbackPanel() {
		return feedbackPanel;
	}

	public BackgroundImagePanel getBackgroundPanel(){
		return this.m_backgroundPanel;
	}

	public static void main(String[] args) throws SIMPLTranslationException {

		InteractionState s0 = new InteractionState();
		// The GUI is started by the agent mathsketch on start
		agent = new EasySketchAgent(s0);
		agent.start();



	}

	public InstructionPanel getInstructionPanel() {
		return instructionPanel;
	}

	public JPanel getNavigationPanel() {
		return navigationPanel;
	}

}
