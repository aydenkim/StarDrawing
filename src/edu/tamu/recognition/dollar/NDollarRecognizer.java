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
package edu.tamu.recognition.dollar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.IRecognitionResult;
import edu.tamu.recognition.recognizer.IRecognizer;
import edu.tamu.tools.PermutationGenerator;
import edu.tamu.tools.SubsetGenerator;

public class NDollarRecognizer implements
		IRecognizer<List<Stroke>, IRecognitionResult> {

	OneDollarRecognizer oneDollar = new OneDollarRecognizer();

	/**
	 * Add a template
	 * 
	 * @param s
	 *            a labeled shape
	 */
	public void addTemplate(Shape s) {
		addTemplate(s.getRecursiveStrokes(), s.getInterpretation().label);
	}

	/**
	 * Combine many strokes into one
	 * 
	 * @param strokes
	 *            the strokes to combine
	 * @param permutation
	 *            the order to combine in
	 * @param flipped
	 *            which strokes to flip
	 * @return
	 */
	public static Stroke makeStroke(List<Stroke> strokes,
			List<Integer> permutation, List<Integer> flipped) {
		Stroke s = new Stroke();
		for (int i : permutation) {
			List<Point> points = new ArrayList<Point>(strokes.get(i)
					.getPoints());
			if (flipped.contains(i))
				Collections.reverse(points);
			s.getPoints().addAll(points);
		}
		s.flagExternalUpdate();
		return s;
	}

	/**
	 * Merge a bunch of strokes together
	 * 
	 * @param strokes
	 * @return single merged stroke
	 */
	public static Stroke naiveMerge(List<Stroke> strokes) {
		Stroke s = new Stroke();
		for (Stroke ss : strokes)
			s.getPoints().addAll(ss.getPoints());
		return s;
	}

	/**
	 * Add a template
	 * 
	 * @param strokes
	 *            some strokes
	 * @param label
	 *            the label for the template
	 */
	public void addTemplate(List<Stroke> strokes, String label) {
		if (strokes.size() == 0)
			return;
		if (strokes.size() == 1) {
			oneDollar.addTemplate(strokes.get(0), label);
			Stroke rev = new Stroke(strokes.get(0));
			Collections.reverse(rev.getPoints());
			rev.flagExternalUpdate();
			oneDollar.addTemplate(rev, label);
			return;
		}

		for (List<Integer> perm : new PermutationGenerator(strokes.size())) {
			for (List<Integer> flip : new SubsetGenerator(strokes.size())) {
				oneDollar.addTemplate(makeStroke(strokes, perm, flip), label);
			}
		}

	}

	@Override
	public IRecognitionResult recognize() {
		return oneDollar.recognize();
	}

	@Override
	public void submitForRecognition(List<Stroke> submission) {
		oneDollar.submitForRecognition(naiveMerge(submission));
	}

}
