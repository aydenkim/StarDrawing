package recognition;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;
import edu.tamu.recognition.dollar.OneDollarStroke;

public class DirectionManager {

	private final static double RATIO_THRESHOLD = 0.75;
	private static final int NUM_POINTS = 40;
	private static final int ANGLE_DISTANCE = 20;
	private static final int DISTANCE_THRESHOLD = 5;

	/**
	 * Using list of angles, return direction
	 * @param angles
	 * @return
	 */
	public Direction findDirection(List<Integer> angles){

		Direction direction = null;
		List<String> newList = new ArrayList<String>(angles.size()) ;
		for (Integer myInt : angles) { 
			if(!newList.contains(String.valueOf(myInt))){
				newList.add(String.valueOf(myInt)); 
			}
		}

		if(angles.size() < 2){ // we don't need to consider small sized angles
			return direction;
		}



		/*int compare1 = Math.abs((Integer)angles.get(0));
		int compare2 = Math.abs((Integer)angles.get(angles.size()/2));
		int compare3 = Math.abs((Integer)angles.get(angles.size()-1));


		if(compare1 == 7 && compare2 == 0){
			compare1 = -1;
		}else if(compare1 == 0 && compare2 == 7){
			compare2 = -1;
		}
		 */
		if(newList.size() <= 2){
			direction = Direction.line;
			return direction;
		}else if(newList.size() == 3){
			List<String> lineList = new ArrayList<String>(3);

			double cnt_1 = 0;
			double cnt_2 = 0;
			double cnt_3 = 0;

			for (Integer myInt : angles) { 
				if(myInt == Integer.parseInt(newList.get(0))){
					cnt_1++;
				}
				else if(myInt == Integer.parseInt(newList.get(1))){
					cnt_2++;
				}
				else if(myInt == Integer.parseInt(newList.get(2))){
					cnt_3++;
				}
			}

			double max_distribution = Math.max(cnt_1, cnt_2);
			max_distribution = Math.max(max_distribution, cnt_3);

			double min_distribution = Math.min(cnt_1, cnt_2);
			min_distribution = Math.min(min_distribution, cnt_3);

			double middle_distribution = 0.0;
			if(cnt_1 < max_distribution && cnt_1 > min_distribution){
				middle_distribution = cnt_1;
			}else if(cnt_2 < max_distribution && cnt_2 > min_distribution){
				middle_distribution = cnt_2;
			}else if(cnt_3 < max_distribution && cnt_3 > min_distribution){
				middle_distribution = cnt_3;
			}



			System.out.println("how much ? = " + max_distribution / angles.size());
			System.out.println(max_distribution + middle_distribution);
			if(max_distribution / angles.size() >= 0.7){
				direction = Direction.line;
				return direction;

			}else if( (max_distribution + middle_distribution) >= 0.7){
				if(  Math.abs(angles.get(0) - angles.get(angles.size()-1)) <= 3) {
					direction = Direction.line;
					return direction;
				}
			}

		}


		int up_count = 0;
		int down_count = 0;

		for(int i = 1; i < angles.size(); i++){
			int compare1 = (Integer)angles.get(i);
			int compare2 = (Integer)angles.get(i-1);

			if(compare1 == 7 && compare2 == 0){
				compare1 = -1;
			}else if(compare1 == 0 && compare2 == 7){
				compare2 = -1;
			}

			if(compare1 < compare2){
				down_count++;
			}else if(compare1 > compare2){
				up_count++;
			}
		}
		if(down_count > up_count){
			direction = Direction.down;
		}else{
			direction = Direction.up; 
		}


		/*if( (Math.abs((compare1 - compare2)) <= 1) && (Math.abs((compare2 - compare3)) <= 1)){
			direction = Direction.line;
		}*/
		/*else if((Integer)unique_angles.get(2) > (Integer)unique_angles.get(1)){
			direction = Direction.up; 
		}else if((Integer)unique_angles.get(2) < (Integer)unique_angles.get(1)){
			direction = Direction.down;
		}*/
		/*else if( (compare1 > compare2) || (compare2 > compare3)){
			direction = Direction.Down;
		}else if((compare1 < compare2) || (compare2 < compare3)){
			direction = Direction.Up; 

		}*/


		return direction;
	}

