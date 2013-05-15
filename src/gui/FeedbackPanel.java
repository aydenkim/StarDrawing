package gui;

/*******************************************************************************
 *  Revision History:<br>
 *  SRL Member - File created
 *  dalogsdon - changed default FPS to 60 for buttery smoothness
 *
 *  <p>
 *  <pre>
 *  This work is released under the BSD License:
 *  (C) 2011 Sketch Recognition Lab, Texas A&M University (hereafter SRL @ TAMU)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Sketch Recognition Lab, Texas A&M University 
 *        nor the names of its contributors may be used to endorse or promote 
 *        products derived from this software without specific prior written 
 *        permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  </pre>
 *  
 *******************************************************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FeedbackPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7135905058468322497L;

	// private boolean correct;
	public String message = "";
	private Timer outTimer, returnTimer, endTimer, displayTimer;
	private boolean shouldShow = false;
	private int delay;
	private double increment, slideTime, displayTime;
	private double animY;

	private static final int HEIGHT = 80;
	private static final int EDGE_PADDING = 40;
	private static final int CORNER_RADIUS = 25;
	private static final int WANTED_POSITION = -(HEIGHT / 2); // enough position
																// can be read

	public static final Color INCORRECT_COLOR = new Color(255, 127, 40);
	public static final Color CORRECT_COLOR = new Color(50, 205, 50);

	public FeedbackPanel() {
		this(60.0, 0.75, 4.0);
	}

	/**
	 * Display a message at the top of the panel.
	 * 
	 * @param fps
	 *            frames per second
	 * @param slideTime
	 *            time to spend animating down (and back up) in seconds
	 * @param displayTime
	 *            time to wait between animations, in seconds
	 */
	public FeedbackPanel(double fps, double slideTime, double displayTime) {
		delay = (int) (1000.0 / fps);
		increment = HEIGHT / (2 * slideTime * fps);
		this.slideTime = slideTime;
		this.displayTime = displayTime;
		setFont(new Font("Tahoma", 0, 18));
		setOpaque(false);
		System.out.println("expected: " + slideTime * fps);
	}

	public void displayResponse(String message, boolean correct) {
		displayResponse(message, correct, null, null, null, null);
	}

	public void displayStatic(String message, boolean correct) {
		setBackground((correct) ? CORRECT_COLOR : INCORRECT_COLOR);
		setForeground(Color.BLACK);

		this.message = message;
		stopTimers();

		shouldShow = true;
		animY = -HEIGHT / 2.0;
	}

	@Override
	public void setVisible(boolean visibility) {
		shouldShow = visibility;
		if (!visibility)
			stopTimers();
		
		super.setVisible(visibility);
	}

	public void displayResponse(String message, boolean correct,
			ArrayList<ActionListener> outActions,
			ArrayList<ActionListener> returnActions,
			ArrayList<ActionListener> displayActions,
			ArrayList<ActionListener> endActions) {
		setVisible(true);
		setBackground((correct) ? CORRECT_COLOR : INCORRECT_COLOR);
		setForeground(Color.BLACK);

		this.message = message;

		stopTimers();
		shouldShow = true;

		animY = -HEIGHT;

		outTimer = makeAnimationTimer(1.0, 0, outActions, displayActions);
		returnTimer = makeAnimationTimer(-1.0,
				(int) (1000.0 * (slideTime + displayTime)), returnActions, null);
		
		endTimer = makeStopTimer(returnTimer,
				2*(int) (1000.0 * (slideTime * 2.0 + displayTime)),
				true, endActions);
		endTimer.setDelay(100);

	}

	/*
	 * Overloaded makeAnimationTimer It calls makeStopTimer when the position is
	 * below than threshold. Also, if the position is below than threshold, it
	 * forces to stop running timer because the message position is enough to
	 * read. Added parameter : ArrayList<ActionListener> Timeractions =>
	 * parameter for makeAnimationTimer
	 */
	private Timer makeAnimationTimer(final double upDown, int initialDelay,
			ArrayList<ActionListener> actions,
			final ArrayList<ActionListener> Timeractions) {

		ActionListener temp = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			}
		};

		final Timer res = new Timer(initialDelay, temp);

		Action animateAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				updatePosition(increment * upDown);
				if (animY >= WANTED_POSITION) { // if the message position is
												// enough to read stop this
												// timer, but if the message
												// position is not
												// enough to read until the
												// running time, it does not
												// stop this timer.
					res.stop();
					if (Timeractions != null)
						for (ActionListener al : Timeractions)
							al.actionPerformed(e);
				}
				repaint();
			}
		};
		res.addActionListener(animateAction);
		res.removeActionListener(temp);

		if (actions != null) {
			for (ActionListener al : actions)
				res.addActionListener(al);
		}
		res.setDelay(delay);
		res.setRepeats(true);
		res.start();
		return res;
	}

	private Timer makeStopTimer(final Timer toStop, int msDelay,
			final boolean lastTimer, ArrayList<ActionListener> actions) {
		Action stopAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				toStop.stop();
				if (lastTimer)
					shouldShow = false;
			}
		};

		Timer res = new Timer(msDelay, stopAction);
		if (actions != null) {
			for (ActionListener al : actions)
				res.addActionListener(al);
		}
		res.setInitialDelay(msDelay);
		res.setRepeats(false);
		res.start();
		return res;
	}

	private void stopTimers() {
		if (outTimer != null) {
			outTimer.stop();
		}
		if (returnTimer != null) {
			returnTimer.stop();
		}
		if (displayTimer != null) {
			displayTimer.stop();
		}
		if (endTimer != null) {
			if (endTimer.isRunning()) {
				endTimer.stop();
				for (ActionListener al : endTimer.getActionListeners())
					al.actionPerformed(null);
			} else {
				endTimer.stop();
			}
		}
	}

	@Override
	public void paintComponent(Graphics gOld) {
		if (shouldShow) {
			Graphics2D g = (Graphics2D) gOld;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);

			FontMetrics fm = g.getFontMetrics(g.getFont());
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(message, g);
			// System.out.println(numUpdate);
			// center text
			int width = getWidth() - EDGE_PADDING;
			int offsetX = (getWidth() - width) / 2;

			int textWidth = (int) rect.getWidth();
			int textHeight = (int) rect.getHeight();
			int textX = (getWidth() - textWidth) / 2;
			int textY = (int) (animY + 3.0 * HEIGHT / 4.0 + textHeight / 2.0) - 2;

			int extraPad = 4;
			Color bg = getBackground();
			g.setColor(bg);
			g.fillRoundRect(offsetX - extraPad, (int) animY, width + 2
					* extraPad, HEIGHT + extraPad, CORNER_RADIUS + extraPad,
					CORNER_RADIUS + extraPad);

			// g.setColor(new Color((int) (bg.getRed()*(1-m)+255*m),
			// (int) (bg.getGreen()*(1-m)+255*m),(int)
			// (bg.getBlue()*(1-m)+255*m)));
			g.setPaint(new GradientPaint(0, 0, lighterColor(bg, 0.7), 0,
					HEIGHT / 2, lighterColor(bg, 0.2)));
			g.fillRoundRect(offsetX, (int) animY, width, HEIGHT, CORNER_RADIUS,
					CORNER_RADIUS);

			g.setColor(getForeground());
			g.drawString(message, textX, textY);
		}
	}

	public void updatePosition(double increment) {

		animY += increment;

	}

	/**
	 * creates a whiter color from a color
	 * 
	 * @param c
	 *            the base color
	 * @param m
	 *            the multiplier 0<=m<=1
	 * @return the new lighter color
	 */
	private Color lighterColor(Color c, double m) {
		return new Color((int) (c.getRed() * (1 - m) + 255 * m),
				(int) (c.getGreen() * (1 - m) + 255 * m), (int) (c.getBlue()
						* (1 - m) + 255 * m));
	}
}
