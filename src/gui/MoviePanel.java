
/**
 * MoviePanel.java
 * 
 * Revision History:<br>
 * Aug 1, 2012 gigemjt - File created
 *
 * <p>
 * <pre>
 * This work is released under the BSD License:
 * (C) 2012 Sketch Recognition Lab, Texas A&M University (hereafter SRL @ TAMU)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sketch Recognition Lab, Texas A&M University 
 *       nor the names of its contributors may be used to endorse or promote 
 *       products derived from this software without specific prior written 
 *       permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY SRL @ TAMU ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SRL @ TAMU BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */

package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import edu.tamu.core.sketch.Point;
import edu.tamu.core.sketch.Sketch;
import edu.tamu.core.sketch.Stroke;

public class MoviePanel extends JPanel
{
	public static void Test(Sketch k)
	{
		
		JFrame frame=new JFrame();
		frame.setVisible(false);
		frame.setSize(650,650);
		/*
		MoviePanel panel=new MoviePanel(k);
		frame.add(panel);
		frame.setVisible(true);
		panel.reorder();
		panel.setRepeat(true);
		panel.start();
		panel.play();
		*/
		frame.add(createMoviePanel(k));
		frame.setVisible(true);
		
	}
	Sketch sketch;
	TreeMap<Long,Point> queue;
	ArrayList<Point> displayingPoints;
	int numPoints;
	Timer t;
	boolean repeat;
	boolean clear;
	