	public int direction(int x, int y) {
		//0 NE, 1 SE, 2 E, 3 NW, 4 SW, 5 W, 6 N, 7 S, 8 (Same place / Not a direction)  
		int direction = 0;

		if(x < 0){
			direction = 3;
		}else if(x == 0){
			direction = 6;
		}

		if(y < 0){
			direction = direction + 1;
		}else if(y == 0){
			direction = direction + 2;
		}
		return direction;
	}

	/**
	 * Return freeman's chain code string
	 * Loop
	 * Up: clock wise
	 * Down: counter clock wise
	 * Line: same direction
	 * @param angles
	 * @return
	 */
	public  List<StrokeComponent> FreemanStringDirection(List angles, List<Point> points ){


		List<Direction> directions = new ArrayList<Direction>();
		List<StrokeComponent> components = new ArrayList<StrokeComponent>();
		List<Integer> temp_point = new ArrayList<Integer>();
		List<Integer> point_index = new ArrayList<Integer>();
		List<Integer> point_angles = new ArrayList<Integer>();
		List<Integer> sharp_point = new ArrayList<Integer>();
		Direction direction = null;

		// find if it is a line
		if(findDirection(angles).toString().equals("line")){
			System.out.println("it will be a line");
			point_index.add(1);
			point_index.add(angles.size()+1);
			StrokeComponent component = new StrokeComponent();
			direction = Direction.line;
			component.addDirections(direction);
			component.addPoints(point_index);
			component.addAngles(point_angles);
			components.add(component);
			return components;

		}


		// some cases,the direction was wrong
		for(int i = 2; i < angles.size()-2; i++){
			if(!angles.get(i).equals(angles.get(i-1))){
				if(!angles.get(i).equals(angles.get(i+1))){
					if(angles.get(i-1).equals(angles.get(i+1))){
						angles.set(i, angles.get(i-1));
					}
				}
			}
		}

		direction = null;
		int i = 0;

		for(i = 1; i < angles.size(); i++){ // ignore first point
			temp_point.add((Integer)angles.get(i));
			if(point_index.isEmpty()){
				point_index.add(i);
			}

			point_angles.add((Integer)angles.get(i));

			int compare1 = Math.abs((Integer)angles.get(i));
			int compare2 = Math.abs((Integer)angles.get(i-1));

			if(compare1 == 7 && compare2 == 0){
				compare1 = -1;
			}else if(compare1 == 0 && compare2 == 7){
				compare2 = -1;
			}


			Double comp1 = 0.0;
			Double comp2 = 0.0;
			if(i >= 2){
				comp1 = this.getEntropyValue(points.get(i-2), points.get(i), points.get(i-1));
				comp2 = this.getEntropyValue(points.get(i-1), points.get(i+1), points.get(i));
				if(comp1 > 160 && comp1 < 200){
					if(comp2 < 30){
						comp2 = 180 + comp2;
					}
				}
				//System.out.println("difference = " + Math.abs(comp1 - comp2));
			}

			if(Math.abs(comp1 - comp2) >= ANGLE_DISTANCE){
				System.out.println("i = " + i);
				if(sharp_point.isEmpty()){
					sharp_point.add(i);
					point_index.add(i);
					if(!temp_point.isEmpty() && (Math.abs(point_index.get(0) - point_index.get(1))>= 4)){  // if it has enough point
						direction = findDirection(temp_point);
					}

					if(direction != null){
						directions.add(direction);
						StrokeComponent component = new StrokeComponent();
						component.addDirections(direction);
						component.addPoints(point_index);
						component.addAngles(point_angles);
						components.add(component);


					}

					point_index.clear();
					point_angles.clear();
					temp_point.clear();
				}


				else if(  ((sharp_point.get(sharp_point.size()-1) - i) >= DISTANCE_THRESHOLD)  ||   (sharp_point.size() == 1 )  ){ // check if it is too close

					System.out.println("direction difference = " + Math.abs(compare1 - compare2));
					//if(Math.abs(compare1 - compare2) >= 2){

					if(!temp_point.isEmpty()){ 
						direction = findDirection(temp_point);
					}

					else if((Math.abs(point_index.get(0) - point_index.get(1))>= 4)){
						if(direction != null){
							sharp_point.add(i);
							point_index.add(i);
							directions.add(direction);
							StrokeComponent component = new StrokeComponent();
							component.addDirections(direction);
							component.addPoints(point_index);
							component.addAngles(point_angles);
							components.add(component);
						}

						point_index.clear();
						point_angles.clear();
						temp_point.clear();
					}

				}
				//directions.add(direction);

				//}
			}

			else if(i == angles.size()-1){ // end of stroke
				direction = findDirection(temp_point);
				if(direction != null){
					directions.add(direction);
					point_index.add(i+2);
					StrokeComponent component = new StrokeComponent();
					component.addDirections(direction);
					component.addPoints(point_index);
					component.addAngles(point_angles);
					components.add(component);
				}
				point_index.clear();
				temp_point.clear();
			}

		}

		return components;

	}

