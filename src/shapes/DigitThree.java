package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;
import edu.tamu.core.sketch.Stroke;

public class DigitThree extends AbstractShape{

	private  DirectionManager directionManager;

	public DigitThree(){
		requiredComponents.add("down");
		requiredComponents.add("down");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "three";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;


		Shape newShape = new Shape();
		Stroke newStroke = new Stroke();
		for(Point tempPoint1 : components.get(0).getPoints()){
			newStroke.addPoint(tempPoint1);
		}
		for(Point tempPoint2 : components.get(1).getPoints()){
			newStroke.addPoint(tempPoint2);
		}
		newShape.add(newStroke);
		newShape.setLabel("digit three");	

		System.out.println("It's a digit three");		

		return components.get(0);
	}

}
