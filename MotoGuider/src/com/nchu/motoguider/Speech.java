package com.nchu.motoguider;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.*;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class Speech extends Activity
{
	ImageButton speechBtn;
	
	protected static final int RESULT_SPEECH = 1;
	private String dest_tmp;
	LatLng destGeo;
	double lat;
	double lng;
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.activity_speech);
	     
	     speechBtn = (ImageButton) findViewById(R.id.startSpeechBtn);
	     speechBtn.setOnClickListener(startSpeech);
    }
	
	private OnClickListener startSpeech = new OnClickListener()
	{
		public void onClick(View v)
		{
			String destination = "й╛з╡й]ел";
					
			Intent intent_2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent_2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

	        try 
	        {
	        	startActivityForResult(intent_2, RESULT_SPEECH);
	        }
	        catch (ActivityNotFoundException a)
	        {
	        	Toast t = Toast.makeText(getApplicationContext(),
	        			"Opps! Your device doesn't support Speech to Text",
	        			Toast.LENGTH_SHORT);
	        	t.show();
	        }
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode)
        {
        case RESULT_SPEECH: 
        {
            if(resultCode == RESULT_OK && null != data)
            {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                dest_tmp = text.get(0);
                Log.d("Speech",dest_tmp);
                new LoadingDataAsyncTask().execute(dest_tmp);
                
                Intent intent = new Intent(Speech.this, Map.class);
           
    			Bundle bundle = new Bundle();
    			
    			bundle.putDouble("lat", lat);
    			bundle.putDouble("lng", lng);
    		
    			intent.putExtras(bundle);
    			startActivity(intent);

                break;
            }
        }
        }
    }
	
	class LoadingDataAsyncTask extends AsyncTask<String, Integer, String>
	{
		@Override
		protected String doInBackground(String... param) 
		{
			String result ="";
			result = getData(param[0]);
			Log.d("speech","download geocoder");
			destGeo = getLatLng(result);
			return result;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			destGeo = getLatLng(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

	}
	
	public String getData(String dest)
	{
		String _url = "http://maps.googleapis.com/maps/api/geocode/json?address="+dest+"&sensor=false";
		String result ="";
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(_url);
		
		HttpResponse response;
		try
		{
			response = client.execute(get);
			HttpEntity resEntity = response.getEntity();
			result = EntityUtils.toString(resEntity);
			//Log.d("speech",result);
			return result;
			
		} catch (ClientProtocolException e) 
		{
			Log.d("speech","ClientException");
			e.printStackTrace();
		} catch (IOException e) 
		{
			Log.d("speech","Please turn on internet");
			e.printStackTrace();
		}
		return result;
	}
	
	public LatLng getLatLng(String result)
	{
		LatLng finalGeo = new LatLng(0,0);
		JSONObject jobj;
		JSONArray obj;
		String allLocation;
		String location ="";
		try 
		{
			// ....
			jobj= new JSONObject(result);
			obj = jobj.getJSONArray("results");
			String obj2 = obj.get(0).toString();
			JSONObject temp = new JSONObject(obj2);
			
			allLocation = temp.get("geometry").toString();
			JSONObject obj3 = new JSONObject(allLocation);
			
			location = obj3.get("location").toString();
			JSONObject obj4 = new JSONObject(location);
			
			lat = obj4.getDouble("lat");
			lng = obj4.getDouble("lng");
			
			Log.d("speech","location="+lat+"/"+lng);
			//
			finalGeo = new LatLng(lat,lng);
			return finalGeo;
		} catch (JSONException e) 
		{
			Log.d("speech","JsonException");
			e.printStackTrace();
		}
		return finalGeo;
	}
		
}