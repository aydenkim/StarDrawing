/*******************************************************************************
 *  Revision History:<br>
 *  SRL Member - File created
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
package org.ladder.recognition.bullseye;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Clusterer for the BullseyeRecognizer.
 * @author pcorey
 *
 */
public abstract class Clusterer {

	/**
	 * Clusters found by a clustering algorithm
	 * @author pcorey
	 *
	 */
	public class Cluster{

		/**
		 * Center location of the cluster.  Determined by the algorithm.  Could use a data point or the average of the cluster
		 */
		protected Bullseye m_center;
		
		/**
		 * The members of the cluster.  Who belongs
		 */
		protected List<Bullseye> m_members;
		
		/**
		 * The radius/width of the cluster
		 */
		private double m_radius;
		
		/**
		 * Create an empty cluster at a center point 
		 * @param center
		 */
		public Cluster(Bullseye center){
			m_radius=0;
			m_center=center;
			m_members = new ArrayList<Bullseye>();
		}
		
		/**
		 * Add a member to the cluster
		 * @param member The member to adds
		 */
		public void add(Bullseye member){
			m_members.add(member);
			if(m_radius<m_center.compareTo(member))
				m_radius=m_center.compareTo(member);
		}
		
		/**
		 * Get the cluster center
		 * @return The cluster center
		 */
		public Bullseye getCenter(){
			return m_center;
		}
		
		/**
		 * Get the list of cluster members
		 * @return List of cluster members
		 */
		public List<Bullseye> getMembers(){
			return m_members;
		}
		
		/**
		 * Get the number of members in the class
		 * @return The number of members
		 */
		public int size(){
			return m_members.size();
		}
		
		/**
		 * Get the distance to a data point from the cluster 
		 * @param bullseye
		 * @return
		 */
		public double distanceTo(Bullseye bullseye){
			return m_center.compareTo(bullseye);
		}
	}

	/**
	 * The clusters found by the clusterer
	 */
	protected List<Cluster> m_clusters;
	
	/**
	 * The cluster centers found by the clusterer
	 */
	protected List<Bullseye> m_centers;
	
	/**
	 * Create an empty cluster
	 */
	protected Clusterer(){
		m_clusters = new ArrayList<Cluster>();
		m_centers = new ArrayList<Bullseye>();
	}
	
	/**
	 * A Quality Threshold Clusterer.
	 * Iteratively creates the largest possible cluster from the remaining points such that the maximum distance
	 * between any two cluster members is below the threshold.  Terminates when no points remain to cluster or 
	 * the number of clusters exceeds a cut off
	 * @author pcorey
	 *
	 */
	public static class QTClusterer extends Clusterer{
		
		/**
		 * Cluster the data using the specified threshold until enough clusters are found
		 * @param data The data to cluster
		 * @param numberClusters The 
		 * @param threshold
		 */
		public QTClusterer(List<Bullseye> data,int numberClusters, double threshold){
			super();
			//System.out.println("Clustering");
			List<Bullseye> myData = new ArrayList<Bullseye>();
			myData.addAll(data);
			for(int i=0;i<numberClusters;i++){
				Cluster addCluster = new Cluster(null);
				//System.out.println("Points left to cluster: " + myData.size());
				for(Bullseye curCenter : myData){
					ArrayList<Bullseye> temp = new ArrayList<Bullseye>(myData);
					Cluster cluster = new Cluster(curCenter);
					cluster.add(curCenter);
					List<Bullseye> inside = new ArrayList<Bullseye>();
					for(Bullseye otherPoint : temp){
						if(!curCenter.equals(otherPoint)&&cluster.distanceTo(otherPoint)<threshold/2){
							inside.add(otherPoint);
						}
					}
					temp.removeAll(inside);
					for(Bullseye b : inside)
						cluster.add(b);
					double width;
					do{
						double minDist = Double.POSITIVE_INFINITY;
						Bullseye current = null;
						for(Bullseye other : temp){
							if(!curCenter.equals(other)&&cluster.distanceTo(other)<minDist){
								current=other;
								minDist = cluster.distanceTo(other);
							}
						}
						width=0;
						for(Bullseye other : cluster.getMembers())
							if(width<other.compareTo(current))
								width=other.compareTo(current);
						if(width<threshold){
							cluster.add(current);
							temp.remove(current);
						}
					}while(width<threshold);
					temp.addAll(cluster.getMembers());
					//System.out.println(cluster.size());
					if(cluster.size()>addCluster.size())
						addCluster=cluster;
				}
				if(addCluster.size()>0){
					m_clusters.add(addCluster);
					m_centers.add(addCluster.getCenter());
					myData.removeAll(addCluster.getMembers());
					//System.out.println("Cluster "+m_clusters.size());
				}
				else
					break;
			}	
		}
		
