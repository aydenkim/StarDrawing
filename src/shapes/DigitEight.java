package shapes;

import java.util.ArrayList;
import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;

public class DigitEight extends AbstractShape{

	private  DirectionManager directionManager;
	
	public DigitEight(){
		requiredComponents.add("loop");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "eight";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;
	
		//first check if it can be two loop ex) eight
		List<Point> tempPoints = components.get(0).getPoints();

		// find the intersected point
		Point topPoint = null;
		Point downPoint = null;
		
		double maxX = 0.0;
		double minX = Double.MAX_VALUE;
		double maxY = 0.0;
		double minY = Double.MAX_VALUE;
		
		for(Point point : tempPoints){
			if(maxX < point.getX())
				maxX = point.getX();
			if(minX > point.getX())
				minX = point.getX();
			if(maxY < point.getY())
				maxY = point.getY();
			if(minY > point.getY())
				minY = point.getY();
		}   
		
		double middleX = (maxX + minX)/2.0;
		double middleY = (maxY + minY)/2.0;
		Point tempMiddlePoint = new Point(middleX,middleY);
		Point intersectedPoint = null;
		Point point = null;
		int cnt = 0;
		for(cnt = 0 ;  cnt < tempPoints.size(); cnt++){
			point = tempPoints.get(cnt);
			if(point.distance(tempMiddlePoint) <= 10.0){
				intersectedPoint = point;
				System.out.println("found intersected point");
				break;
			}
		}
		
		if(point == null)
			return null;
		
		System.out.println("It's digit eight");
		components.get(0).setLabel("eight");
		
		return components.get(0);
	}

}
