package recognition;

/**
 * IntersectionSegmenter.java
 *
 * 
 * <p>
 * 
 * <pre>
 * This work is released under the BSD License:
 * (C) 2008 Sketch Recognition Lab, Texas A&amp;M University (hereafter SRL @ TAMU)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&amp;M University
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Segmentation;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.segmentation.AbstractSegmenter;

/**
 * Segments strokes based on where they intersect each other. Assumes all input
 * strokes represent line segments that have already been identified by another
 * segmenter.
 */
public class IntersectionSegmenter extends AbstractSegmenter {

	private static double ANSWER_THRESHOLD = 0.1853972798854688;
	/**
	 * Default constructor.
	 */
	public IntersectionSegmenter() { }

	
	/**
	 * Split a stroke into 2 pieces
	 * 
	 * @param split
	 * @param with
	 * @return
	 */
	public Shape[] split(Stroke split, Stroke with) {
		int corner = split.addPointAtIntersection(with);
		// corner=0 means split at the first point
		if (corner == -1 || corner == 0 || corner == split.getNumPoints() - 1)
			return null;
		List<Integer> corners = Arrays.asList(new Integer[] { 0, corner,
				split.getNumPoints() - 1 });
		System.out.println("splitting: " + corners);
		Segmentation s = segmentStroke(split, corners, "", 1.0).get(0);

		Shape first = new Shape();
		first.add(s.getSegmentedStrokes().get(0));
		first.setLabel("line");
		Shape second = new Shape();
		second.add(s.getSegmentedStrokes().get(1));
		second.setLabel("line");

		return new Shape[] { first, second };
	}
	
	/**
	 * Calculate intersection point along both line segments.
	 * 
	 * ua is 0 if the intersection is at alast and 1 if it is at ahere ua is
	 * 0.333 if it is 1/3 the way from alast to ahere.
	 * 
	 * ub acts a lot like ua, except with blast and bhere.
	 * 
	 * @param one
	 *            end point for first line
	 * @param two
	 *            endpoint for first line
	 * @param a
	 *            endpoint for second line
	 * @param b
	 *            endpoint for second line
	 * @return [ua,ub]
	 */
	public static double[] segmentIntersection(Point one, Point two, Point a,
			Point b) {

		double x1 = one.getX();
		double x2 = two.getX();
		double x3 = a.getX();
		double x4 = b.getX();
		double y1 = one.getY();
		double y2 = two.getY();
		double y3 = a.getY();
		double y4 = b.getY();
		
		double denom = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        
		
		if (Math.abs(denom) < 1e-5)
			return new double[] { Double.MAX_VALUE, Double.MAX_VALUE };
		// parallel (ish) lines

		double ua = ((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3));
		double ub = ((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3));

		return new double[] { ua / denom, ub / denom };
	}



}
