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

import java.util.Iterator;
import java.util.UUID;

import org.ladder.core.sketch.IPoint;
import org.ladder.core.sketch.Point;
/**
 * Bullseye points for use in the BullseyeRecognizer.  Since direction is determined from the stroke, it is best to obtain
 * these points using one of the BullseyeConversions rather than creating the points.
 * @author pcorey
 *
 */
public class BullseyePoint extends Point implements IPoint{
	
	/**
	 * Bullseye points also care about stroke direction at the point
	 */
	private double m_direction;
	
	/**
	 * Create a "default" point
	 */
	public BullseyePoint(){
		this(0,0,0,0);
	}
	
	/**
	 * Create a BullseyePoint at (x,y) with time t and the stroke direction at that point
	 * @param x X coordinate of the point
	 * @param y Y coordinate of the point
	 * @param t Time of the point
	 * @param direction The direction of the stroke at the point
	 */
	public BullseyePoint(double x, double y, long t, double direction){
		super(x,y,t);
		m_direction = direction;
	}
	
	/**
	 * Create a BullseyePoint from point with the stroke direction at point
	 * @param point The point to create the BullseyePoint from
	 * @param direction Stroke direction at point
	 */
	public BullseyePoint(IPoint point, double direction) {
		
		// Copy the coordinates
		this(point.getX(), point.getY(), point.getTime(), direction);
		
		// Copy the ID
		if (point.getID() != null) {
			setID(UUID.fromString(point.getID().toString()));
		}
	}

	/**
	 * Copy constructor
	 * @param point The BullseyePoint to copy
	 */
	public BullseyePoint(BullseyePoint point) {
		
		// Copy the coordinates
		this(point.getX(), point.getY(), point.getTime(), point.getDirection());
		
		// Copy the ID
		if (point.getID() != null) {
			setID(UUID.fromString(point.getID().toString()));
		}
	}

	/**
	 * Create a BullseyePoint from a Point
	 * @param point The point to use
	 * @param direction The stroke direction at point
	 */
	public BullseyePoint(Point point, double direction) {
		
		// Copy the necessary IPoint components. We have to make this cast to
		// avoid calling this constructor and ending up in infinite recursion.
		this((IPoint) point, direction);
		
		// Copy the additional attributes not present in the IPoint interface
		if (point.getName() != null) {
			setName(new String(point.getName()));
		}
		if (point.getPressure() != null) {
			setPressure(new Double(point.getPressure().doubleValue()));
		}
		if (point.getTiltX() != null) {
			setTiltX(new Double(point.getTiltX().doubleValue()));
		}
		if (point.getTiltY() != null) {
			setTiltY(new Double(point.getTiltY().doubleValue()));
		}
		if (point.getAttributes() != null) {
			for (Iterator<String> i = point.getAttributes().keySet().iterator(); i
			        .hasNext();) {
				String k = new String(i.next());
				setAttribute(k, new String(point.getAttribute(k)));
			}
		}
	}
	
	/**
	 * Set the direction of the stroke at the BullseyePoint
	 * @param direction The stroke direction
	 */
	public void setDirection(double direction){
		m_direction = direction;
	}
	
	/**
	 * Get the direction of the stroke at this BullseyePoint
	 * @return
	 */
	public double getDirection(){
		return m_direction;
	}

	/**
	 * Returns whether two points are equal by comparing their positions, time, and direction.
	 * 
	 * @param obj
	 *            The object to compare to
	 * @return True if the two points have the same position, time, and direction, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		
		if (obj instanceof BullseyePoint) {
			if (this == obj) {
				ret = true;
			}
			else {
				BullseyePoint p = (BullseyePoint) obj;
				ret = p.getX()==this.getX()&&p.getY()==this.getY()&&p.getDirection()==this.getDirection()&&p.getTime()==this.getTime();
			}
		}
		
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		return new BullseyePoint(this);
	}
}
