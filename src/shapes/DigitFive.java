package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitFive extends AbstractShape{

	private  DirectionManager directionManager;

	public DigitFive(){
		requiredComponents.add("line");
		requiredComponents.add("down");
		requiredComponents.add("line");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "five";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;

		Shape horizontal_line = null;
		Shape curve_line = null;
		Shape vertical_line = null;


		// first, find the horizontal and vertical line


		directionManager = new DirectionManager();
		Point point1 = null;
		Point point2 = null;
		double angle1 = 0.0;

		for(int i = 0 ; i < components.size(); i++){
			if(components.get(i).getInterpretation().label.equals("line")){
				point1 = components.get(i).getPoints().get(components.get(i).getPoints().size()-1);
				point2 = components.get(i).getPoints().get(0);
				angle1 = directionManager.getAngleInDegrees(point1, point2);
				if(angle1 >= 60 && angle1 <= 105){
					vertical_line = components.get(i);
				}else if(angle1 >= 165 && angle1 <= 195){
					horizontal_line = components.get(i);
				}
			}else if(components.get(i).getInterpretation().label.equals("down")){
				curve_line = components.get(i);
			}

		}

		if(horizontal_line == null || vertical_line == null || curve_line == null)
			return null;

		// second, check if a horizontal line and a vertical line intersects a middle point of the horizontal line

        Double below_curve_y = curve_line.getPoints().get(0).y;
        
        for(Point point : curve_line.getPoints()){
        	if(below_curve_y < point.y){
        		below_curve_y = point.y;
        	}
        }
        
        Double below_vertical_y = vertical_line.getPoints().get(0).y;
        Point  high_vertical = vertical_line.getPoints().get(0);
        Point  below_horizontal = horizontal_line.getPoints().get(0);

        for(Point point : vertical_line.getPoints()){
        	if(below_vertical_y < point.y){
        		below_vertical_y = point.y;
        	}
        	if(high_vertical.getY() > point.y){
        		high_vertical = point;
        	}
        }
        
        for(Point point : horizontal_line.getPoints()){
        	if(below_horizontal.getY() < point.y){
        		below_horizontal = point;
        	}
        }
		
        if(below_vertical_y < below_curve_y){
        	if(below_vertical_y > below_horizontal.y){
        		Shape newShape = new Shape();
    			Stroke newStroke = new Stroke();
    			for(Point tempPoint1 : components.get(0).getPoints()){
    				newStroke.addPoint(tempPoint1);
    			}
    			for(Point tempPoint2 : components.get(1).getPoints()){
    				newStroke.addPoint(tempPoint2);
    			}
    			for(Point tempPoint3 : components.get(2).getPoints()){
    				newStroke.addPoint(tempPoint3);
    			}

    			newShape.add(newStroke);
    			newShape.setLabel("five");	
    			System.out.println("It's a digit five");	
    			return newShape;
        	}
        }

		return null;


	}

}
