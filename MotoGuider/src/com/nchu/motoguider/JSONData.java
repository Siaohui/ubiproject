package com.nchu.motoguider;
/*
 * Direction JSON 取回來的資料儲存為此型態
 * */
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class JSONData implements Serializable
{

	private static final long serialVersionUID = 1L;
	transient LatLng startPoint[];
	transient LatLng endPoint[];
	String htmlInstruction[];
	
	transient JSONArray jRoutes = null;
	transient JSONArray jLegs = null;
	transient JSONArray jSteps = null;
    
    String end_location_lat, end_location_lng ;
    String start_location_lat, start_location_lng;
    String html_instructions = "";
    
	public JSONData(JSONObject jObject)
	{
	    try 
	    {
	    	jRoutes = jObject.getJSONArray("routes");
	    	for(int i = 0; i<jRoutes.length(); i++)
	    	{
	    		jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
		  	    /** Traversing all legs */
	
		  	    for(int j=0;j<jLegs.length();j++)
		  	    {
		  	    	 jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
		  	    	 int len = jSteps.length();
	  	    		 startPoint = new LatLng[len];
	  	    		 endPoint = new LatLng[len];
	  	    		 htmlInstruction = new String[len];
		  	    	/** Traversing all steps */
	
			  	     for(int k = 0; k < jSteps.length(); k++)
			  	     {
			  	    	 end_location_lat = (((JSONObject)((JSONObject)jSteps.get(k)).get("end_location"))).get("lat").toString() ;   	 
			  	    	 end_location_lng = (((JSONObject)((JSONObject)jSteps.get(k)).get("end_location"))).get("lng").toString() ;
			  	    	 endPoint[k] = new LatLng(Double.valueOf(end_location_lat), Double.valueOf(end_location_lng));
			  	    	 
			  	    	 start_location_lat = (((JSONObject)((JSONObject)jSteps.get(k)).get("start_location"))).get("lat").toString() ;
			  	    	 start_location_lng =(((JSONObject)((JSONObject)jSteps.get(k)).get("start_location"))).get("lng").toString() ;
			  	    	 startPoint[k] = new LatLng(Double.valueOf(end_location_lat), Double.valueOf(end_location_lng));
			  	    	 
			  	    	 html_instructions = ((((JSONObject)jSteps.get(k)).getString("html_instructions")).toString());
			  	    	 htmlInstruction[k] = html_instructions.replaceAll("\\<.*?>","");
			  	     }
		  	    }
	    	}
	    } catch (JSONException e)
	    { 
	    	e.printStackTrace();
	    }
	    catch (Exception e){}
   }
	
	/* get JSON data */

	public LatLng[] getAllRoadStartPoint()
	{
		return startPoint;
	}
	
	public LatLng[] getAllRoadEndPoint()
	{
		return endPoint;
	}
	public String[] getAllRoadInstruction()
	{
		return htmlInstruction;
	}
	
	/* parse html's instruction direction*/
	
	public int getTurn(String s)
	 {
		 int turnResult = -1;
		 
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