package com.nchu.motoguider;

import android.content.Context;
import android.location.*;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.util.Log;
import android.os.Bundle;


public class getGPS extends Activity implements LocationListener {
	private LocationManager locationManager;
	private LocationListener locationListener;
	LatLng result;
	
//Called when the activity is first created.
@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	UpdateLocation();
	}
	public void UpdateLocation(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationListener = new LocationListener() {
				public void onLocationChanged(Location newLocation) {
					Double longitude = newLocation.getLongitude();
					Double latitude = newLocation.getLatitude();
					result = new LatLng(longitude,latitude);
					Log.d("Location1", "X=" + longitude.intValue() + ", Y=" + latitude.intValue());
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
			public void onStatusChanged(String provider, int status,
			Bundle extras) {
			// TODO Auto-generated method stub
			
			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
@Override
	public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	Double longitude = location.getLongitude();
	Double latitude = location.getLatitude();
	result = new LatLng(longitude,latitude);
	Log.d("Location1", "X=" + longitude.toString() + ", Y=" + latitude.toString());
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

	public LatLng RetrunResult(){
		return result;
	}
}