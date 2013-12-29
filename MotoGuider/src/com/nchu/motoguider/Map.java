package com.nchu.motoguider;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.*;
import com.google.android.gms.maps.model.LatLng;

public class Map extends ListActivity 
{
	ListView showInstructionView;
	TextView showText;
	String[] instruction;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		String[] values = new String[] { "Android", "iPhone", "WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2" };
		double dest_lat = 24.136781;
		double dest_lng = 120.685008;		   
		LatLng dest = new LatLng(dest_lat,dest_lng);
		LatLng origin = getOriginGPS();
		/*
		Intent getIntent = this.getIntent();
		Bundle bundle = getIntent.getExtras();;
		dest_lat = bundle.getDouble("lat");
		dest_lng = bundle.getDouble("lng");
		*/
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_map);
		// list view
		setListAdapter( 
                new ArrayAdapter<String>(
                        this, 
                        android.R.layout.simple_list_item_1, 
                        values) 
        );
		//
		Intent intent = new Intent(Map.this, InfoService.class);
		startService(intent);
		   
		String url = getDirectionsUrl(origin, dest);
		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(url);
	 }
		 
	 public LatLng getOriginGPS()
	 {
		 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	     Criteria criteria = new Criteria();
	     String bestProvider = locationManager.getBestProvider(criteria, false);
	     Location location = locationManager.getLastKnownLocation(bestProvider);
	     Double lat =0.0,lon=0.0;
	     try 
	     {
	    	 lat = location.getLatitude();
		     lon = location.getLongitude();
	     }
	     catch (NullPointerException e)
	     {
	    	 Log.d("map","nullpointer");
	         e.printStackTrace();
	     
	     }
	     Log.d("map","Origin:lat = " + lat + " lon = "+lon);
	     return new LatLng(lat,lon);
	 }

	 private String getDirectionsUrl(LatLng origin, LatLng dest) 
	 {
		 // Origin of route
		 String str_origin = "origin=" + origin.latitude + ","
	    + origin.longitude;
	
		 // Destination of route
		 String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
	
		 // Sensor enabled
		 String sensor = "sensor=false";
	
		 // Building the parameters to the web service
		 String parameters = str_origin + "&" + str_dest + "&" + sensor +"&avoid=highways";
	
		 // Output format
		 String output = "json";
		 // Building the url to the web service
		 String url = "https://maps.googleapis.com/maps/api/directions/"
		    + output + "?" + parameters;
	
		 return url;
	 }
	 /* 取得html_instruction中的方向code */
	 public int getTurn(String s)
	 {
		 int turnResult=-1;
		 
		 if(s.contains("north"))
			 turnResult=0;
		 else if(s.contains("northeast"))
			 turnResult=1;
		 else if(s.contains("east"))
			 turnResult=2;
		 else if(s.contains("southeast"))
			 turnResult=3;
		 else if(s.contains("south"))
			 turnResult=4;
		 else if(s.contains("southwest"))
			 turnResult=5;
		 else if(s.contains("west"))
			 turnResult=6;
		 else if(s.contains("northwest"))
			 turnResult=7;
		 if(s.contains("Turn")&s.contains("left"))
			 turnResult=8;
		 if(s.contains("Turn")&s.contains("right"))
			 turnResult=9;
		 if(s.contains("Slight")&s.contains("left"))
			 turnResult=10;
		 if(s.contains("Slight")&s.contains("right"))
			 turnResult=11;
		 if(s.contains("Sharp")&s.contains("left"))
			 turnResult=12;
		 if(s.contains("Sharp")&s.contains("right"))
			 turnResult=13;
		 if(s.contains("U-turn"))
			 turnResult=14;
		 return turnResult; 
	}
	
	 /**從URL下載JSON資料的方法**/
	 private String downloadUrl(String strUrl) throws IOException 
	 {
		 String data = "";
		  InputStream iStream = null;
		  HttpURLConnection urlConnection = null;
		  try 
		  {
		   URL url = new URL(strUrl);
		
		   // Creating an http connection to communicate with url
		   urlConnection = (HttpURLConnection) url.openConnection();
		
		   // Connecting to url
		   urlConnection.connect();
		
		   // Reading data from url
		   iStream = urlConnection.getInputStream();
		
		   BufferedReader br = new BufferedReader(new InputStreamReader(
		     iStream));
		
		   StringBuffer sb = new StringBuffer();
		
		   String line = "";
		   while ((line = br.readLine()) != null) 
		   {
			   sb.append(line);
		   }
		   data = sb.toString();
		
		   br.close();
	}
	catch (Exception e) 
	{
	   Log.d("Exception while downloading url", e.toString());
	  } finally {
	   iStream.close();
	   urlConnection.disconnect();
	  }
	  return data;
	 }
	
	 // Fetches data from url passed
	 private class DownloadTask extends AsyncTask<String, Void, String>
	 {
		 // Downloading data in non-ui thread
		 @Override
		  protected String doInBackground(String... url)
		  {
			 // For storing data from web service
			   String data = "";
			   try 
			   {
				   data = downloadUrl(url[0]);
			   }
			   catch (Exception e)
			   {
				   Log.d("Map", "Background Task"+e.toString());
			   }
			   return data;
	  }
	
	  // Executes in UI thread, after the execution of
	  // doInBackground()
	  @Override
	  protected void onPostExecute(String result)
	  {
	   super.onPostExecute(result);
	   ParserTask parserTask = new ParserTask();
	   // Invokes the thread for parsing the JSON data
	   parserTask.execute(result);
	  }
	 }
	
	 /** 解析JSON格式 **/
	 private class ParserTask extends
	   AsyncTask<String, Integer, List<List<HashMap<String, String>>>> 
	 {
		 // Parsing the data in non-ui thread
		  @Override
		  protected List<List<HashMap<String, String>>> doInBackground(
		    String... jsonData)
		 {
		   JSONObject jObject;
		   List<List<HashMap<String, String>>> routes = null;
		   try 
		   {
			    jObject = new JSONObject(jsonData[0]);
			    DirectionsJSONParser parser = new DirectionsJSONParser();
			    // Starts parsing data
			    routes = parser.parse(jObject);
			    //
			    JSONArray jRoutes = null;
			    JSONArray jLegs = null;
			    JSONArray jSteps = null;
			    String end_location_lat ;
			    String end_location_lng ;
			    String start_location_lat;
			    String start_location_lng;
			    String html_instructions = "";
			    
			    try 
			    {
			  	  jRoutes = jObject.getJSONArray("routes");
			  	  /** Traversing all routes */
			     
			  	   for(int i=0;i<jRoutes.length();i++)
			  	   {   
			  	    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
			  	    
			  	    /** Traversing all legs */
			  	    for(int j=0;j<jLegs.length();j++)
			  	    {
			  	     jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
			  	     
			  	     /** Traversing all steps */
			  	     for(int k=0;k<jSteps.length();k++)
			  	     {
			  	    	 end_location_lat = (((JSONObject)((JSONObject)jSteps.get(k)).get("end_location"))).get("lat").toString() ;   	 
			  	    	 end_location_lng = (((JSONObject)((JSONObject)jSteps.get(k)).get("end_location"))).get("lng").toString() ;
			  	    	 start_location_lat = (((JSONObject)((JSONObject)jSteps.get(k)).get("start_location"))).get("lat").toString() ;
			  	    	 start_location_lng =(((JSONObject)((JSONObject)jSteps.get(k)).get("start_location"))).get("lng").toString() ;
			  	    	 html_instructions = ((((JSONObject)jSteps.get(k)).getString("html_instructions")).toString());
			  	    	 
			  	    	 Log.d("distance",end_location_lat);
			  	    	 Log.d("distance",end_location_lng);
			  	    	 Log.d("distance",start_location_lat);
			  	    	 Log.d("distance",start_location_lng);
			  	    	 Log.d("distance",html_instructions);
			  	    	 
			  	    	 Log.d("distance",""+getTurn(html_instructions));
			  	     }
			  	    }
			     }
			    } catch (JSONException e)
			    { 
			    	e.printStackTrace();
			    }
			    catch (Exception e){}
		   }
		   catch (Exception e)
		   {
			   e.printStackTrace();
		   }
		   return routes;
		  }
	 }
}