	/**
	 * Return value based on Freeman's chain code
	 * @param angle
	 * @return
	 */
	public static int getFreemanCodeDirection(double angle){
		int freeman = 0;

		if(angle >= 180 && angle < 225){
			freeman = 0;
		}
		else if(angle >= 225 && angle < 270){
			freeman = 1;
		}
		else if(angle >= 270 && angle < 315){
			freeman = 2;
		}
		else if(angle >= 315 && angle <= 360){
			freeman = 3;
		}
		else if(angle >= 0 && angle < 45)
		{
			freeman = 4;
		}
		else if(angle >= 45 && angle < 90){
			freeman = 5;
		}
		else if(angle >= 90 && angle < 135){
			freeman = 6;
		}
		else if(angle >= 135 && angle < 180)
		{
			freeman = 7;
		}

		return freeman;
	}

	public double function1(Point center, Point current, Point previous) {

		double ang1 = Math.atan((previous.getY() - center.getY())
				/ (previous.getX() - center.getX()));
		double ang2 = Math.atan((current.getY() - center.getY())
				/ (current.getX() - center.getX()));
		double rslt = ang1 - ang2;

		return Math.toDegrees(rslt) * -1;
	}

	private double function2(Point center, Point current, Point previous) {
		double dx = current.getX() - center.getX();
		double dy = current.getY() - center.getY();
		double a = Math.atan2(dy, dx);

		double dpx = previous.getX() - center.getX();
		double dpy = previous.getY() - center.getY();
		double b = Math.atan2(dpy, dpx);

		double diff = a - b;
		double degres = Math.toDegrees(diff);
		return degres;
	}


	/**
	 * entropy set from Akshay
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public Double getEntropyValue(Point p0, Point p1, Point c){


		if((p0.x == c.x) || (p1.x == c.x)  ){
			return Double.MAX_VALUE;
		}
		double AB = c.distance(p0);
		double BC = c.distance(p1);

		Point vector1 = new Point(p0.x - c.x, p0.y - c.y);
		Point vector2 = new Point(p1.x - c.x, p1.y - c.y);

		double factor2 = (p1.x - c.x)*(p0.x - c.x);
		double factor3 = (p1.y - c.y)*(p0.y - c.y);
		double result = Math.acos(    (factor3+factor2) /(AB*BC));

		double angle = Math.toDegrees(result);

		return angle;

	}


	/**
	 * Get direction by using Freeman's chain code
	 * @param stroke
	 */
	public List<StrokeComponent> getDirection(Stroke stroke){
		List<edu.tamu.core.sketch.Point> points = null;
		List<edu.tamu.core.sketch.Point> replaced_points = new ArrayList<edu.tamu.core.sketch.Point>();
		List<Integer> freemanAngle = new ArrayList<Integer>();
		List<Integer> temp_freemanAngle = new ArrayList<Integer>();
		List<Double> angles = new ArrayList<Double>();
		List<Double> angle_difference = new ArrayList<Double>();
		points = stroke.getPoints();
		System.out.println("new points " + points.toString());

		if(points.size() > 2){
			points.remove(0);
			points.remove(points.size()-1);
		}

		// smoothing
		for(int i = 3; i < points.size() -3; i++){
			double x_value = (points.get(i-3).x + points.get(i-2).x + points.get(i-1).x + points.get(i).x + points.get(i+1).x + points.get(i+2).x)/6;
			double y_value = (points.get(i-3).y + points.get(i-2).y + points.get(i-1).y + points.get(i).y + points.get(i+1).y + points.get(i+2).y)/6;
			Point new_point = new Point(x_value, y_value);
			replaced_points.add(new_point);
		} 

		// re-sampling

		// re-sample points to have only 40
		double resampleWidth = stroke.getPathLength() / NUM_POINTS;
		replaced_points = OneDollarStroke.resamplePoints(replaced_points,
				resampleWidth);

		double angle = 0.0;
		for(int i = 2 ; i < replaced_points.size() -2 ; i++){
			angle = this.getEntropyValue(replaced_points.get(i-2), replaced_points.get(i+2), replaced_points.get(i));
			//System.out.println("angle = " + angle);
			double direction =  getFreemanCodeDirection(angle);
			direction = 	Math.floor(((((angle*16)/(2*3.14)) + 1)%16)/2);
			temp_freemanAngle.add((int) direction);	
		} 


		for(int i = 0 ; i < replaced_points.size() - 2; i++){
			if(i != replaced_points.size()-1){
				//		slopes.add(dy/dx);
				angle = (getAngleInDegrees(replaced_points.get(i+2),replaced_points.get(i)));
				//freemanAngle.add(getFreemanCodeDirection(Math.floor(((((angle*16)/(2*3.14)) + 1)%16)/2)));
				int comp1 = (int) (replaced_points.get(i).getX() - replaced_points.get(i+1).getX());
				int comp2 = (int) (replaced_points.get(i).getY() - replaced_points.get(i+1).getY());

				//temp_freemanAngle.add((int) direction(comp1, comp2));
				freemanAngle.add(getFreemanCodeDirection(getAngleInDegrees(replaced_points.get(i+2),replaced_points.get(i))));
				//System.out.println("Point " + points.get(i) + " getFreemanCodeDirection = " + getFreemanCodeDirection(getAngleInDegrees(points.get(i+1),points.get(i))));
				//System.out.println("Point " + points.get(i) + " result = " + dy/dx + " getFreemanCodeDirection = " + getFreemanCodeDirection(getAngleInDegrees(points.get(i+1),points.get(i))) + " getAngleInRadians = " + getAngleInDegrees(points.get(i+1),points.get(i)));
			}
		}  

		points.clear();
		points = replaced_points;
		stroke.setPoints(points);


		List<StrokeComponent> components = FreemanStringDirection(freemanAngle,points);
		return components;
	}

