package gui;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/*
 * DefaultQuestionPanel.java requires these files:
 *   images/Bird.gif
 *   images/Cat.gif
 *   images/Dog.gif
 *   images/Rabbit.gif
 *   images/Pig.gif
 */
public class DefaultQuestionPanel extends JPanel
implements ActionListener {
	static String girlString = "Girl";
	static String boyString = "Boy";


	JLabel genderLabel;
	JLabel ageLabel;
	JTextField ageText;
	JPanel contentPanel;

	public DefaultQuestionPanel() {

		int y_value = 1; 
		contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		//contentPanel.setBackground(Color.WHITE);

		//Create the radio buttons.
		JRadioButton girlButton = new JRadioButton(girlString);
		girlButton.setActionCommand(girlString);
		girlButton.setSelected(true);

		JRadioButton boyButton = new JRadioButton(boyString);
		boyButton.setMnemonic(KeyEvent.VK_C);
		boyButton.setActionCommand(boyString);

		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(girlButton);
		group.add(boyButton);

		//Register a listener for the radio buttons.
		girlButton.addActionListener(this);
		boyButton.addActionListener(this);

		//Put the radio buttons in a column in a panel.
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(girlButton);
		radioPanel.add(boyButton);

		// AnswerPanel
		//Set up the question label.
		genderLabel = new JLabel("What is your gender? ");
		ageLabel = new JLabel("How old are you? ");

		JPanel genderPanel =new JPanel();		
		genderPanel.add(genderLabel, BorderLayout.LINE_START);
		genderPanel.add(radioPanel, BorderLayout.CENTER);


		GridBagConstraints genderGBC = new GridBagConstraints();
		genderGBC.gridx = 0;
		genderGBC.gridy = y_value;

		contentPanel.add(genderPanel,genderGBC);

		// Compression
		JPanel agePane = new JPanel();
		//ageLabel = new JLabel("How old are you? ");
		ageText = new JTextField();
		ageText.setSize(300, 300);
		//agePane.add(ageLabel, BorderLayout.LINE_START);
		agePane.add(ageText, BorderLayout.CENTER);
		GridBagConstraints agePaneGBC = new GridBagConstraints();
		agePaneGBC.gridx = 0;
		agePaneGBC.gridy = ++y_value;
		contentPanel.add(agePane,agePaneGBC);

		this.add(contentPanel);

	}

	/** Listens to the radio buttons. */
	public void actionPerformed(ActionEvent e) {
		/* picture.setIcon(createImageIcon("images/"
                                        + e.getActionCommand()
                                        + ".gif"));*/
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("DefaultQuestionPanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		JComponent newContentPane = new DefaultQuestionPanel();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}