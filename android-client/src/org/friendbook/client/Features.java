package org.friendbook.client;

import java.io.Serializable;

public class Features {
	
	private double[] featureVector;
	
	public Features(double[] _featureVector){
		this.featureVector = _featureVector;
	}
	
	public double[] getfeatures(){
		return featureVector;
	}

}
