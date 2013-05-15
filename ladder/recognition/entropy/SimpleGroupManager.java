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
package org.ladder.recognition.entropy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ladder.core.logging.LadderLogger;
import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.IShape;
import org.ladder.core.sketch.IStroke;
import org.ladder.core.sketch.Shape;

public class SimpleGroupManager {
	
	public static int EXCLUSION_TIME_DIFFERENCE_THRESHOLD = 400;
	
	public static int INCLUSION_TIME_DIFFERENCE_THRESHOLD = 300;
	
	public static int MINIMUM_DISTANCE_THRESHOLD = 3;
	
	/**
	 * Logger for this class
	 */
	private static final Logger log = LadderLogger.getLogger(SimpleGroupManager.class);
	
	/**
	 * Output groups
	 */
	private List<IShape> m_groups = new ArrayList<IShape>();
	
	/**
	 * Tells us whether to recompute the groups or not.
	 */
	private boolean m_dirty = false;
	
	/**
	 * 
	 * @return List<IShape> as groups of strokes
	 */
	public List<IShape> getStrokeGroups() {
		// skip the computation if need be
		if(!m_dirty)
			return m_groups;
		
		m_dirty = false;
		
		for(int i = 0; i < m_groups.size()-1;) {
			
			// get stroke group and the one after it
			IShape currentGroup = m_groups.get(i);
			IShape nextGroup = m_groups.get(i+1);
			
			// get the last stroke of current group and the first stroke of the next group
			IStroke lastStroke = currentGroup.getLastStroke();
			IStroke firstStroke = nextGroup.getFirstStroke();
			
			// continue if stroke are too far apart
			if(firstStroke.getFirstPoint().getTime() - lastStroke.getLastPoint().getTime() > EXCLUSION_TIME_DIFFERENCE_THRESHOLD) {
				i++;
				continue;
			}
			
			List<IPoint> allCurrentPoints = new ArrayList<IPoint>();
			for(IStroke stroke : currentGroup.getStrokes())
				allCurrentPoints.addAll(stroke.getPoints());
			
			double minimum = Double.MAX_VALUE;
			// find the minimum distance between first stroke of current group and all strokes of current group
			for(IPoint point : firstStroke.getPoints())
				for(IPoint comparePt : allCurrentPoints)
					if(comparePt.distance(point) < minimum)
						minimum = comparePt.distance(point);
			
			// if really close in time or space, merge the groups
			if(firstStroke.getFirstPoint().getTime() - lastStroke.getLastPoint().getTime() < INCLUSION_TIME_DIFFERENCE_THRESHOLD
					|| minimum < MINIMUM_DISTANCE_THRESHOLD) {
				
				for(IStroke stroke : nextGroup.getStrokes())
					currentGroup.addStroke(stroke);
				
				m_groups.remove(nextGroup);
			} 
			else i++;
		}
		
		return m_groups;
	}

	/**
	 * Add each stroke as its own group.  Stroke close together will be merged.
	 * 
	 * @param strokes
	 */
	public void submitStrokes(List<IStroke> strokes) {
		if (strokes == null) {
			log.error("Cannot add a null stroke");
			throw new IllegalArgumentException("Stroke cannot be null");
		}
		
		m_groups = new ArrayList<IShape>();
		
		for(IStroke stroke : strokes) {
			IShape group = new Shape();
			group.addStroke(stroke);
			m_groups.add(group);
			m_dirty = true;
		}
	}
}
