package com.nchu.motoguider;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

public class InfoService extends Service
	implements LocationListener, SensorEventListener
{
	/*
	 * Service
	 * */

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
	
	/*
	 * Location GPS
	 * */

	@Override
	public void onLocationChanged(Location location) 
	{
		Intent intent = new Intent("MyFilter");
		intent.putExtra("Lat", location.getLatitude());
		intent.putExtra("Long", location.getLongitude());
		intent.putExtra("Accuracy", location.getAccuracy());
		intent.putExtra("Bearing", location.getBearing());
		intent.putExtra("Speed", location.getSpeed());
		intent.putExtra("Time", location.getTime());
		sendBroadcast(intent);
		
	}

	@Override
	public void onProviderDisabled(String provider) 
	{
		// 當GPS或網路定位關閉時
		
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		// 當GPS或網路定位開啟
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * Compass(Sensor) Listener
	 * */

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		// TODO Auto-generated method stub
		
	}

	
	

	
}
