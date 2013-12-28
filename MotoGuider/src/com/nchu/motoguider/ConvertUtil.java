package com.nchu.motoguider;

import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
 

public class ConvertUtil
{ 
	// get the Latitude and longitude by address
	public static double[] getLocationInfo(String address) {
		// set HttpClient client
		HttpClient client = new DefaultHttpClient();
		// require Http to get the values
		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
		+ address + "&sensor=false&language=zh-TW");
		
		StringBuilder sb = new StringBuilder();
		try
		{
			// get the reply from http
			HttpResponse response = client.execute(httpGet);
			// get the entity of reply from http
			HttpEntity entity = response.getEntity();
			// get the contents from the entity of reply

			InputStream stream = entity.getContent();

			// count the length of stream of contents from reply
			
			int count;
			
			// the loop which receiving the reply from http get

			while ((count = stream.read()) != -1) 
			{
			
			sb.append((char) count);
			
			}

			// let jsonObject be receiving reply

			JSONObject jsonObject = new JSONObject(sb.toString());
			
			// take the Latitude and Longitude of address by JSONObject
			
			JSONObject location = jsonObject.getJSONArray("results")
			
			.getJSONObject(0).getJSONObject("geometry")
			
			.getJSONObject("location");
			
			// get the Latitude
			
			double longitude = location.getDouble("lng");
			
			// get the Longitude
			
			double latitude = location.getDouble("lat");
			
			// return Latitude and Longitude
			
			return new double[] { longitude, latitude };
			
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;

}

 

// get the address by Latitude and longitude

public static String getAddress(double longitude, double latitude) {

// set HttpClient client«Î«Û

HttpClient client = new DefaultHttpClient();

// require Http to get the values

HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/"

+ "geocode/json?latlng=" + latitude + "," + longitude

+ "&sensor=false&region=cn");

StringBuilder sb = new StringBuilder();

try {

// get the reply from http

HttpResponse response = client.execute(httpGet);

// get the entity of reply from http

HttpEntity entity = response.getEntity();

// get the contents from the entity of reply

InputStream stream = entity.getContent();

// count the length of stream of contents from reply

int count;

// the loop which receiving the reply from http get

while ((count = stream.read()) != -1) {

sb.append((char) count);

}

// let jsonObject be receiving reply

JSONObject jsonObj = new JSONObject(sb.toString());

// return address

return jsonObj.getJSONArray("results").getJSONObject(0)

.getString("formatted_address");

} catch (Exception e) {

e.printStackTrace();

}

return null;

}

}