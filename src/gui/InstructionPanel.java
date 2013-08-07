/**
 * InstructionPanel.java
 * 
 * Revision History:<br>
 * Nov 3, 2011 fvides - File created
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.InteractionObserver;
import model.InteractionState;
import model.Question;
import model.agent.AgentEmotionalState;
import model.agent.ReadExcel;
import model.agent.ReadExcel.ExcelContents;

public class InstructionPanel extends JPanel implements ActionListener,
InteractionObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1351558470647000600L;

	public static final Color BACK_COLOR = new Color(144, 238, 144); // 175, 238, 238
	private AgentEmotionalState currentStatus;
	private Map<AgentEmotionalState, ImageIcon> images;
	private Properties imageMappingFile = new Properties();
	private	JScrollPane scrollPane;

	/**
	 * This is the agent visually. It is a button so it is sensitive to clicks
	 * made by the kid
	 */

	private JLabel instructions = null;

	public InstructionPanel() {
		super();
		this.currentStatus = AgentEmotionalState.NEUTRAL;

		initializeGUI();

	}
	
	

	private void initializeGUI() {

		Font insFont = new Font("Helvetica", Font.PLAIN, 14);

		instructions = new JLabel("<html><p></p></html>");
		instructions.setFont(insFont);
		instructions.setAlignmentX(Component.CENTER_ALIGNMENT);

		setPreferredSize(new Dimension(240, 240));
		setMinimumSize(new Dimension(220, 0));

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(BACK_COLOR);


		this.add(Box.createRigidArea(new Dimension(0, 10)));

		this.add(instructions);
		this.add(Box.createRigidArea(new Dimension(0, 10)));
		setDefaultSet(true);


	}

	/**
	 * set instruction text
	 * @param question
	 */
	public void setCurrentQuestion(Question question) {
		char[] temp_instructions = new char[question.getHelpInstructions(0).length()*2];
		char[] instructions_array = question.getHelpInstructions(0).toCharArray();

		if(temp_instructions.length > 0){
			int instructions_index = 0;
			temp_instructions[0] = '<';
			temp_instructions[1] = 'h';
			temp_instructions[2] = 't';
			temp_instructions[3] = 'm';
			temp_instructions[4] = 'l';
			temp_instructions[5] = '>';

			instructions_index = 6;
			for(int i = 0; i < instructions_array.length; i++){
				if(i != 0 && (i % 24 == 0)){
					temp_instructions[instructions_index++] = instructions_array[i];
					temp_instructions[instructions_index++] = '<';
					temp_instructions[instructions_index++] = 'b';
					temp_instructions[instructions_index++] = 'r';
					temp_instructions[instructions_index++] = '>';
				}else{
					temp_instructions[instructions_index++] = instructions_array[i];
				}
			}
			//temp_instructions[instructions_index++] = '/';
			temp_instructions[instructions_index++] = '<';
			temp_instructions[instructions_index++] = 'h';
			temp_instructions[instructions_index++] = 't';
			temp_instructions[instructions_index++] = 'm';
			temp_instructions[instructions_index++] = 'l';
			temp_instructions[instructions_index++] = '>';
			instructions.setText(new String(temp_instructions));
		}
		
	}
	
	public JLabel getInstructions(){
		return instructions;
	}
	
	public void setDefaultSet(Boolean turnOn){
		if(turnOn){
			JPanel defaultPanel = new DefaultQuestionPanel();
			//this.add(defaultPanel);
		}
	}
	
	public int Summarize(String filePath) throws IOException{
		
		
        
		ExcelContents contents;
		ReadExcel excel = new ReadExcel();
		excel.setInputFile(filePath);
		excel.read();
		contents = excel.getContents();
        int tryCount = 0;

		ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();
		
		// TODO support multiple images and multiple instructions depending on
		// the help level
		for (int i = 1; i < contents.cell1.size(); i++) {
			if(!contents.cell1.get(i).isEmpty()){
				list.add(new KeyValuePair(contents.cell2.get(i), contents.cell3.get(i)));
				tryCount = tryCount + Integer.parseInt(contents.cell3.get(i));
			}
		}
		
		
		Double total = Double.parseDouble(Integer.toString(contents.cell1.size()-1));
		Double tryDouble = Double.parseDouble(Integer.toString(tryCount));
		String score = Double.toString(Math.round(total/tryDouble*100)) + "%";

		int scoreInt = (int) Math.round(total/tryDouble*100);
		
		list.add(new KeyValuePair("Total", score));
		
		// Instantiate JTable and DefaultTableModel, and set it as the
		// TableModel for the JTable.
		JTable table = new JTable();
		DefaultTableModel model = new DefaultTableModel();
		table.setModel(model);
		model.setColumnIdentifiers(new String[] {"Shape", "You tried"});

		// Populate the JTable (TableModel) with data from ArrayList
		for (KeyValuePair p : list)
		{
		    model.addRow(new String[] {p.key, p.value});
		}

		// Add the table to a scrolling pane
		scrollPane = new JScrollPane( table );
		this.add(scrollPane);
		
		return scoreInt;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		//updateImage(currentStatus);

	}

	@Override
	public void stateChanged(InteractionState state) {

		//updateImage(currentStatus); 

	}

	class KeyValuePair
	{
	    public String key;
	    public String value;

	    public KeyValuePair(String k, String v)
	    {
	        key = k;
	        value = v;
	    }
	}
}
