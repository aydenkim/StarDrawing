package shapes;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.core.sketch.Shape;

public abstract class AbstractShape {

	/**
	 * Use this to check that all required components are present before
	 * recognition.
	 */
	protected ArrayList<String> requiredComponents = new ArrayList<String>();

	public abstract String getLabel();


	/**
	 * Attempt to recognize this symbol from a list of components.
	 * 
	 * @param components
	 *            the candidate components.
	 * @return an Shape containing the recognized shape + confidence, or null if
	 *         it is not possible to recognize.
	 */
	public abstract Shape recognize(List<Shape> components);

	/**
	 * Check if a given list of components is the same as our list of required
	 * components.
	 * 
	 * @param components
	 * @return
	 */
	protected boolean hasRequiredComponents(List<Shape> components) {
		if (components.size() != requiredComponents.size())
			return false;

		ArrayList<String> test = new ArrayList<String>(requiredComponents);

		for (Shape is : components) {
			try {
				if (is.getInterpretation().label != null) {
					String label = is.getInterpretation().label.toLowerCase();
					if (!test.remove(label))
						return false;
				} else
					return false;
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return true;
	}
}
