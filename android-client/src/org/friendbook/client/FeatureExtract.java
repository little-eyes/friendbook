package org.friendbook.client;

public class FeatureExtract {
	private double[] val;
	
	public FeatureExtract(double[] _val){
		this.val = _val;
	}

	/** 
	 * mean and standard deviation of samples
	 */
	public double getMean(){		
		double mean= 0.0;		
		int len = this.val.length;
		if (len>0){
			for (int i=0; i<len; i++){
				mean += this.val[i];
			}
			mean = mean/len;
		}else{
			mean = 0.0;
		}
		return mean;
	}
	
	public double getStd(){
		double std = 0.0;
		double sum = 0.0;
		double mean = 0.0;
		int len = this.val.length;
		if (len<=1){
			std = 0.0;
		}else{
			mean = this.getMean();
			for (int i=0; i<len; i++){
				sum += (this.val[i]-mean)*(this.val[i]-mean);
			}
			std = Math.sqrt(sum/(len-1));
		}		
		return std;
	}
	
	
}
