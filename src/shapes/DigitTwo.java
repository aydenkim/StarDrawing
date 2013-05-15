package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitTwo extends AbstractShape{

	private  DirectionManager directionManager;

	public DigitTwo(){
		requiredComponents.add("down");
		requiredComponents.add("line");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "two";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;

		// check if it has a horizontal line
		directionManager = new DirectionManager();
		List<Point> points = components.get(1).getPoints();
		List<Point> points1 = components.get(0).getPoints();


		Shape line = null;
		Shape curve = null;
		Shape newShape = null;
		Point point1 = null;
		Point point2 = null;

		for(Shape shape : components){
			if(shape.getInterpretation().label.equals("line")){		
				point1 = shape.getPoints().get(shape.getPoints().size()-1);
				point2 = shape.getPoints().get(0);
				double angle = directionManager.getAngleInDegrees(components.get(1).getFirstStroke().getFirstPoint(), point2);
				int direction = directionManager.getFreemanCodeDirection(angle);
				if(direction == 0 || direction == 4){  // check if it is horizontal line
					line= shape;		
				}
			}else{
				curve = shape;
			}
		}

		if(line == null || curve == null)
			return null;
		else{
			// check if the curve is above then the line
			if(curve.getPoints().get(0).getY() < line.getPoints().get(0).getY()){
				newShape = new Shape();
				Stroke newStroke = new Stroke();
				for(Point tempPoint1 : components.get(0).getPoints()){
					newStroke.addPoint(tempPoint1);
				}
				for(Point tempPoint2 : components.get(1).getPoints()){
					newStroke.addPoint(tempPoint2);
				}

				if(curve.getPoints().size() > line.getPoints().size()){
					newShape.add(newStroke);
					newShape.setLabel("two");	
					System.out.println("It's a digit two");		
				}
			}



			return newShape;
		}
	}
}
