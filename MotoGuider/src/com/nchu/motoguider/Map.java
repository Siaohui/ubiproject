package com.nchu.motoguider;

/*
 * 取得Direction的資料
 * 啟動InfoService,註冊BroadcastReceiver持續監看GPS和Compass位置
 * ->case by case透過Bluetooth obj傳送代碼給 Arduino
 * 需顯示路線資料+利用Log記錄歷史路徑
 * */

import java.io.IOException;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.location.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;

public class Map extends FragmentActivity
{
	static final LatLng NKUT = new LatLng(24.1236371, 120.6750405); // nchu
	GoogleMap map;
	ArrayList<LatLng> markerPoints;

	 @Override
	 protected void onCreate(Bundle savedInstanceState) 
	 {
		   double dest_lat = 25.0485301;
		   double dest_lng = 121.5171919;		   
		   LatLng dest = new LatLng(dest_lat,dest_lng);
		 
		   LatLng origin = getOriginGPS();
		   
		 
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_map);
	
		  // Initializing
		  markerPoints = new ArrayList<LatLng>();
		
		  // Getting reference to SupportMapFragment of the activity_main
		  SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
		    .findFragmentById(R.id.map);
		
		  // Getting Map for the SupportMapFragment
		  map = fm.getMap();
		  Marker nkut = map.addMarker(new MarkerOptions().position(NKUT).title("中興大學").snippet("資訊工程學系"));
		  // Move the camera instantly to NKUT with a zoom of 16.
		  map.moveCamera(CameraUpdateFactory.newLatLngZoom(NKUT, 16));
		
		  if (map != null)
		  {
		   // Enable MyLocation Button in the Map
		   map.setMyLocationEnabled(true);
		
		   /*
		   // Setting onclick event listener for the map
		   map.setOnMapClickListener(new OnMapClickListener() 
		   {
			   @Override
			   public void onMapClick(LatLng point)
			   {
				   // Already two locations
				   if (markerPoints.size() > 1)
				   {
					   markerPoints.clear();
					   map.clear();
				}
				// Adding new item to the ArrayList
				markerPoints.add(point);
		
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();
		
				// Setting the position of the marker
				options.position(point);
		
			     //起始及終點位置符號顏色
		 
			     if (markerPoints.size() == 1) {
			      options.icon(BitmapDescriptorFactory
			        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //起點符號顏色
			     } else if (markerPoints.size() == 2) {
			      options.icon(BitmapDescriptorFactory
			        .defaultMarker(BitmapDescriptorFactory.HUE_RED)); //終點符號顏色
			     }
		
			     // Add new marker to the Google Map Android API V2
			     map.addMarker(options);
		     
			     // Checks, whether start and end locations are captured
			     
			     if (markerPoints.size() >= 2) {
			    	 LatLng origin = markerPoints.get(0); //origin
			    	 LatLng dest = markerPoints.get(1); //destination
		
				      
			     }
			     
		
			   }
		   });*/
		   
		   	// Getting URL to the Google Directions API
		      String url = getDirectionsUrl(origin, dest);
		      

		      DownloadTask downloadTask = new DownloadTask();

		      // Start downloading json data from Google Directions
		      // API
		      downloadTask.execute(url);
		      
		      
		  }
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
	       lat = location.getLatitude ();
	       lon = location.getLongitude ();
	     }
	     catch (NullPointerException e)
	     {
	    	 Log.d("map","nullpointer");
	         e.printStackTrace();
	     
	     }
	     Log.d("map","lat = " + lat + " lon = "+lon);
	     
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
	
	 /**從URL下載JSON資料的方法**/
	 private String downloadUrl(String strUrl) throws IOException {
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
	
	  } catch (Exception e) {
	   Log.d("Exception while downloading url", e.toString());
	  } finally {
	   iStream.close();
	   urlConnection.disconnect();
	  }
	  return data;
	 }
	
	 // Fetches data from url passed
	 private class DownloadTask extends AsyncTask<String, Void, String> {
	
	  // Downloading data in non-ui thread
	  @Override
	  protected String doInBackground(String... url) {
	
	   // For storing data from web service
	   String data = "";
	
	   try {
	    // Fetching the data from web service
	    data = downloadUrl(url[0]);
	   } catch (Exception e) {
	    Log.d("Map", "Background Task"+e.toString());
	   }
	   return data;
	  }
	
	  // Executes in UI thread, after the execution of
	  // doInBackground()
	  @Override
	  protected void onPostExecute(String result) {
	   super.onPostExecute(result);
	
	   ParserTask parserTask = new ParserTask();
	
	   // Invokes the thread for parsing the JSON data
	   parserTask.execute(result);
	
	  }
	 }
	
	 /** 解析JSON格式 **/
	 private class ParserTask extends
	   AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
	
		  // Parsing the data in non-ui thread
		  @Override
		  protected List<List<HashMap<String, String>>> doInBackground(
		    String... jsonData) {
		
		   JSONObject jObject;
		   List<List<HashMap<String, String>>> routes = null;
		
		   try {
		    jObject = new JSONObject(jsonData[0]);
		    DirectionsJSONParser parser = new DirectionsJSONParser();
		
		    // Starts parsing data
		    routes = parser.parse(jObject);
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		   return routes;
		  }
	
		  /*  
		  // Executes in UI thread, after the parsing process
		  @Override
		  protected void onPostExecute(List<List<HashMap<String, String>>> result) {
		   ArrayList<LatLng> points = null;
		   PolylineOptions lineOptions = null;
		   MarkerOptions markerOptions = new MarkerOptions();
		
		   // Traversing through all the routes
		   for (int i = 0; i < result.size(); i++) {
		    points = new ArrayList<LatLng>();
		    lineOptions = new PolylineOptions();
		
		    // Fetching i-th route
		    List<HashMap<String, String>> path = result.get(i);
		
		    // Fetching all the points in i-th route
		    for (int j = 0; j < path.size(); j++) {
		     HashMap<String, String> point = path.get(j);
		
		     double lat = Double.parseDouble(point.get("lat"));
		     double lng = Double.parseDouble(point.get("lng"));
		     LatLng position = new LatLng(lat, lng);
		
		     points.add(position);
		    }
		
		    // Adding all the points in the route to LineOptions
		    lineOptions.addAll(points);
		    lineOptions.width(5);  //導航路徑寬度
		    lineOptions.color(Color.BLUE); //導航路徑顏色
		
		   }
		
		   // Drawing polyline in the Google Map for the i-th route
		   map.addPolyline(lineOptions);
		  }
		  
	  */
	 }
	 

	 
	 
}