	public void addStopButton(JPanel p)
	{
		
	}
	public static JPanel createMoviePanel(Sketch k)
	{
		final MoviePanel movie=new MoviePanel(k);
		movie.setRepeat(true);
		JPanel panel=new JPanel();
		JButton stop=new JButton("Stop");
	//	stop.setPreferredSize(new Dimension(100,100));
		stop.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				movie.stop();
			}
		});
		JButton play=new JButton("Play");
	//	play.setPreferredSize(new Dimension(100,100));
		play.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				movie.reorder();
				movie.start();
				movie.play();
			}
		});
		
		JPanel buttonPanel=new JPanel();
		buttonPanel.add(stop);
		buttonPanel.add(play);
		buttonPanel.setPreferredSize(new Dimension(202,102));
		panel.add(buttonPanel);
		movie.setPreferredSize(new Dimension(800,600));
		panel.add(movie);
		return panel;
	}
	/*
	boolean finished;
	long finalTime;
	long startTime;
	Point lastPoint;
	Point nextPoint;
	*/
	public MoviePanel(Sketch k)
	{
		sketch=k;
		queue=new TreeMap<Long,Point>();
		displayingPoints=new ArrayList<Point>();
	}
	List<Stroke> m_strokes;
	public void reorder()
	{
		displayingPoints.clear();
		queue.clear();
		numPoints=0;
//		System.out.println("before");
		m_strokes=sketch.getStrokes();
//		System.out.println("after");
		/*
		lastPoint=m_strokes.get(0).getPoint(0);
		finalTime=lastPoint.getTime();
		nextPoint=m_strokes.get(0).getPoint(1);
		*/
		//*
		long time=5;
		for(Stroke k:m_strokes)
		{
			for(Point point:k.getPoints())
			{
			//	System.out.println("Trying time:"+point.getTime()+", x: "+point.getX()+", y: "+point.getY());
				if(time>0L)//&&!queue.containsKey(time))
				{
					time+=30;
					queue.put(time, point);
					numPoints++;
		//			System.out.println("Reording! "+point.getTime());
				}else
				{
		//			System.out.println("Throwing out time:"+point.getTime()+", x: "+point.getX()+", y: "+point.getY());
				}
			}
			time+=100;
		}
	//	*/
		System.out.println("Done ordering");
		//now everything is ordered!
	}
	public void paintComponent(Graphics g2)
	{
		Graphics2D g = (Graphics2D)g2;
		BasicStroke drawingStroke = new BasicStroke(4);
		g.setStroke(drawingStroke);
		if(clear)
		{
			clear=false;
			g.setColor(Color.white);
			g.fillRect(0,0,getWidth(), getHeight());
			g.setColor(Color.black);
		}
		if(displayingPoints.size()==1)
		{
			g.setColor(Color.red);
			g.fillOval((int)displayingPoints.get(0).x-6, (int)displayingPoints.get(0).y-6, 11,11);
		}else if(displayingPoints.size()>2)
		{
			Point p1=displayingPoints.get(displayingPoints.size()-1);
			Point p2=displayingPoints.get(displayingPoints.size()-2);
			if(p1!=null&&p2!=null)
			{
				g.setColor(Color.black);
				g.drawLine((int)p1.getX(),(int)p1.getY(), (int)p2.getX(),(int)p2.getY());
			}
		}
		/*
		for(int k=0;k<displayingPoints.size();k++)
		{			
			Point p1=displayingPoints.get(k);
			if(p1==null)
			{
				continue;
			}
			//p1 is not null
			Point p2=null;
			if(k+1<displayingPoints.size())
				p2=displayingPoints.get(k+1);
			
			
			if(p2==null)
			{
				//p1 is the last point
				//g.drawRect((int)p1.getX(),(int)p1.getY(), 5, 5);
			}
			else
				g.drawLine((int)p1.getX(),(int)p1.getY(), (int)p2.getX(),(int)p2.getY());
			if(k<10)
			{
				g.setColor(Color.red);
				g.fillOval((int)displayingPoints.get(0).x-6, (int)displayingPoints.get(0).y-6, 11,11);
			}
			
		}
		//*/
		/*
		boolean stop=false;
		for(int q=0;q<m_strokes.size();q++)
		{
			System.out.println("painting");
			Stroke stroke=m_strokes.get(q);
			ArrayList<Point> points=(ArrayList<Point>) stroke.getPoints();
			for(int k=0;k<points.size();k++)
			{	
				Point p1=points.get(k);
				System.out.println("painting p1 "+k);
				if(p1.getTime()>finalTime)
				{
					System.out.println("p1 is over");
					lastPoint=p1;
					stop=true;
					break;
				}
				//p1 is not null
				Point p2=null;
				if(k+1<displayingPoints.size())
					p2=points.get(k+1);
				if(p2.getTime()>finalTime)
				{
					System.out.println("p2 is over");
					lastPoint=p1;
					nextPoint=p2;
					p2=null;
				}
				g.setColor(Color.black);
				if(p2==null)
				{
					//p1 is the last point
					//g.drawRect((int)p1.getX(),(int)p1.getY(), 5, 5);
				}
				else
					g.drawLine((int)p1.getX(),(int)p1.getY(), (int)p2.getX(),(int)p2.getY());
				if(k<2)
				{
					g.setColor(Color.red);
					g.fillOval((int)points.get(0).x-6, (int)points.get(0).y-6, 11,11);
				}
				
			}
			if(stop)
			{
				if(q+1>=m_strokes.size())
				{
					finished=true;
				}else
				{
					nextPoint=m_strokes.get(q+1).getPoint(0);
				}
				break;
				//stop drawing everything
			}
		}
		*/
	}
	public void start()
	{
	//	finished=false;
		t=new Timer(0,new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
			//	if(!finished)
				if(numPoints > displayingPoints.size()+1)//who needs the last point
				{
					addPoint();
				}
				else 
					if(repeat)
				{
					
					stop();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Thread thread=new Thread()
					{
						public void run()
						{
							reorder();					
							start();
							play();
						}
					};
					thread.start();
				}else
				{
					stop();
				}
			}
		});
		
		//this will start the timer
	}
	public void stop()
	{
		t.stop();
		displayingPoints.clear();
		queue.clear();
		clear=true;
	}
	public void pause()
	{
		t.stop();
	}
	public void play()
	{
		t.start();
	}
	
	public void addPoint()
	{
	//	System.out.println("adding point");
		//if should never reach the end
		t.stop();//maybe change it so that it doesn't start and stop?
//		finalTime=nextPoint.getTime();
		//*
		long time=queue.firstKey();//the next point in the list
		Point p=queue.remove(time);
		displayingPoints.add(p);
		if(displayingPoints.size()<numPoints)
		{
		//	System.out.println(displayingPoints.size()+"<"+numPoints);
			long nextTime=queue.firstKey();
			long split=((long)nextTime-time);
//			System.out.println("next split is "+split);
//			System.out.println("next split as int "+((int)split));
			if(split>80)
			{
				displayingPoints.add(null);
				numPoints++;
				split=100;
			}else
			{
				//displayingStrokes.get(displayingStrokes.size()-1).addPoint(p);
			}
			t.setInitialDelay((int)split);
			t.start();
			repaint();
		}else
		{
			t.setInitialDelay((int)1000);
			t.start();
		}
	//	*/
	//	t.setInitialDelay((int)(nextPoint.getTime()-lastPoint.getTime()));
		t.start();
		repaint();
	}
	public void setRepeat(boolean b)
	{
		repeat=b;
	}
}

