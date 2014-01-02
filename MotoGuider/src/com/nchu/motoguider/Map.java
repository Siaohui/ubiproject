package com.nchu.motoguider;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.*;
import com.google.android.gms.maps.model.LatLng;

public class Map extends ListActivity
{
	
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter adapter;
	
	TextView showText;
	String[] instruction;
	LatLng statrtPoint[];
	LatLng endPoint[];
	MyBroadcastReceiver receiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		double dest_lat = 24.136781; //�x���������g�n��
		double dest_lng = 120.685008;		   
		
		//Intent getIntent = this.getIntent();
		//Bundle bundle = getIntent.getExtras();		
		//dest_lat = bundle.getDouble("lat");
		//dest_lng = bundle.getDouble("lng");
		Log.d("Map","��Geocoder���o�ت��a�g/�n�� = "+dest_lat+"/"+dest_lng);
		
		LatLng dest = new LatLng(dest_lat,dest_lng);
		LatLng origin = getOriginGPS();//new LatLng(24.1236371,120.6750405);//getOriginGPS();
		// origin�ثe�g���������j��,�]���դ��L�k���o�ۤv��GPS
		super.onCreate(savedInstanceState);
		   
		String url = getDirectionsUrl(origin, dest);
		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(url);
		
		/* ���U�s�� */
		IntentFilter filter = new IntentFilter("InfoService");
		receiver = new MyBroadcastReceiver();
		registerReceiver(receiver, filter);

	 }
	
	@Override
	protected void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	/**/
	private class MyBroadcastReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			int str;
			Bundle bundle = intent.getExtras();
			Log.d("Map","Recieve Brocast from InfoService"+bundle.getDouble("str"));
			adapter.notifyDataSetChanged();
		}
	}
	/**/
		 
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
	    	 Log.d("Map","nullpointer");
	         e.printStackTrace();
	     
	     }
	     if(lat == 0 & lon == 0)
	    	 Log.d("Map","�ثe�L�k�ϥ�GPS���o�z����m");
	     else
	    	 Log.d("Map","�z�ثe��m��GPS�g�n�� = " + lat + "/ lon = "+lon);
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
	 /* ���ohtml_instruction������Vcode */
	
	 /**�qURL�U��JSON��ƪ���k**/
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
		}
		finally 
		{
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
	
	 /** �ѪRJSON�榡 **/
	 private class ParserTask extends
	   AsyncTask<String, Integer, JSONData> 
	 {
		 // Parsing the data in non-ui thread
		  @Override
		  protected JSONData doInBackground(String... jsonData)
		 {
		   JSONObject jObject;
		   JSONData allData = null;
		   try 
		   {
			   jObject = new JSONObject(jsonData[0]);
			   allData = new JSONData(jObject);
			   return allData;
			} catch (JSONException e) 
			{
				e.printStackTrace();
			}
		   return allData;
		   
		  }
		  
		  @Override
		  protected void onPostExecute(JSONData result)
		  {
			  instruction = result.getAllRoadInstruction();
			  statrtPoint = result.getAllRoadStartPoint();
			  endPoint = result.getAllRoadEndPoint();
			  
			  //���ƥ[�JArrayList��
			  
			  for(int i=0; i<instruction.length; i++)
			  {
				  HashMap<String,String> item = new HashMap<String,String>();
				  item.put( "Instruction", instruction[i]+"��V:"+JSONData.getTurn(instruction[i]));
				  item.put( "startLatLng"," �_�I�g�n�סG" + statrtPoint[i].latitude + "/" + statrtPoint[i].longitude );
				  item.put( "endLatLng", "���I�g�n�סG"+ endPoint[i].latitude + "/" + endPoint[i].longitude);
				  item.put( "nowLatLng", "�ثe�g�n��: unKnown");
				  item.put( "nowSpeed", "�ثe�t��: unKnown");
				  item.put( "nowDistance", "�ثe�Ѿl�Z��: unKnown");
				  item.put( "remainTime", "�ثe�Ѿl�ɶ�: unKnown");
				  list.add(item);
			  }
			  
			  //�s�WSimpleAdapter
			  adapter =new SimpleAdapter( 
			  Map.this, 
			  list,
			  R.layout.mylistview1, 
			  new String[] { "Instruction","startLatLng","endLatLng","nowLatLng","nowSpeed","nowDistance","remainTime" },
			  new int[] { R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7 } );
			  
			  setListAdapter(adapter);
			  
			  //
			  
			  /*
			  setListAdapter
			  ( 
		                new ArrayAdapter<String>(
		                        Map.this, 
		                        android.R.layout.simple_list_item_1, 
		                        instruction)
		      );
		      */
			  
			  Intent intent = new Intent(Map.this, InfoService.class);
			  Bundle bundle = new Bundle();
			  bundle.putSerializable("allData",result);
			  intent.putExtras(bundle);
			  startService(intent);
		  }
	 }
}