	/**
	 * Get the angle of the line between the two endpoints. The angle is
	 * returned in radians and will be between the values [0...2*PI] (0 and 360
	 * degrees, if converted). If the endpoints are in the same (x,y) location,
	 * returns 0.
	 * <p>
	 * The angle of the line is computed perceptually, as it would look on the
	 * screen. Since screen coordinates increase Y value as you move down the
	 * screen, this means we have to "flip" the angle to align the values with
	 * Cartesian coordinates, or how the user sees things on the screen and
	 * expects them to be. This means that a line moving from a point on the
	 * screen up and to the right, even though Y values are decreasing since
	 * it's moving up the screen and the angle should be negative value or large
	 * value close to 2*PI (decreasing Y values), will have a positive angle <
	 * Pi/2.
	 * <p>
	 * The notion of "first" and "last" endpoints are used here to preserve
	 * stroke direction and therefore angle. This way, things don't always look
	 * positive.
	 * 
	 * @return The angle of the line in radians.
	 */

	public double getAngleInRadians(edu.tamu.core.sketch.Point first, edu.tamu.core.sketch.Point last) {
		final double TWOPI = 2 * Math.PI;

		// radians
		double deltaY = -first.y + last.y;
		double deltaX = -first.x + last.x;




		// negate the angle because with screen coords, increasing Y values go
		// down. ATAN is expecting cartesian, where increasing Y goes up.
		// negating the angle flips from cartesian to screen orientation.
		double angle = -Math.atan2(deltaY, deltaX);
		// rotate to 0...2*PI
		while (angle < 0) {
			angle += TWOPI;
		}
		while (angle >= TWOPI) {
			angle -= TWOPI;
		}

		return angle;
	}


	/**
	 * Compute the angle in radians, then convert it to degrees. Because of the
	 * computations in {@link #getAngleInRadians()}, the angle returned here
	 * will be in the range [0...360].
	 * <p>
	 * See {@link #getAngleInRadians()} for a discussion about the angle
	 * returned. The angle is based on perception--what the user sees on the
	 * monitor--rather than strictly on the x and y values of the line
	 * endpoints.
	 * 
	 * @return Get the angle of the line in degrees.
	 */
	public double getAngleInDegrees(edu.tamu.core.sketch.Point first, edu.tamu.core.sketch.Point last) {
		// get the angle in radians
		double angle = getAngleInRadians(first,last);
		// to degrees
		angle = angle * 180 / Math.PI;
		return angle;
	}

}
