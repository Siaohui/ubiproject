package com.nchu.motoguider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser
{
 
 /** 接收一個JSONObject並返回一個列表的列表，包含經緯度 */
 public List<List<HashMap<String,String>>> parse(JSONObject jObject)
 {
  
  List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
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
	     /*
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
	     */
	    }
   }
   
  } catch (JSONException e)
  { 
   e.printStackTrace();
  }
  catch (Exception e){}
  return routes;
 }
 
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
}