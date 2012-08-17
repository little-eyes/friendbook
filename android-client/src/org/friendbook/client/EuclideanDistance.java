package org.friendbook.client;

/**
 * calculate the euclidean distance between two vectors
 */
public class EuclideanDistance {

	private double[] vec1;
	private double[] vec2;
	
	public EuclideanDistance(double[] _vec1, double[] _vec2){
		this.vec1 = _vec1;
		this.vec2 = _vec2;
	}
	
	public double getEuclideanDistance(){
		double dist = 0.0;
		for (int i=0; i<vec1.length; i++){
			dist += (vec1[i]-vec2[2])*(vec1[i]-vec2[2]);				
		}
		return dist;
	}
}
