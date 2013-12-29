package com.nchu.motoguider;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class InfoService extends Service implements LocationListener
{
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	double lastLat = 0.0;
	double lastLon = 0.0;
	/** Called when the activity is first created. */
	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.d("InfoService","start InfoService");
		UpdateLocation();
	}
	public void UpdateLocation()
	{
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
		locationListener = new LocationListener()
		{ 
		public void onLocationChanged(Location newLocation)
		{
			double remainTime = 0.0;
			int distance = -1;
			float speed = newLocation.getSpeed();
			Double nowLon = newLocation.getLongitude();
			Double nowLat = newLocation.getLatitude();
			Log.d("InfoService", "X=" + nowLon + ", Y=" + nowLat);
			
			//get json data
			//find distances
			distance = (int)getDistance(nowLat,nowLon,lastLat, lastLon);
													// endpoint lat/lng
			remainTime = distance/speed;
			Log.d("InfoService","¶ZÂ÷:"+distance+"®É¶¡"+remainTime);
			//final
			lastLat = nowLat;
			lastLon = nowLon;
		}
		public double getDistance(double lat1, double lon1, double lat2, double lon2)
		{
			float[] results=new float[1];
			Location.distanceBetween(lat1, lon1, lat2, lon2, results);
			return results[0];
		}
	@Override
	public void onProviderDisabled(String provider) 
	{
	// TODO Auto-generated method stub
	
	}
	
	@Override
	public void onProviderEnabled(String provider) 
	{
		// TODO Auto-generated method stub
	
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
	} 
	};
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000 , 0, locationListener);
	}
	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		float speed = location.getSpeed();
		Double longitude = location.getLongitude();
		Double latitude = location.getLatitude();
		Log.i("Location=", "X=" + longitude.intValue() + ", Y=" + latitude.intValue());
		
	}
	@Override
	public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
	}
	@Override
	public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}