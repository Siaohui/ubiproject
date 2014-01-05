package com.nchu.motoguider;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
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
	JSONData jsonData = null;
	
	double startPointLat[], startPointLng[];
	double endPointLat[], endPointLng[];
	
	String htmlInstruction[];

	Intent intentToMap = new Intent("InfoService");
	Bundle bundle = new Bundle();
	
	String bestProvider;
	static BTS mbts;
	double lastLat = 0.0, lastLon = 0.0;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent,int startId)
	{
		Log.d("InfoService","�Ұ� InfoService");		
		getJSONfromMap(intent);
		
		//bundle.putDouble("str", 3.0);
		//intentToMap.putExtras(bundle);
		//sendBroadcast(intentToMap);
		UpdateLocation();
		Log.d("InfoService","�����w��:"+lastLat+"/"+lastLon);	
	}
	
	/* �qMap���oJSON Data */
	public void getJSONfromMap(Intent intent)
	{
		bundle = intent.getExtras();
		jsonData = (JSONData) bundle.getSerializable("allData");
		startPointLat = jsonData.getAllRoadStartPointLat();
		startPointLng = jsonData.getAllRoadStartPointLng();
		endPointLat = jsonData.getAllRoadEndPointLat();
		endPointLng = jsonData.getAllRoadEndPointLng();
		htmlInstruction = jsonData.getAllRoadInstruction();
		// get bluetooth
		//mbts = (BTS)bundle.getSerializable("bts");
		//mbts.BTSend(1, 2);
	}
	
	/* �Ұ�Location listener */
	public void UpdateLocation()
	{
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
		Criteria criteria = new Criteria();
	    bestProvider = locationManager.getBestProvider(criteria, false);
		
	    locationListener = new LocationListener()
		{
			int numOfInstruction = htmlInstruction.length; // �ɯ���q�`��
			int numOfNowInstruction = 0; // �ϥΪ̱q�Ĥ@��html_instruction�}�l
			
			public void onLocationChanged(Location newLocation)
			{
				if(numOfNowInstruction >= numOfInstruction)
				{
					Log.d("InfoService", "�ɯ赲��");
					// bluetooth.send(); // ��F���I���H��
				}
				else
				{
					double remainTime = -1.0;
					double distance = -1;
					float speed = newLocation.getSpeed();
					Double nowLng = newLocation.getLongitude();
					Double nowLat = newLocation.getLatitude();
					Log.d("InfoService", "�ϥΪ̥ثe�g/�n�� = " + nowLng + ", /" + nowLat);

					
					// �D�ثe��m�P�ثe���q���I���Z��
					distance = 
							getDistance
							(
									nowLat,nowLng,
									endPointLat[numOfNowInstruction],
									endPointLng[numOfNowInstruction]
							);
					// �D�ثe��m��ثe���q���I���Ѿl�ɶ�
					
					remainTime = distance/speed;
					Log.d("InfoService","�Z���U�Ӹ��f: "+distance+" �ɶ�: "+remainTime);
					
					// �q��Map����ListView, �e�X�һݧ�ʪ����
					
					bundle.putDouble("nowLng", nowLng);
					bundle.putDouble("nowLat", nowLat);
					bundle.putDouble("nowSpeed", speed);
					bundle.putDouble("nowDistance", distance);
					bundle.putDouble("nowRemainTime", remainTime);
					bundle.putInt("nowIndex", numOfNowInstruction);
					
					intentToMap.putExtras(bundle);
					sendBroadcast(intentToMap);
					Log.d("InfoService", "InfoService �w�e�X�{�b�g�n��");
					
					if(remainTime <= 12 & remainTime >= 0)
					{
						// ex:
						// bluetooth.sendTime(remainTIme);
						// bluetooth.sendDirection(getTurn(htmlInstruction[numOfNowInstruction]));
						
					}
					// else do nothing
					if(remainTime == 0)
					{
						numOfNowInstruction++;
					}
				}
			}
			/* ���o���I�g�n�ת��Z�� */
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
	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 , 0, locationListener);
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		//float speed = location.getSpeed();
		location = locationManager.getLastKnownLocation(bestProvider);
		
		Double latitude = location.getLatitude();
		Double longitude = location.getLongitude();
		
		Log.i("Location=", "X=" + longitude.intValue() + ", Y=" + latitude.intValue());
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
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
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