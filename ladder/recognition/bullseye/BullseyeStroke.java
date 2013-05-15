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

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.ISegmentation;
import org.ladder.core.sketch.IStroke;

import edu.tamu.core.sketch.BoundingBox;

/**
 * A stroke used in the BullseyeRecognizer.  Like a regular stroke, but should contain only BullseyePoints
 * @author pcorey
 *
 */
public class BullseyeStroke implements IStroke {
	
	/**
	 * The points of this stroke
	 */
	private List<IPoint> m_points;

	/**
	 * Unique ID of the stroke
	 */
	private UUID m_id = UUID.randomUUID();
	
	/**
	 * The stroke bounding box
	 */
	private BoundingBox m_boundingBox;
	
	/**
	 * Create a BullseyeStroke from an IStroke
	 * @param stroke The stroke to convert to a BullseyeStroke
	 */
	public BullseyeStroke(IStroke stroke){
		m_points = BullseyeConversions.getBullseyePoints(stroke);
	}
	
	public double strokePointDistance(IPoint point){
		double distance = Double.POSITIVE_INFINITY;
		for(IPoint p : m_points)
			if(p.distance(point)<distance)
				distance=p.distance(point);
		return distance;
	}

	/**
	 * Create an IStroke, then use new BullseyeStroke(IStroke)
	 */
	@Deprecated
	@Override
	public void addPoint(IPoint point) {
		
	}

	/**
	 * BullseyeStrokes don't use segmentation
	 */
	@Deprecated
	@Override
	public void addSegmentation(ISegmentation segmentation) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Points should never be set
	 */
	@Deprecated
	@Override
	public void flagExternalUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (m_boundingBox == null && m_points.size() > 0) {
			// start low to find the max
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;
			
			// start high to find the min
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			
			for (IPoint p : m_points) {
				maxX = (p.getX() > maxX) ? p.getX() : maxX;
				maxY = (p.getY() > maxY) ? p.getY() : maxY;
				minX = (p.getX() < minX) ? p.getX() : minX;
				minY = (p.getY() < minY) ? p.getY() : minY;
			}
			
			// create the box using the min/max points
			m_boundingBox = new BoundingBox(minX, minY, maxX, maxY);
		}
		
		return m_boundingBox;
	}

	@Override
	public IPoint getFirstPoint() {
		if (m_points.size() > 0)
			return m_points.get(0);
		else
			return null;
	}

	
	@Override
	public UUID getID() {
		// TODO Auto-generated method stub
		return m_id;
	}

	@Override
	public IPoint getLastPoint() {
		if (m_points.size() > 0)
			return m_points.get(m_points.size() - 1);
		else
			return null;
	}

	@Override
	public int getNumPoints() {
		return m_points.size();
	}

	@Deprecated	
	@Override
	public IStroke getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPoint getPoint(int index) {
		return m_points.get(index);
	}

	@Override
	public List<IPoint> getPoints() {
		return m_points;
	}

	/**
	 * No segementations in Bullseye
	 */
	@Deprecated
	@Override
	public List<ISegmentation> getSegmentations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return getFirstPoint().getTime();
	}

	/**
	 * No segmentations, so they can't be removed
	 */
	@Deprecated
	@Override
	public boolean removeSegmentation(ISegmentation segmentation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setParent(IStroke parent) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * DO NOT SET THE POINTS, create a BullseyeStroke from an IStroke using the constructor
	 */
	@Deprecated
	@Override
	public void setPoints(List<IPoint> points) {

		
	}

	@Override
	public void setSegmentations(List<ISegmentation> segmentations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(IStroke arg0) {
		// Compare original strokes
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.core.sketch.IStroke#clone()
	 */
	@Override
	public Object clone() {
		return new BullseyeStroke(this);
	}

	@Override
	public void setColor(Color color) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Color getColor()
	{
		return Color.red;
	}

	@Override
    public String getLabel() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	@Override
	public double getLabelProbability()
	{
		return 0.0;
	}
	@Override
	public void setLabelProbability(double d)
	{
		
	}

	@Override
    public void setLabel(String label) {
	    // TODO Auto-generated method stub
	    
    }

	/* (non-Javadoc)
     * @see org.ladder.core.sketch.IStroke#getMinInterPointDistance()
     */
    @Override
    public double getMinInterPointDistance() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	/* (non-Javadoc)
     * @see org.ladder.core.sketch.IStroke#getPathLength()
     */
    @Override
    public double getPathLength() {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
	public void translate(double x, double y)
	{
		for (IPoint p: m_points)
		{
			p.translate(x, y);
		}
	}
	
	@Override
	public void scale(double x, double y)
	{
		for (IPoint p: m_points)
		{
			p.scale(x, y);
		}
	}

}
