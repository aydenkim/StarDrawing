package recognition;

import java.util.ArrayList;
import java.util.List;

/**
 * class for saving directions and points
 * @author Ayden Kim
 *
 */

public class StrokeComponent {
	List<Integer> points_index = new ArrayList<Integer>();
	List<Integer> points_angles = new ArrayList<Integer>();
	Direction direction = null;
	public StrokeComponent(){};

	public void addPoints(List<Integer> indexes){
		points_index.addAll(indexes);
	}
	public void addAngles(List<Integer> indexes){
		points_angles.addAll(indexes);
	}
	public void addDirections(Direction direction){
		this.direction = direction;
	}

}
