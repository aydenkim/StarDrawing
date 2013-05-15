package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitFour extends AbstractShape{

	private  DirectionManager directionManager;
	
	public DigitFour(){
		requiredComponents.add("line");
		requiredComponents.add("line");
		requiredComponents.add("line");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "four";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;
		
		Shape horizontal_line = null;
		Shape non_horizontal_line = null;
		Shape vertical_line = null;
		
		
		// first, find the horizontal and vertical line

		
		directionManager = new DirectionManager();
		Point point1 = null;
		Point point2 = null;
		double angle1 = 0.0;
		
		for(int i = 0 ; i < components.size(); i++){
			
			point1 = components.get(i).getPoints().get(components.get(i).getPoints().size()-1);
			point2 = components.get(i).getPoints().get(0);
			angle1 = directionManager.getAngleInDegrees(point1, point2);
			if(angle1 >= 75 && angle1 <= 105){
				vertical_line = components.get(i);
			}else if(angle1 >= 165 && angle1 <= 195){
				horizontal_line = components.get(i);
			}

		}
		
        if(horizontal_line == null || vertical_line == null)
		   return null;
        
		// second, check if a horizontal line and a vertical line intersects a middle point of the horizontal line
		
        		
		
        double range_1 = Math.abs(horizontal_line.getPoints().get(horizontal_line.getPoints().size()-1).getX() - horizontal_line.getPoints().get(0).getX()) / 3 + horizontal_line.getPoints().get(0).getX();
        double range_2 = Math.abs(horizontal_line.getPoints().get(horizontal_line.getPoints().size()-1).getX() - horizontal_line.getPoints().get(0).getX()) / 3  + range_1;
        
        double value = vertical_line.getPoints().get(vertical_line.getPoints().size()/2).getX();
        
        System.out.println("in digit foir " + ((vertical_line.getPoints().get(vertical_line.getPoints().size()/2).getX() >= range_1) && (vertical_line.getPoints().get(vertical_line.getPoints().size()/2).getX() <= range_2)));
		if((vertical_line.getPoints().get(vertical_line.getPoints().size()/2).getX() >= range_1) && (vertical_line.getPoints().get(vertical_line.getPoints().size()/2).getX() <= range_2)){
			Shape newShape = new Shape();
			Stroke newStroke = new Stroke();
			for(Point tempPoint1 : components.get(0).getPoints()){
				newStroke.addPoint(tempPoint1);
			}
			for(Point tempPoint2 : components.get(1).getPoints()){
				newStroke.addPoint(tempPoint2);
			}

			newShape.add(newStroke);
			newShape.setLabel("four");	
			System.out.println("It's a digit four");	
			return newShape;
		}

		return null;
		
		
	}

}
