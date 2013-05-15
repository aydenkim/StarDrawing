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
package org.ladder.recognition.bullseye;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.ladder.core.sketch.IShape;
import org.ladder.core.sketch.ISketch;
import org.ladder.core.sketch.IStroke;
import org.ladder.core.sketch.Point;
import org.ladder.core.sketch.Shape;
import org.ladder.io.DOMInput;
import org.ladder.recognition.bullseye.BullseyeRecognizer;

import edu.tamu.recognition.IRecognitionResult;


public class BullseyeTest {
	private static JFileChooser m_chooser = new JFileChooser();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		BullseyeRecognizer br = null;
		File codebookFile = null;
		File svmFile = null;
		m_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int r = m_chooser.showOpenDialog(jf);
		if (r == JFileChooser.APPROVE_OPTION) {
			codebookFile = m_chooser.getSelectedFile();
		}
		m_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		r = m_chooser.showOpenDialog(jf);
		if (r == JFileChooser.APPROVE_OPTION) {
			svmFile = m_chooser.getSelectedFile();
		}
		if(codebookFile!=null&&svmFile!=null){
			br = new BullseyeRecognizer(codebookFile,svmFile);
		}
		if(br==null)
			System.exit(0);
		m_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		r = m_chooser.showOpenDialog(jf);
		double overallCorrect=0;
		double totalClassified=0;
		double position=0;
		ArrayList<Double> classAccuracy = new ArrayList<Double>();
		int dirNumber=0;
		if (r == JFileChooser.APPROVE_OPTION) {
			File dir = m_chooser.getSelectedFile();
			long startTime = System.currentTimeMillis();
			for(File shapeDirectory : dir.listFiles()){
				if(shapeDirectory.isDirectory()){
					double shapeCorrect=0;
					double numberShape = 0;
					dirNumber++;
					System.out.println(shapeDirectory.getName());
					for(File shapeFile : shapeDirectory.listFiles()){
						if(shapeFile.isFile()){
							DOMInput inFile = new DOMInput();
							try {
								ISketch m_sketch = inFile.parseDocument(shapeFile);
								IShape shape = new Shape();
								for(IStroke s : m_sketch.getStrokes())
									shape.addStroke(s);
								List<IShape> shapes = m_sketch.getShapes();
								shapes.add(shape);
								br.addShapeGroup(shapes);
								List<IRecognitionResult> results = br.recognize();
								IRecognitionResult result = results.get(results.size()-1);
								System.out.println(shapeFile.getName());
								result.sortNBestList();
								int i=0;
								for(IShape resShape : result.getNBestList()){
									//System.out.println(resShape.getLabel()+" "+resShape.getConfidence().toString());
									if(resShape.getLabel().equalsIgnoreCase(shapeDirectory.getName()))
											position+=i;
									i++;
								}
								String classification = result.getBestShape().getLabel();
								if(classification.equalsIgnoreCase(shapeDirectory.getName())){
									shapeCorrect++;
									overallCorrect++;
									System.out.println(shapeFile.getName()+" correct");
								}
								else{
									System.out.println(shapeFile.getName()+" classified as "+classification);
								}
								totalClassified++;
								numberShape++;
								br.removeShapeGroup(shapes);
							}
							catch (Exception e) {
								System.err.println("Error loading sketch from file: "
								                   + e.getMessage());
							}
						}
					}
					classAccuracy.add(new Double(shapeCorrect/numberShape));
					System.out.println(shapeDirectory.getName()+": "+(shapeCorrect/numberShape)+"% correct\n\n");
				}
			}
			System.out.println(System.currentTimeMillis()-startTime);
			System.out.println("\n\n"+"Overall: "+(overallCorrect/totalClassified)+"% correct");
			System.out.println("\n\nAverage Position: "+(position/totalClassified));
			System.out.println(dirNumber);
			for(Double d : classAccuracy)
				System.out.println(d.toString());
		}

		
//		Bullseye b = new Bullseye(new BullseyePoint(0,0,0,0));
//		for(int i = 0; i < 300; i++){
//			System.out.print(i+" ");
//			if(i<45)
//				b.addPoint(new BullseyePoint((i+1)*Math.cos(-Math.PI/4),(i+Math.pow(-1, i))*Math.sin(-Math.PI/4),1,1));
//			else
//				b.addPoint(new BullseyePoint(45,i-45,1,1));
//		}
//		System.out.println(b.toString());
//		System.out.println(b.getHistogram().toString());
//		JFrame jf = new JFrame();
//		jf.add(new BullseyePanel(b));
//		jf.setSize(800, 800);
//		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		jf.setVisible(true);
	}

}
