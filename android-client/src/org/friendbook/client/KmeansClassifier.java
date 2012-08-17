package org.friendbook.client;

import java.util.ArrayList;

public class KmeansClassifier {
	private double[] featureVector;
	private ArrayList<Features> clusterCentroids;
	
	public KmeansClassifier(double[] _featureVector, ArrayList<Features> _clusterCentroids){
		this.featureVector = _featureVector;
		this.clusterCentroids = _clusterCentroids;		
	}
	
	public int getClusterId(){
		double dist = 10000000.00;
		int id = 10000;
		double tempdist = 0.0;
		int clusterNumber = clusterCentroids.size();
		for (int i=0; i<clusterNumber; i++){
			tempdist = (new EuclideanDistance(featureVector, clusterCentroids.get(i).getfeatures())).getEuclideanDistance();
			if (tempdist < dist){
				dist = tempdist;
				id = i;
			}			
		}
		return id;
	}	

}
