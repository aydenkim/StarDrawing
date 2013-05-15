package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitSeven2 extends AbstractShape{

	private  DirectionManager directionManager;

	public DigitSeven2(){
		requiredComponents.add("line");
		requiredComponents.add("line");
		requiredComponents.add("down");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "seven";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;

		directionManager = new DirectionManager();
		Shape horizontal_line = null;
		Shape vertical_line = null;
		Shape curve = null;
		Shape newShape = null;
		Point point1 = null;
		Point point2 = null;

		double angle1 = 0.0;
		int direction = 0;
		
		for(Shape shape : components){
			if(shape.getInterpretation().label.equals("line")){
				point1 = shape.getPoints().get(shape.getPoints().size()-1);
				point2 = shape.getPoints().get(0);
				angle1 = directionManager.getAngleInDegrees(point1, point2);
				direction = directionManager.getFreemanCodeDirection(angle1);
				if(direction == 4.0 || direction == 0.0){ 
				   horizontal_line = shape;
				}else if(direction == 1.0 || direction == 2.0){
					vertical_line = shape;
				}
			}else if(shape.getInterpretation().label.equals("down")){
				point1 = shape.getPoints().get(shape.getPoints().size()-1);
				point2 = shape.getPoints().get(0);
				angle1 = directionManager.getAngleInDegrees(point1, point2);
				direction = directionManager.getFreemanCodeDirection(angle1);
				if(direction == 5.0 || direction == 6.0){ 
					curve = shape;
				}				
			}
		}
		
		
		// check if horizontal line is in right side of vertical line
		Point comp1 = horizontal_line.getPoints().get(0);
		Point comp2 = horizontal_line.getPoints().get(horizontal_line.getPoints().size()-1);
		Point compare = null;
		if(comp1.getX() > comp2.getX()){
			compare = comp1;
		}else{
			compare = comp2;
		}

		if( (vertical_line.getPoints().get(0).getX() > compare.getX()) || (vertical_line.getPoints().get(vertical_line.getPoints().size()-1).getX() > compare.getX())){
			return null;
		}

		//number seven should have one horizontal line and one non-horizontal line
		if(horizontal_line != null && curve != null && vertical_line != null){
			if(curve.getPoints().get(0).getY() > horizontal_line.getPoints().get(0).getY()){
				newShape = new Shape();
				Stroke newStroke = new Stroke();
				for(Point tempPoint1 : components.get(0).getPoints()){
					newStroke.addPoint(tempPoint1);
				}
				for(Point tempPoint2 : components.get(1).getPoints()){
					newStroke.addPoint(tempPoint2);
				}
				newShape.add(newStroke);
				newShape.setLabel("seven");	
				System.out.println("It's a digit seven");	
			}
		}

		return newShape;
	}

}
