package org.friendbook.client;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class FriendbookClientActivity extends Activity {
    /** Called when the activity is first created. */
	
	private static ToggleButton sensorBtn = null;
	private static ToggleButton queryBtn = null;
	private ProgressDialog queryDialog = null;
	private MyArrayAdapter adapter = null;
	private ArrayList <Candidates> candidates = null;
	private String ratingFrom = null;
	private String ratingTo = null;
	private double ratingScore = 0;
	//private Handler mhandler = null;
	private ListView resultList = null;
	
	private FileOutputStream fos = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        startService(new Intent(FriendbookClientActivity.this, FriendbookClientService.class));
        
        sensorBtn = (ToggleButton) findViewById(R.id.toggleButton1);
        queryBtn = (ToggleButton) findViewById(R.id.toggleButton2);
        resultList = (ListView) findViewById(R.id.listView1);
        queryBtn.setText("Query");
        
        // --------- Evaluation -----------
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/friendbook/responding-time.txt");
        try {
        	f.createNewFile();
        	fos = new FileOutputStream(f, true);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        // --------- Evaluation -----------
        
        sensorBtn.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View v) {
				if (sensorBtn.isChecked()) {
					// start
					Intent pi = new Intent("intent.friendbook.on_off");
					pi.putExtra("cmd", "on");
					sendBroadcast(pi);
				}
				else {
					// stop
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
					builder.setMessage("Ready to stop sensing?");
					builder.setCancelable(false);
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   Intent pi = new Intent("intent.friendbook.on_off");
					        	   pi.putExtra("cmd", "off");
					        	   sendBroadcast(pi);
					           }
					       });
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					                dialog.cancel();
					           }
					       });
					AlertDialog alert = builder.create();
					alert.show();
				}	
			}});
        queryBtn.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View arg0) {
				queryDialog = ProgressDialog.show(FriendbookClientActivity.this, "", 
		                "Querying. Please wait...", true);
				queryBtn.setText("Working...");
				// --------- Evaluation -----------
				for (int i = 0; i < 100; ++i) {
					long st = new Date().getTime();
					queryRecommendation("http://com1384.eecs.utk.edu:808/friendbook-query.html", 3);
					st = new Date().getTime() - st;
					try {
						fos.write((String.valueOf(st) + "\n").getBytes());
						fos.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// --------- Evaluation -----------
				resultList.setAdapter(new MyArrayAdapter(getApplicationContext(), candidates));
				for (Candidates c: candidates) {
					System.out.println(c.Name);
				}
				queryDialog.dismiss();
				queryBtn.setText("Query");
				
				
			}});
        
        resultList.setOnItemClickListener(new OnItemClickListener() {

			//@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final String[] items = {"Connect", "Rating"};

				final int pos = arg2;
				AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
				builder.setTitle("Choose an Option");
				builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	dialog.dismiss();
				    	if (item == 0) buildConnection(pos);
				    	else showRateFriends(pos);
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				
			}});
        
        candidates = new ArrayList<Candidates>();
        candidates.add(new Candidates("N/A", "Use query key."));
        adapter = new MyArrayAdapter(getApplicationContext(), candidates);
        resultList.setAdapter(adapter);
    }
    
    
    public class MyArrayAdapter extends ArrayAdapter<Candidates> {
    	private final Context context;
    	private final ArrayList <Candidates> candidates;
    	
    	public MyArrayAdapter(Context context, ArrayList <Candidates> candidates) {
    		super(context, R.layout.rowlayout, candidates);
    		this.candidates = candidates;
    		this.context = context;
    	}
    	
    	@Override
    	public View getView(int pos, View convertView, ViewGroup parent) {
    		LayoutInflater inflater = (LayoutInflater) 
    				context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
    		TextView nameView = (TextView) rowView.findViewById(R.id.textView1);
    		TextView scoreView = (TextView) rowView.findViewById(R.id.textView2);
    		nameView.setText(candidates.get(pos).Name);
    		scoreView.setText(candidates.get(pos).Score);
    		
    		return rowView;
    	}
    }
    
    // The candidate class.
    public class Candidates {
    	public String Name;
    	public String Score;
    	public Candidates(String name, String score){
    		this.Name = name;
    		this.Score = score;
    	}
    }
    
    private void queryRecommendation(String url, int k) {
    	try {
	    	HttpClient client = new DefaultHttpClient();
	        HttpGet request = new HttpGet();
	        request.setURI(new URI(url+"?"+"hwid="+readDeviceUUID()/*"5fd01dd3c4253311"*/+"&"+"topk="+String.valueOf(k)));
	        request.addHeader("Accept", "text/xml");
	        request.addHeader("Content-Type", "text/xml");
	        HttpResponse response = client.execute(request);
	        
	        SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            
            XMLReader xr = sp.getXMLReader();
            
            RecommendationXmlHandler myXmlHandler = new RecommendationXmlHandler();
            xr.setContentHandler(myXmlHandler);
            
            xr.parse(new InputSource(response.getEntity().getContent()));
            
            candidates = myXmlHandler.getParsedData();       
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    

    private void buildConnection(int pos) {
    	String from = readDeviceUUID();
    	String to = candidates.get(pos).Name;
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://com1384.eecs.utk.edu/connection.php");
    	List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	nameValuePairs.add(new BasicNameValuePair("from", from));
    	nameValuePairs.add(new BasicNameValuePair("to", to));
    	try {
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
    		HttpResponse response = httpclient.execute(httppost);
    		if (response.getStatusLine().getStatusCode() == 200)
    			Toast.makeText(getApplicationContext(), "Build Connection Success!!!", Toast.LENGTH_LONG).show();
    		else
    			Toast.makeText(getApplicationContext(), "Build Connection failed, try it later...", Toast.LENGTH_LONG).show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private void showRateFriends(int pos) {
    	final Dialog dialog = new Dialog(resultList.getContext()); 
    	ratingFrom = readDeviceUUID();
    	ratingTo = candidates.get(pos).Name;
    	dialog.setContentView(R.layout.ratelayout);
    	dialog.setTitle("Rate our recommendations");
    	
    	RatingBar rb = (RatingBar) dialog.findViewById(R.id.ratingBar1);
    	Button rateBtn = (Button) dialog.findViewById(R.id.button1);
    	Button cancelBtn = (Button) dialog.findViewById(R.id.button2);
    	
    	rb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			//@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				ratingScore = arg1;				
			}});
    	rateBtn.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View arg0) {
				runRating();
				dialog.dismiss();
			}});
    	cancelBtn.setOnClickListener(new OnClickListener() {

			//@Override
			public void onClick(View v) {
				dialog.dismiss();
			}});
    	dialog.show();

    }
    
    private void runRating() {
    	String from = ratingFrom;
    	String to = ratingTo;
    	String score = String.valueOf(ratingScore);
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httppost = new HttpPost("http://com1384.eecs.utk.edu/rating.php");
    	List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
    	nameValuePairs.add(new BasicNameValuePair("from", from));
    	nameValuePairs.add(new BasicNameValuePair("to", to));
    	nameValuePairs.add(new BasicNameValuePair("score", score));
    	try {
    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	try {
    		HttpResponse response = httpclient.execute(httppost);
    		if (response.getStatusLine().getStatusCode() == 200)
    			Toast.makeText(getApplicationContext(), "Rating Success!!!", Toast.LENGTH_LONG).show();
    		else
    			Toast.makeText(getApplicationContext(), "Rating failed, try it later...", Toast.LENGTH_LONG).show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private String readDeviceUUID() {
		String deviceId = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
		return deviceId;
	}
    
    private class RecommendationXmlHandler extends DefaultHandler {
    	private ArrayList <Candidates> candidates = new ArrayList <Candidates>();
    	private Candidates friend;
    	private StringBuilder builder;
    	
    	public ArrayList <Candidates> getParsedData() {
    		return this.candidates;
    	}
    	
    	@Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }
    	
    	@Override
    	public void startDocument() throws SAXException{
    		this.candidates = new ArrayList <Candidates> ();
    		this.builder = new StringBuilder();
    	}
    	
    	@Override
    	public void endDocument() throws SAXException{
    	
    	}
    	
    	 @Override
         public void startElement(String namespaceURI, String localName,
                         String qName, Attributes atts) throws SAXException {
    		 if (localName.equals("friend")) {
    			 this.friend = new Candidates("", "");
    		 }
    		 
    	 }
    	 
    	 @Override
         public void endElement(String namespaceURI, String localName, String qName)
                         throws SAXException {
    		 if (localName.equals("friend"))
    			 this.candidates.add(friend);
    		 else if (localName.equals("hwid")) {
    			 this.friend.Name = builder.toString().trim();
    		 }
    		 else if (localName.equals("score")) {
    			 this.friend.Score = builder.toString().trim();
    		 }
    		 builder.setLength(0);
    	 }
    }
 
}