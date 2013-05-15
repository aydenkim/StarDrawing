package shapes;

import java.util.List;

import recognition.DirectionManager;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Shape;

public class DigitOne extends AbstractShape{

	private  DirectionManager directionManager;
	
	public DigitOne(){
		requiredComponents.add("line");
	}
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "one";
	}

	@Override
	public Shape recognize(List<Shape> components) {
		// TODO Auto-generated method stub
		if (!hasRequiredComponents(components))
			return null;
		
		directionManager = new DirectionManager();
		
		Point point1 = components.get(0).getPoints().get(components.get(0).getPoints().size()-1);
		Point point2 = components.get(0).getPoints().get(0);
		
		double angle = directionManager.getAngleInDegrees(point1, point2);

		if(angle >= 30 && angle <= 95){ 
			components.get(0).setLabel("one");
			
		}

		System.out.println("It's a digit one");
		
		
		return components.get(0);
	}

}