		public List<Bullseye> getCenters(){
			return m_centers;
		}
		
		public List<Cluster> getClusters(){
			return m_clusters;
		}
	}
	
	public class OltmansCluster extends Cluster implements Comparable<OltmansCluster>
	{
		public OltmansCluster(Bullseye center)
		{
			super(center);
		}
		
		@Override
		public double distanceTo(Bullseye other)
		{
			double maxDist = 0.0;
			
			for (Bullseye member: m_members)
				maxDist = Math.max(maxDist, member.compareTo(other));
			
			return maxDist;
		}
		
		@Override
		public int compareTo(OltmansCluster other)
		{
			return m_members.size() - other.m_members.size();
		}
	}
	
	public static class OltmansClusterer extends Clusterer
	{
		
		public static final int nPoints = 2000;
		public static final int nPerRound = 25;
		
		public OltmansClusterer(List<Bullseye> data, int numberClusters, double threshold){
			super();
			
			List<Bullseye> myData = null;
			Random rand = new Random(System.currentTimeMillis());
			
			// take a random sample of the data: 2000 points
			if (data.size() > nPoints)
			{
				myData = new ArrayList<Bullseye>(nPoints);
				for (int i = 0; i < nPoints; ++i)
				{
					myData.add(data.remove(rand.nextInt(data.size())));
				}
			}
			else
			{
				myData = new ArrayList<Bullseye>(data);
			}
			
			// up to numberClusters
			for (int i = 0; i < numberClusters && myData.size() > 0; ++i)
			{
				ArrayList<Integer> picked = new ArrayList<Integer>();
				ArrayList<OltmansCluster> clusters = new ArrayList<OltmansCluster>();
				
				while (picked.size() < Math.min(nPerRound, myData.size()))
				{
					int pick = rand.nextInt(myData.size());
					if (picked.contains(pick)) continue;
					picked.add(pick);
					
					Bullseye potentialCenter = myData.get(pick);
					ArrayList<Pair> distances = new ArrayList<Pair>();
					for (Bullseye b: myData)
					{
						if (b != potentialCenter)
							distances.add(new Pair(potentialCenter.compareTo(b), b));
					}
					Collections.sort(distances);
					OltmansCluster cluster = new OltmansCluster(potentialCenter);
					cluster.add(potentialCenter);
					
					for (int j = 0; j < distances.size() && distances.get(j).d < threshold; ++j)
					{
						if (cluster.distanceTo(distances.get(j).b) < threshold)
							cluster.add(distances.get(j).b);
					}
					
					clusters.add(cluster);
				}
				
				Collections.sort(clusters);
				
				OltmansCluster winner = clusters.get(0);
				
				m_clusters.add(winner);
				m_centers.add(winner.getCenter());
				
				myData.removeAll(winner.getMembers());
			}
		}
		
		public List<Bullseye> getCenters(){
			return m_centers;
		}
		
		public List<Cluster> getClusters(){
			return m_clusters;
		}
		
		class Pair implements Comparable<Pair>
		{
			double d;
			Bullseye b;
			
			Pair(double d, Bullseye b)
			{
				this.d = d;
				this.b = b;
			}
			
			@Override
			public int compareTo(Pair o) {
				return (int)(d - o.d);
			}
			
		}
	}
}















