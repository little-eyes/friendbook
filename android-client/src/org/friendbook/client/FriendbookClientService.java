package org.friendbook.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class FriendbookClientService extends Service {

	static private double [] acc = new double[3];
	static private double [] gyr = new double[3];
	static private double [] gps = new double[4];
	public final String datadir = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ "/friendbook";
	public final String datapath = datadir + "/raw";
	public final String documentpath = datadir + "/doc";
	static private FileOutputStream fos = null;
	static private CommonLocationManager mLocationMgr = null;
	static private CommonSensorManager mSensorMgr = null;
	static public boolean senseOngoing = false;
	static public int prog = 0;
	static private FileOutputStream documentFos = null;
	
	//cluster centroids for kmeans clustering
	private static final int featureNumber = 7;
	private static final int clusterNumber = 15;
	private double[] dcentroid0 = {0.748456,14.708913,8.068699,8.525011,9.09386,11.943502,10.862649};
	private double[] dcentroid1 = {0.007258,0.406512,0.540875,0.540096,0.507164,0.496289,0.51968};
	private double[] dcentroid2 = {-2.311677,0.058579,0.033388,0.043472,0.143591,0.699729,0.077384};
	private double[] dcentroid3 = {-0.081859,11.353061,7.580691,8.457156,7.756749,9.599028,8.20283};
	private double[] dcentroid4 = {0.700406,0.5765,0.75581,0.770671,0.808063,0.670314,0.70497};
	private double[] dcentroid5 = {-0.257834,0.938461,1.354903,1.223121,1.242737,1.205735,1.16795};
	private double[] dcentroid6 = {-0.89436,0.508973,0.610307,0.578942,0.485694,0.489455,0.560052};
	private double[] dcentroid7 = {-0.607266,1.017115,1.275919,1.202842,1.202362,1.145397,1.108045};
	private double[] dcentroid8 = {-0.432534,1.317496,1.644737,1.519063,1.539822,1.510225,1.526299};
	private double[] dcentroid9 = {1.045754,0.89793,0.947056,0.911609,1.016113,0.966098,0.937912};
	private double[] dcentroid10 = {0.440001,1.224705,1.111222,1.222184,1.174176,1.158568,1.041785};
	private double[] dcentroid11 = {-1.588784,0.002676,0.003449,0.003219,0.001206,0.001195,0.001211};
	private double[] dcentroid12 = {0.265084,0.77343,0.739888,0.835033,0.751285,0.78236,0.715366};
	private double[] dcentroid13 = {1.386746,0.474941,0.450967,0.486053,0.473852,0.482918,0.53293};
	private double[] dcentroid14 = {-0.73049,19.702222,20.384851,16.235003,10.615717,11.532199,17.708576};
	private Features centroid0 = new Features(dcentroid0);
	private Features centroid1 = new Features(dcentroid1);
	private Features centroid2 = new Features(dcentroid2);
	private Features centroid3 = new Features(dcentroid3);
	private Features centroid4 = new Features(dcentroid4);
	private Features centroid5 = new Features(dcentroid5);
	private Features centroid6 = new Features(dcentroid6);
	private Features centroid7 = new Features(dcentroid7);
	private Features centroid8 = new Features(dcentroid8);
	private Features centroid9 = new Features(dcentroid9);
	private Features centroid10 = new Features(dcentroid10);
	private Features centroid11 = new Features(dcentroid11);
	private Features centroid12 = new Features(dcentroid12);
	private Features centroid13 = new Features(dcentroid13);
	private Features centroid14 = new Features(dcentroid14);	
	
	private ArrayList<Features> clusterCentroids = new ArrayList<Features>();
	
	private void initFeatures() {
		clusterCentroids.add(centroid0); clusterCentroids.add(centroid1); clusterCentroids.add(centroid2);
		clusterCentroids.add(centroid3); clusterCentroids.add(centroid4); clusterCentroids.add(centroid5);
		clusterCentroids.add(centroid6); clusterCentroids.add(centroid7); clusterCentroids.add(centroid8);
		clusterCentroids.add(centroid9); clusterCentroids.add(centroid10); clusterCentroids.add(centroid11);
		clusterCentroids.add(centroid12); clusterCentroids.add(centroid13); clusterCentroids.add(centroid14);
	}

	
	// normalization parameters: (mean and standard deviation) for features extracted from raw data  
	private double[] norm_mean = {14.479200823612667, 0.0020299812913130077, -0.00394638490302047, -0.01707181403018664, 0.02186330976773779, 0.007398993257810107, -0.006261374164440122};
	private double[] norm_std  = {5.731461818000807, 0.7383419047929148, 0.7300122877314742, 0.7779778604783759, 0.47491686137469136, 0.4627368531414916, 0.26195897850032046};
	private double[] featureVector = new double[featureNumber];
	private int kmeansResult; //classification result after kmeans clustering
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		// first initialize the feature values.
		initFeatures();
		
		File f = new File(datadir);
		try {
			if (!f.exists()) f.mkdirs();
			f = new File(datapath+"-"+String.valueOf(new Date().getTime())+".dat");
			if (!f.exists()) f.createNewFile();
			fos = new FileOutputStream(f, true);
			f = new File(documentpath+".dat");
			if (!f.exists()) f.createNewFile();
			documentFos = new FileOutputStream(f, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSensorMgr = new CommonSensorManager();
		mLocationMgr = new CommonLocationManager();
		
		registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getExtras().getString("cmd").equals("on")) {
					mSensorMgr.startSensor();
					mLocationMgr.startFineLocationService();
					reportDocumentTimer = new Timer();
					reportDocumentTimer.scheduleAtFixedRate(new reportDocumentTask(), 600000, 600000);
					samplingTimer = new Timer();
					samplingTimer.scheduleAtFixedRate(new samplingTask(),sampleRate, sampleRate);
					
				}
				else if (intent.getExtras().getString("cmd").equals("off")) {
					mSensorMgr.stopSensor();
					mLocationMgr.stopFineLocationService();
					reportDocumentTimer.cancel();
				}
			}}, new IntentFilter("intent.friendbook.on_off"));
	}
	
	@Override
	public void onDestroy() {
		
	}
	
	
	private static Timer reportDocumentTimer = null;
	
	private class reportDocumentTask extends TimerTask {

		@Override
		public void run() {
			try {
				documentFos.close();
				boolean ok = postFile("http://com1384.eecs.utk.edu/friendbook-upload.php", 
						documentpath+".dat", readDeviceUUID()+"-"+String.valueOf(new Date().getTime()));
				if (ok) {
					File tf = new File(documentpath+".dat");
					tf.delete();
					tf.createNewFile();
				}
				documentFos = new FileOutputStream(new File(documentpath+".dat"), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//sampling rate and number for one classification
	private static final int sampleRate = 1000;
	private static final int maxSampleNumber = 60;
	private static Timer samplingTimer = null;
	private double[] timeSamples = new double[maxSampleNumber];
	private double[] accxSamples = new double[maxSampleNumber];
	private double[] accySamples = new double[maxSampleNumber];
	private double[] acczSamples = new double[maxSampleNumber];
	private double[] gyrxSamples = new double[maxSampleNumber];
	private double[] gyrySamples = new double[maxSampleNumber];
	private double[] gyrzSamples = new double[maxSampleNumber];
	private int sampleCount = 0;
	
	private class samplingTask extends TimerTask{
		@Override
		public void run(){
			
			if (sampleCount == maxSampleNumber){
				//extract features and normalization
				featureVector[0] = (new FeatureExtract(timeSamples)).getMean();
				featureVector[1] = (new FeatureExtract(accxSamples)).getStd();
				featureVector[2] = (new FeatureExtract(accySamples)).getStd();
				featureVector[3] = (new FeatureExtract(acczSamples)).getStd();
				featureVector[4] = (new FeatureExtract(gyrxSamples)).getStd();
				featureVector[5] = (new FeatureExtract(gyrySamples)).getStd();
				featureVector[6] = (new FeatureExtract(gyrzSamples)).getStd();
				//Kmeans classification
				kmeansResult = (new KmeansClassifier(featureVector, clusterCentroids)).getClusterId();
				System.out.println(kmeansResult);
				sampleCount = 0; // restart sampling raw data
				//write classification results into document
				try {	
					//if (documentFos == null)
					//	documentFos = new FileOutputStream(new File(documentpath+".dat") , true);
					documentFos.write((kmeansResult+";").getBytes()); 
					documentFos.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
			else if (sampleCount < maxSampleNumber) {//put raw data into arrays
				timeSamples[sampleCount] = ((new Date()).getHours()-norm_mean[0])/norm_std[0];;
				accxSamples[sampleCount] = (acc[0]-norm_mean[1])/norm_std[1];;
				accySamples[sampleCount] = (acc[1]-norm_mean[2])/norm_std[2];;
				acczSamples[sampleCount] = (acc[2]-norm_mean[3])/norm_std[3];
				gyrxSamples[sampleCount] = (gyr[0]-norm_mean[4])/norm_std[4];
				gyrySamples[sampleCount] = (gyr[1]-norm_mean[5])/norm_std[5];
				gyrzSamples[sampleCount] = (gyr[2]-norm_mean[6])/norm_std[6];
				sampleCount++;
				
			}
		}		
	}
	
	
    private class CommonSensorManager implements SensorEventListener {
    	
    	private SensorManager mSensorManager;  	
    	private Sensor Accelerometer;
    	private Sensor Gyroscopemeter;

    	public CommonSensorManager() {
    		this.mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    		this.Accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    		this.Gyroscopemeter = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	}
    	
    	// Start sensing
    	public void startSensor() {
    		File f = new File(datadir);
    		try {
    			if (!f.exists()) f.mkdirs();
    			f = new File(datapath+"-"+String.valueOf(new Date().getTime())+".dat");
    			if (!f.exists()) f.createNewFile();
    			fos = new FileOutputStream(f, true);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		senseOngoing = true;
    		mSensorManager.registerListener(this, this.Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    		mSensorManager.registerListener(this, this.Gyroscopemeter, SensorManager.SENSOR_DELAY_NORMAL);
    	}
    	
    	// Stop sensing
    	public void stopSensor() {
    		senseOngoing = false;
    		mSensorManager.unregisterListener(this, this.Accelerometer);
    		mSensorManager.unregisterListener(this, this.Gyroscopemeter);
    	}
    	
    	public void onSensorChanged(SensorEvent event) {
    		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) { 			
    			acc[0] = event.values[0];
    			acc[1] = event.values[1];
    			acc[2] = event.values[2];
    		}
    		else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
    			gyr[0] = event.values[0];
    			gyr[1] = event.values[1];
    			gyr[2] = event.values[2]; 	
    		}
    	}
    	
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    
    /*
	 * This part is to do location service.
	 * */
    
    private class CommonLocationManager {
    	private LocationManager mlocmgr = null;
		private LocationListener coarseListener = new LocationListener() {
	
			public void onLocationChanged(Location location) {
				gps[0] = location.getLatitude();
				gps[1] = location.getLongitude();
				gps[2] = location.getSpeed();
				gps[3] = location.getAccuracy();
			}
	
			public void onProviderDisabled(String provider) {}
	
			public void onProviderEnabled(String provider) {}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
		};
		
		private LocationListener fineListener = new LocationListener() {
	
			public void onLocationChanged(Location location) {
				gps[0] = location.getLatitude();
				gps[1] = location.getLongitude();
				gps[2] = location.getSpeed();
				gps[3] = location.getAccuracy();
			}
	
			public void onProviderDisabled(String provider) {}
	
			public void onProviderEnabled(String provider) {}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {}
		};
		
		private Criteria createCoarseCriteria() {
			Criteria c = new Criteria();
			c.setAccuracy(Criteria.ACCURACY_COARSE);
			c.setAltitudeRequired(false);
			//c.setAccuracy(100);
			//c.setBearingAccuracy(Criteria.ACCURACY_COARSE);
			//c.setBearingRequired(false);
			c.setSpeedRequired(false);
			c.setCostAllowed(true);
			c.setPowerRequirement(Criteria.POWER_LOW);
			return c;
		}
		
		private Criteria createFineCriteria() {
			Criteria c = new Criteria();
			c.setAccuracy(Criteria.ACCURACY_FINE);
			c.setAltitudeRequired(false);
			//c.setBearingRequired(false);
			//c.setBearingAccuracy(Criteria.ACCURACY_FINE);
			c.setSpeedRequired(false);
			c.setCostAllowed(true);
			c.setPowerRequirement(Criteria.POWER_HIGH);
			return c;
		}
		
		private boolean startCoarseLocationService() {
			
			mlocmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			LocationProvider locp = mlocmgr.getProvider(mlocmgr.getBestProvider(createCoarseCriteria(), true));
			if (locp != null) {
				mlocmgr.requestLocationUpdates(locp.getName(), 0, 0, coarseListener);
				return true;
			}
			else return false;
		}
		
		private void stopCoarseLocationService() {
			mlocmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			mlocmgr.removeUpdates(coarseListener);
		}
		
		private boolean startFineLocationService() {
			mlocmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			LocationProvider locp = mlocmgr.getProvider(mlocmgr.getBestProvider(createFineCriteria(), true));
			if (locp != null) {
				mlocmgr.requestLocationUpdates(locp.getName(), 0, 0, fineListener);
				return true;
			}
			else return false;
		}
		
		private void stopFineLocationService() {
			mlocmgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			mlocmgr.removeUpdates(fineListener);
		}
    }
    
    private String readDeviceUUID() {
		String deviceId = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
		return deviceId;
	}
    
    
 // don't forget to add http:// at the beginning of an url.
 	private boolean postFile(String urlstr, String localpath, String uploadname) {
     	HttpURLConnection conn = null;
         DataOutputStream os = null;
         String lineEnd = "\r\n";
         String twoHyphens = "--";
         String boundary =  "*****";
         int bytesRead, bytesAvailable, bufferSize, bytesUploaded = 0;
         byte[] buffer;
         int maxBufferSize = 2*1024*1024;
         
         try
         {
 	        FileInputStream fis = new FileInputStream(new File(localpath) );
 	
 	        URL url = new URL(urlstr);
 	        conn = (HttpURLConnection) url.openConnection();
 	        conn.setChunkedStreamingMode(maxBufferSize);
 	
 	        // POST settings.
 	        conn.setDoInput(true);
 	        conn.setDoOutput(true);
 	        conn.setUseCaches(false);
 	        conn.setRequestMethod("POST");
 	        conn.setRequestProperty("Connection", "Keep-Alive");
 	        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
 	        conn.setConnectTimeout(2000);
 	
 	        os = new DataOutputStream(conn.getOutputStream());
 	        os.writeBytes(twoHyphens + boundary + lineEnd);
 	        os.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + uploadname +"\"" + lineEnd);
 	        os.writeBytes(lineEnd);
 	
 	        bytesAvailable = fis.available();
 	        if (bytesAvailable == 0) return true;
 	        System.out.println("available: " + String.valueOf(bytesAvailable));
 	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
 	        buffer = new byte[bufferSize];

 	        prog = 0;
 	        bytesRead = fis.read(buffer, 0, bufferSize);
 	        bytesUploaded += bytesRead;
 	        while (bytesRead > 0)
 	        {
 	        	prog = bytesUploaded/bytesAvailable;
 		        os.write(buffer, 0, bufferSize);
 		        bytesAvailable = fis.available();
 		        bufferSize = Math.min(bytesAvailable, maxBufferSize);
 		        buffer = new byte[bufferSize];
 		        bytesRead = fis.read(buffer, 0, bufferSize);
 		        bytesUploaded += bytesRead;
 	        }
 	        System.out.println("uploaded: "+String.valueOf(bytesUploaded));
 	        os.writeBytes(lineEnd);
 	        os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 	
 	        // Responses from the server (code and message)
 	        conn.setConnectTimeout(2000); // allow 2 seconds timeout.
 	        int rcode = conn.getResponseCode();
 	        System.out.println(rcode);
 	        //if (rcode == 200) Toast.makeText(getApplicationContext(), "Success!!", Toast.LENGTH_LONG).show();
 	        //else Toast.makeText(getApplicationContext(), "Failed!!", Toast.LENGTH_LONG).show();
 	        //String rmsg = conn.getResponseMessage();
 	        fis.close();
 	        os.flush();
 	        os.close();
 	        return rcode == 200;
         }
         catch (Exception ex)
         {
         	ex.printStackTrace();
         	return false;
         }
 	}

}
