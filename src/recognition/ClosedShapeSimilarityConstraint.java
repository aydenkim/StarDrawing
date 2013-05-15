package recognition;

/**
 * BelowConstraint.java
 * 
 * Revision History:<br>
 * Aug 8, 2008 jbjohns - File created
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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.ladder.core.logging.LadderLogger;

import edu.tamu.core.sketch.BoundingBox;
import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.constraint.IConstrainable;
import edu.tamu.recognition.constraint.confidence.AbstractConfidenceConstraint;
import edu.tamu.recognition.dollar.OneDollarStroke;

/**
 * Constraint to determine if two closedshapes are similar.
 * {@link IConstrainable}.
 * 
 * @author svalentine
 */
public class ClosedShapeSimilarityConstraint extends
AbstractConfidenceConstraint {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.recognition.constraint.IConstraint#newInstance()
	 */
	@Override
	public ClosedShapeSimilarityConstraint newInstance() {
		return new ClosedShapeSimilarityConstraint();
	}

	private static final int NUM_POINTS = 64;// number of points in resampled
	// shape
	private static final int MAX_BOUNDING_BOX = 64; // the scale of the
	// comparative shapes
	private static final double OVERLAPPING_THRESHOLD = 4; // the threshold of
	// two overlapping
	// points

	/**
	 * threshold for the maximum shortest distance between two points (one from
	 * each shape)
	 */
	private static double maxPointDistanceThreshold = .25;

	/**
	 * threshold for the average shortest distance between every point in each
	 * shape and its closest neighbor in the other shape.
	 */
	private static double averagePointDistanceThreshold = .25;

	/**
	 * threshold for the ratio of overlapping points to the number of total
	 * points (actually, when the tanimotoCoefficient confidence is solved, we
	 * use 1 - the tanimoto coefficient. So, the threshold is 1 - the max value
	 * of the ratio.
	 */
	private static double tanimotoCoefficientThreshold = .25;

	static char field[][];

	/**
	 * logger
	 */
	private Logger log = LadderLogger
	.getLogger(ClosedShapeSimilarityConstraint.class);

	/**
	 * The name for this constraint
	 */
	public static final String NAME = "ClosedShapeSimilarity";

	/**
	 * The description for this constraint
	 */
	public static final String DESCRIPTION = "Tests if two shapes are perceptually similar.";

	/**
	 * Number of parameters this constraint uses.
	 */
	public static final int NUM_PARAMETERS = 2;

	/**
	 * The default threshold for this constraint, used only in the case of
	 * points. If not points, the threshold is dynamic and based on shape
	 * heights
	 */
	public static final double DEFAULT_THRESHOLD = 15;

	/**
	 * Create a below constraint with the default threshold.
	 */
	public ClosedShapeSimilarityConstraint() {
		this(DEFAULT_THRESHOLD);
	}

	/**
	 * Construct the constraint with the given threshold
	 * 
	 * @param threshold
	 *            The threshold to use when solving the confidence for this
	 *            constraint
	 */
	public ClosedShapeSimilarityConstraint(double threshold) {
		super(NAME, DESCRIPTION, NUM_PARAMETERS, threshold);
		this.setScaleParameters(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ladder.recognition.constraint.IConstraint#solve()
	 */
	@Override
	public double solve() {
		if (getParameters() == null
				|| getParameters().size() < getNumRequiredParameters()) {
			log.debug("Below requires " + getNumRequiredParameters()
					+ " parameters");
			return 0;
		}

		IConstrainable shape1 = getParameters().get(0);
		IConstrainable shape2 = getParameters().get(1);

		return solve(shape1, shape2);
	}


	/**
	 * get Euclidean distance between two points
	 * @return
	 */
	public double EuclideanDistance(Point a, Point b){
		return (a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y);
	}
	/**
	 * Shorthand method (no list of shapes) for seeing if the first shape is
	 * below the second shape
	 * 
	 * @param shape1
	 *            The first shape
	 * @param shape2
	 *            The second shape
	 * @return The confidence that the first shape is below the second shape
	 */
	public double elasticSolve(List<Point> p1, List<Point> p2) {

		return 0.0;
	}

    /**
     * Hausdoff distance
     * @param shape1
     * @param shape2
     * @return
     */
	public double solveHD(List<Point> p1, List<Point> p2){

		double minimum_distance = 0.0;
		double maximum_distance1 = 0.0;
		for(Point point1 : p1){
			minimum_distance =  Double.MAX_VALUE;
			for(Point point2 : p2){
				minimum_distance = Math.min(point1.distance(point2), minimum_distance);
			}
			maximum_distance1 = Math.max(maximum_distance1, minimum_distance);
		}

		double maximum_distance2 = 0.0;
		for(Point point2 : p2){
			minimum_distance =  Double.MAX_VALUE;
			for(Point point1 : p1){
				minimum_distance = Math.min(point2.distance(point1), minimum_distance);
			}
			maximum_distance2 = Math.max(maximum_distance2, minimum_distance);
		}

		double result = Math.max(maximum_distance1, maximum_distance2);
		result = (1 - (result/20.0));

		//System.out.println("HD = " + result);

		return result;
	}

	/**
	 * Modified Hausdoff distance
	 * @param shape1
	 * @param shape2
	 * @return
	 */
	public double solveMHD(List<Point> p1, List<Point> p2){

		double minimum_distance = 0.0;
		double sum_distance1 = 0.0;
		for(Point point1 : p1){
			minimum_distance =  Double.MAX_VALUE;
			for(Point point2 : p2){
				minimum_distance = Math.min(point1.distance(point2), minimum_distance);
			}
			sum_distance1 = sum_distance1 + minimum_distance;
		}

		double sum_distance2 = 0.0;
		for(Point point2 : p2){
			minimum_distance =  Double.MAX_VALUE;
			for(Point point1 : p1){
				minimum_distance = Math.min(point2.distance(point1), minimum_distance);
			}
			sum_distance2 = sum_distance2 + minimum_distance;
		}

		double result = Math.max(sum_distance1/p1.size(), sum_distance2/p2.size());
		result = (1 - (result/20.0));

		//System.out.println("MHD = " + result);

		return result;
	}

	/**
	 * Shorthand method (no list of shapes) for seeing if the first shape is
	 * below the second shape
	 * 
	 * @param shape1
	 *            The first shape
	 * @param shape2
	 *            The second shape
	 * @return The confidence that the first shape is below the second shape
	 */
	public double solve(List<Point> p1, List<Point> p2) {

		double shortestDistance1[] = new double[p1.size() + 1];
		double shortestDistance2[] = new double[p2.size() + 1];
		double avgShort1 = 0;
		double avgShort2 = 0;
		double maxFoundDistance = 0;
		int numOverlapping = 0;
		for (int i = 0; i < shortestDistance1.length; i++) {
			shortestDistance1[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < shortestDistance2.length; i++) {
			shortestDistance2[i] = Double.MAX_VALUE;
		}

		for (int i = 0; i < p1.size(); i++) {
			for (int j = 0; j < p2.size(); j++) {
				if (p1.get(i).distance(p2.get(j)) < shortestDistance1[i]) {
					shortestDistance1[i] = p1.get(i).distance(p2.get(j));
				}
			}
			avgShort1 += shortestDistance1[i];
			// System.out.println(shortestDistance1[i]);
			if (shortestDistance1[i] > maxFoundDistance) {
				maxFoundDistance = shortestDistance1[i];
			}
			if (shortestDistance1[i] < OVERLAPPING_THRESHOLD) {
				numOverlapping++;
			}
		}

		for (int i = 0; i < p2.size(); i++) {
			for (int j = 0; j < p1.size(); j++) {
				if (p2.get(i).distance(p1.get(j)) < shortestDistance2[i]) {
					shortestDistance2[i] = p2.get(i).distance(p1.get(j));
				}
			}
			avgShort2 += shortestDistance2[i];
			// System.out.println(shortestDistance1[i]);
			if (shortestDistance2[i] > maxFoundDistance) {
				maxFoundDistance = shortestDistance2[i];
			}
			if (shortestDistance2[i] < OVERLAPPING_THRESHOLD) {
				numOverlapping++;
			}
		}

		avgShort1 = avgShort1 / shortestDistance1.length;
		avgShort2 = avgShort2 / shortestDistance2.length;

		double tanimotoConfidence = p1.size() + p2.size();
		if (numOverlapping != tanimotoConfidence) {
			tanimotoConfidence = numOverlapping / (tanimotoConfidence);
		} else {
			tanimotoConfidence = 1.0;
		}

		double avgDistConfidence = (avgShort1 + avgShort2) / 2;
		avgDistConfidence = 1 - avgDistConfidence / 20;

		double maxDistConfidence = 1 - maxFoundDistance / 20;

		double totalConfidence = avgDistConfidence + maxDistConfidence
		+ tanimotoConfidence;
		totalConfidence = totalConfidence / 3;

		return totalConfidence;

	}

	/**
	 * Shorthand method (no list of shapes) for seeing if the first shape is
	 * below the second shape
	 * 
	 * @param shape1
	 *            The first shape
	 * @param shape2
	 *            The second shape
	 * @return The confidence that the first shape is below the second shape
	 */
	public double solve(IConstrainable shape1, IConstrainable shape2) {
		Shape s1 = shape1.getParentShape();
		Shape s2 = shape2.getParentShape();

		List<Point> points = s2.getPoints();
		BoundingBox b1 = s1.getBoundingBox();
		BoundingBox b2 = s2.getBoundingBox();

		translateToImage(s1);
		List<Point> p1 = tailorPoints(s1);
		List<Point> p2 = tailorPoints(s2);

		return solve(p1, p2);

	}



	/**
	 * use image-based recognition
	 */
	public List<Point> translateToImage(Shape shape){
		List<Point> points = new ArrayList<Point>();

		BoundingBox box = shape.getBoundingBox();

		if(box.getHeight() > box.getWidth()){

		}

		return points;

	}

	/**
	 * Shorthand method (no list of shapes) for seeing if the first shape is
	 * below the second shape
	 * 
	 * @param shape1
	 *            The first shape
	 * @param shape2
	 *            The second shape
	 * @return The confidence that the first shape is below the second shape
	 */
	public double elasticSolve(IConstrainable shape1, IConstrainable shape2) {
		Shape s1 = shape1.getParentShape();
		Shape s2 = shape2.getParentShape();

		List<Point> p1 = tailorPoints(s1);
		List<Point> p2 = tailorPoints(s2);

		return elasticSolve(p1, p2);

	}
	
	public static List<Point> beautifyShape(List<Point> AllPoints){
		List<Point> points = new ArrayList<Point>();
		points.add(AllPoints.get(0));
		points.add(AllPoints.get(1));
		points.add(AllPoints.get(2));
		
		Double beautify_x = 0.0;
		Double beautify_y = 0.0;
		for(int i = 3; i < AllPoints.size()-3; i++){
			beautify_x = 0.0;
			beautify_y = 0.0;
			for(int j = (i-3); j < (i+3); j++){
				beautify_x = beautify_x + AllPoints.get(j).x;
				beautify_y = beautify_y + AllPoints.get(j).y;
			}
			Point newPoint = new Point(beautify_x / 6, beautify_y/6);
			points.add(newPoint);
		}
		
		return points;
	}

	public static List<Point> tailorPoints(Shape shape) {

		ArrayList<Shape> pieces = new ArrayList<Shape>(shape.getShapes());
		List<Point> AllPoints = new ArrayList<Point>();
        List<Point> points = new ArrayList<Point>();
		Stroke stroke = new Stroke();

		for(Shape piece : pieces){
			AllPoints.addAll(piece.getPoints());
			/*for(Point point : piece.getPoints()){
				stroke.addPoint(point);
			}*/
		}
		
		points = beautifyShape(AllPoints);
		for(Point point : points){
			stroke.addPoint(point);
		}
		

		ArrayList<Shape> inOrder = new ArrayList<Shape>();
		ArrayList<Point> allPoints = new ArrayList<Point>();


		for (int i = stroke.getPoints().size() - 1; i >= 0; i--) {
			allPoints.add(stroke.getPoint(i));
			allPoints.get(allPoints.size() - 1).setTime(
					System.currentTimeMillis());
		}

		//System.out.println("point size " + shape.getPoints().size());

		Stroke s1 = new Stroke(shape.getPoints());
		s1 = s1.clone();

		BoundingBox box = s1.getBoundingBox();

		final double boxX = box.width;
		final double boxY = box.height;
		// System.out.println("boxX = "+boxX);
		// System.out.println("boxY = "+boxY);

		double ratio, xAddToCenter, yAddToCenter;

		// compute modified ratios
		if (boxX > boxY) {
			ratio = MAX_BOUNDING_BOX / boxX;
			xAddToCenter = 0;
			yAddToCenter = (MAX_BOUNDING_BOX - (boxY * ratio)) / 2;
		} else {
			ratio = MAX_BOUNDING_BOX / boxY;
			yAddToCenter = 0;
			xAddToCenter = (MAX_BOUNDING_BOX - (boxX * ratio)) / 2;
		}

		// resample points to have only NUM_POINTS
		double resampleWidth = s1.getPathLength() / NUM_POINTS;
		List<Point> oldPoints = OneDollarStroke.resamplePoints(s1.getPoints(),
				resampleWidth);
		// System.out.println("Num resampled points: "+oldPoints.size());

		// find left and topmost point
		double leftmostX = Double.MAX_VALUE;
		double topmostY = Double.MAX_VALUE;

		for (int i = 0; i < oldPoints.size(); i++) {
			if (oldPoints.get(i).x < leftmostX) {
				leftmostX = oldPoints.get(i).x;
			}
			if (oldPoints.get(i).y < topmostY) {
				topmostY = oldPoints.get(i).y;
			}
		}

		// translate points into new ratio and populate char array
		for (int i = 0; i < oldPoints.size(); i++) {
			Point p = oldPoints.get(i);
			p.x = (p.x - leftmostX) * ratio + xAddToCenter;
			p.y = (p.y - topmostY) * ratio + yAddToCenter;

		}

		return oldPoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.ladder.recognition.constraint.confidence.AbstractConstraint#
	 * isClearlyFalse(double)
	 */
	@Override
	public boolean isClearlyFalse(double value) {
		return value < 0.3;
	}

	public static Point getPointTailoredToSmallGrid(Point oldPoint, Shape shape) {
		ArrayList<Shape> pieces = new ArrayList<Shape>(shape.getShapes());
		Point current = pieces.get(0).getFirstStroke().getFirstPoint();
		Point last = pieces.get(0).getFirstStroke().getLastPoint();


		ArrayList<Shape> inOrder = new ArrayList<Shape>();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		for (int i = pieces.get(0).getFirstStroke().getPoints().size() - 1; i >= 0; i--) {
			allPoints.add(pieces.get(0).getFirstStroke().getPoint(i));
			allPoints.get(allPoints.size() - 1).setTime(
					System.currentTimeMillis());
		}

		Stroke s1 = new Stroke(allPoints);
		s1 = s1.clone();

		BoundingBox box = s1.getBoundingBox();

		final double boxX = box.width;
		final double boxY = box.height;
		// System.out.println("boxX = "+boxX);
		// System.out.println("boxY = "+boxY);

		double ratio, xAddToCenter, yAddToCenter;

		// compute modified ratios
		if (boxX > boxY) {
			ratio = MAX_BOUNDING_BOX / boxX;
			xAddToCenter = 0;
			yAddToCenter = (MAX_BOUNDING_BOX - (boxY * ratio)) / 2;
		} else {
			ratio = MAX_BOUNDING_BOX / boxY;
			yAddToCenter = 0;
			xAddToCenter = (MAX_BOUNDING_BOX - (boxX * ratio)) / 2;
		}

		// resample points to have only NUM_POINTS
		double resampleWidth = s1.getPathLength() / NUM_POINTS;
		List<Point> oldPoints = OneDollarStroke.resamplePoints(s1.getPoints(),
				resampleWidth);
		// System.out.println("Num resampled points: "+oldPoints.size());

		// find left and topmost point
		double leftmostX = Double.MAX_VALUE;
		double topmostY = Double.MAX_VALUE;

		for (int i = 0; i < oldPoints.size(); i++) {
			if (oldPoints.get(i).x < leftmostX) {
				leftmostX = oldPoints.get(i).x;
			}
			if (oldPoints.get(i).y < topmostY) {
				topmostY = oldPoints.get(i).y;
			}
		}

		// translate points into new ratio and populate char array
		Point p = new Point(oldPoint);
		p.x = (p.x - leftmostX) * ratio + xAddToCenter;
		p.y = (p.y - topmostY) * ratio + yAddToCenter;

		return p;

	}

	public static Point getPointFromSmallToFullGrid(Point smallPoint,
			Shape shape, boolean inside) {

		ArrayList<Shape> pieces = new ArrayList<Shape>(shape.getShapes());
		Point current = pieces.get(0).getFirstStroke().getFirstPoint();
		Point last = pieces.get(0).getFirstStroke().getLastPoint();


		ArrayList<Shape> inOrder = new ArrayList<Shape>();
		ArrayList<Point> allPoints = new ArrayList<Point>();
		for (int i = pieces.get(0).getFirstStroke().getPoints().size() - 1; i >= 0; i--) {
			allPoints.add(pieces.get(0).getFirstStroke().getPoint(i));
			allPoints.get(allPoints.size() - 1).setTime(
					System.currentTimeMillis());
		}

		Stroke s1 = new Stroke(allPoints);
		s1 = s1.clone();

		BoundingBox box = s1.getBoundingBox();

		final double boxX = box.width;
		final double boxY = box.height;

		double ratio, inverseratio, xAddToCenter, yAddToCenter;

		// compute modified ratios
		if (boxX > boxY) {
			ratio = MAX_BOUNDING_BOX / boxX;
			inverseratio = boxX / MAX_BOUNDING_BOX;
			xAddToCenter = 0;
			yAddToCenter = (MAX_BOUNDING_BOX - (boxY * ratio)) / 2;
		} else {
			ratio = MAX_BOUNDING_BOX / boxY;
			inverseratio = boxY / MAX_BOUNDING_BOX;
			yAddToCenter = 0;
			xAddToCenter = (MAX_BOUNDING_BOX - (boxX * ratio)) / 2;
		}

		// put it inside a little
		// ratio /= 1.15;

		// resample points to have only NUM_POINTS
		double resampleWidth = s1.getPathLength() / NUM_POINTS;
		List<Point> oldPoints = OneDollarStroke.resamplePoints(s1.getPoints(),
				resampleWidth);

		// find left and topmost point
		double leftmostX = Double.MAX_VALUE;
		double topmostY = Double.MAX_VALUE;

		for (int i = 0; i < oldPoints.size(); i++) {
			if (oldPoints.get(i).x < leftmostX) {
				leftmostX = oldPoints.get(i).x;
			}
			if (oldPoints.get(i).y < topmostY) {
				topmostY = oldPoints.get(i).y;
			}
		}

		// make them small
		if (inside) {
			for (Point p : oldPoints) {
				p.x = (p.x - leftmostX) * ratio + xAddToCenter;
				p.y = (p.y - topmostY) * ratio + yAddToCenter;
			}

			// find points inside the polygon
			List<Point> points = new ArrayList<Point>();
			for (int row = 1; row <= 39; ++row) {
				List<Double> crossings = crossings(oldPoints, row);
				for (int crossing = 0; crossing < crossings.size()-1; crossing += 2) {//Laura: Changed this to crossings.size()-1 because of index out of bounds for odd numbers
					int cstart = (int) Math.ceil(crossings.get(crossing));
					int cend = (int) Math.floor(crossings.get(crossing + 1));
					for (int col = cstart; col <= cend; ++col)
						points.add(new Point(col, row));
				}
			}

			// now find the closest one
			if (points.size() > 0) {
				Point bestP = points.get(0);
				double bestD = smallPoint.distance(bestP);

				for (int i = 1; i < points.size(); ++i) {
					double test = smallPoint.distance(points.get(i));
					if (test < bestD) {
						bestD = test;
						bestP = points.get(i);
					}
				}

				// the winner!
				smallPoint = bestP;
			}
		}

		Point p = new Point(smallPoint);
		// System.out.println(inverseratio);
		p.x = (Math.abs(p.x - xAddToCenter) * inverseratio) + leftmostX;
		p.y = (Math.abs(p.y - yAddToCenter) * inverseratio) + topmostY;

		return p;

		// return new Point (300,300);

	}

	/**
	 * Find the crossings of the line y = Y with the polygon described by the
	 * given list.
	 * 
	 * @param polygon
	 * @param startButton
	 * @return
	 */
	public static List<Double> crossings(List<Point> polygon, double Y) {
		List<Double> res = new ArrayList<Double>();

		Point left = new Point(-10000.0, Y);
		Point right = new Point(10000.0, Y);

		Point last = null;
		Point here = polygon.get(polygon.size() - 1);

		for (int i = 0; i < polygon.size(); ++i) {
			last = here;
			here = polygon.get(i);
			double[] inter = IntersectionSegmenter.segmentIntersection(left,
					right, last, here);
			if (inter[0] >= 0.0 && inter[0] <= 1.0 && inter[1] >= 0.0
					&& inter[1] <= 1.0) {
				double d = -10000.0 + 20000.0 * inter[0];
				if (!res.contains(d))
					res.add(d);
			}
		}

		Collections.sort(res);
		return res;
	}

	/**
	 * Check if a point is inside a polygon
	 * 
	 * @param polygon
	 * @param p
	 * @return
	 */
	public static boolean inside(List<Point> polygon, Point p) {
		List<Double> crossings = crossings(polygon, p.y);
		if (crossings.size() == 0)
			return false;

		int i;
		for (i = 0; i < crossings.size() && crossings.get(i) < p.x; ++i)
			;

		if (i == 0)
			return false;
		return (i % 2 == 1);

	}

}
