package com.nchu.motoguider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;



/*
 * Process Google map direction data (JSON)
 * 
 */

public class Direction
{
	public void getDirection() throws IOException
	{
		/**/
		String url = getDirectionsUrl("中星大學","忠孝校市");
		DownloadTask downloadTask = new DownloadTask();

	     // Start downloading json data from Google Directions
	     // API
	    downloadTask.execute(url);
		
		// JSON object
	}
	// set URL
	private String getDirectionsUrl(String origin, String dest) 
	{
		  // Origin of route
		  String str_origin = "origin=" + origin;

		  // Destination of route
		  String str_dest = "destination=" + dest;

		  // Sensor enabled
		  String sensor = "sensor=false";

		  // Building the parameters to the web service
		  String parameters = str_origin + "&" + str_dest + "&" + sensor;

		  // Output format
		  String output = "json";

		  // Building the url to the web service
		  String url = "https://maps.googleapis.com/maps/api/directions/"
		    + output + "?" + parameters;

		  return url;
	}
	//download json data
	
	private String downloadUrl(String strUrl) throws IOException
	{
		  String data = "";
		  InputStream iStream = null;
		  HttpURLConnection urlConnection = null;
		  try {
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
		   while ((line = br.readLine()) != null) {
		    sb.append(line);
		   }

		   data = sb.toString();

		   br.close();

		  } catch (Exception e) 
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
	    // Fetching the data from web service
	    data = downloadUrl(url[0]);
	   } catch (Exception e) 
	   {
	    Log.d("Background Task", e.toString());
	   }
	   return data;
	  }

	  // Executes in UI thread, after the execution of
	  // doInBackground()
	 	 }

	
	
}
