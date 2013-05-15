package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JLayeredPane;
import javax.swing.OverlayLayout;

import org.ladder.ui.drawpanel.old.BackgroundImagePanel;

import edu.tamu.core.gui.ISketchObserver;
import edu.tamu.core.gui.JDrawPanel;
import edu.tamu.core.sketch.Sketch;

public class MathSketchCanvas extends JLayeredPane {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6510496687672736410L;
	
	private JDrawPanel drawPanel;
	
	private BackgroundImagePanel background;
	
	public static final int SKETCH = 1;
	public static final int BACKGROUND = 2;
	public static final int EVERYTHING = SKETCH | BACKGROUND;
	
	public MathSketchCanvas(){
		setLayout(new OverlayLayout(this));

		background = new BackgroundImagePanel(null);
		background.setBackground(Color.WHITE);
		add(background, 0);

		drawPanel = new JDrawPanel();
		drawPanel.setOpaque(false);
		add(drawPanel, 0);
	}
	
	public MathSketchCanvas(Sketch s){
		this();
		drawPanel.setSketch(s);
	}
	
	public void setSketch(Sketch s) {
		drawPanel.setSketch(s);
	}

	public Sketch getSketch() {
		return drawPanel.getSketch();
	}

	@Override
	public void setPreferredSize(Dimension d) {
		super.setPreferredSize(d);
		drawPanel.setPreferredSize(d);
		background.setPreferredSize(d);
	}

	public void setStrokeWidth(float width) {
		drawPanel.setStrokeWidth(width);
	}

	public void setStrokeColor(Color color) {
		drawPanel.setStrokeColor(color);
	}

	public void clear() {
		clear(EVERYTHING);
	}

	public void clear(int howMuch) {
		if ((howMuch & BACKGROUND) != 0) {
			background.setBackground(Color.WHITE);
		}

		if ((howMuch & SKETCH) != 0) {
			drawPanel.clear();
		}
		repaint();
	}
	
	public void addObserver(ISketchObserver observer){
		drawPanel.addObserver(observer);
	}
	
	/**
	 * Set the background image shown behind the draw panel
	 * 
	 * @param backgroundImage
	 *            image to show behind the draw panel
	 */
	public void setBackgroundImage(Image backgroundImage) {
		if (backgroundImage != null) {
			background.setBackgroundImage(backgroundImage);
		}
	}

	/**
	 * Set the background image shown behind the draw panel
	 * 
	 * @param imageLoc
	 *            location of the image on file
	 */
	public void setBackgroundImage(String imageLoc) {
		setBackgroundImage(getToolkit().getImage(imageLoc));
	}

}
