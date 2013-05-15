package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitSeven1 extends AbstractShape{

	private  DirectionManager directionManager;

	public DigitSeven1(){
		requiredComponents.add("line");
		requiredComponents.add("line");
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
		Shape non_horizontal_line = null;
		Shape newShape = null;;

		/*for(Shape shape : components){
			if(shape.getInterpretation().label.equals("line")){
				horizontal_line = shape;
			}else if(shape.getInterpretation().label.equals("down")){
				curve = shape;
			}
		}*/

		Point point1 = components.get(0).getPoints().get(components.get(0).getPoints().size()-1);
		Point point2 = components.get(0).getPoints().get(0);
		Point point3 = components.get(1).getPoints().get(components.get(1).getPoints().size()-1);
		Point point4 = components.get(1).getPoints().get(0);

		double angle1 = directionManager.getAngleInDegrees(point1, point2);
		double angle2 = directionManager.getAngleInDegrees(point3, point4);
		int direction1 = directionManager.getFreemanCodeDirection(angle1);
		int direction2 = directionManager.getFreemanCodeDirection(angle2);



		if(direction1 == 4.0 || direction1 == 0.0){ 
			horizontal_line = components.get(0);
			if(direction2 == 5 || direction2 == 6){
				non_horizontal_line = components.get(1);
			}
		}else if(direction2 == 4.0 || direction2 == 0.0){
			horizontal_line = components.get(1);
			if(direction1 == 5 || direction1 == 6){
				non_horizontal_line = components.get(0);
			}
		}

		//number seven should have one horizontal line and one non-horizontal line
		if(horizontal_line != null && non_horizontal_line != null){
			//System.out.println("non_horizontal_line.getPoints() = " + non_horizontal_line.getPoints().toString());
			//System.out.println("horizontal_line.getPoints() = " + horizontal_line.getPoints().toString());
			double non_horizontal_middle_y = non_horizontal_line.getPoints().get(non_horizontal_line.getPoints().size()/2).getY();
			double horizontal_middle_y = horizontal_line.getPoints().get(horizontal_line.getPoints().size()/2).getY();
			if(horizontal_line.getPoints().get(0).getY() < non_horizontal_line.getPoints().get(0).getY()){
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
