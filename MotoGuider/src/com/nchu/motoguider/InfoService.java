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
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	
	double lastLat = 0.0, lastLon = 0.0;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent,int startId)
	{
		mbts = new BTS();
		//mbts.BTSend(0,1);
		//mbts.BTSend(Time, Direction)
		Log.d("InfoService","啟動 InfoService");		
		getJSONfromMap(intent);
		//
	      if(btAdapter==null) 
	      {
	    	  Log.e("BTSError", "Bluetooth not support");
	      }else if (!btAdapter.isEnabled()) 
	      {
	          Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	      }
	      else Log.i("BTState","...Bluetooth ON...");
	      
	      if(mbts!=null)
	      {
	    	  if(mbts.BTSConnect()=="Finish")
	      		;
	      }
	      else Log.e("BTState","mbts is NULL");
		//
		//bundle.putDouble("str", 3.0);
		//intentToMap.putExtras(bundle);
		//sendBroadcast(intentToMap);
		UpdateLocation();
		Log.d("InfoService","完成定位:"+lastLat+"/"+lastLon);
		mbts.BTSend(0,1);

	}
	
	/* 從Map取得JSON Data */
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
	}
	
	/* 啟動Location listener */
	public void UpdateLocation()
	{
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
		Criteria criteria = new Criteria();
	    bestProvider = locationManager.getBestProvider(criteria, false);
		
	    locationListener = new LocationListener()
		{
	    	boolean haveChanged = false;
			int numOfInstruction = htmlInstruction.length; // 導航路段總數
			int numOfNowInstruction = 0; // 使用者從第一個html_instruction開始
			public void onLocationChanged(Location newLocation)
			{
				if(numOfNowInstruction > numOfInstruction)
				{
					Log.d("InfoService", "導航結束");
					mbts.BTSend(0, 0);
				}
				else
				{
					double remainTime = -1.0;
					double distance = -1;
					float speed = newLocation.getSpeed();
					Double nowLng = newLocation.getLongitude();
					Double nowLat = newLocation.getLatitude();
					Log.d("InfoService", "使用者目前經/緯度 = " + nowLng + ", /" + nowLat);

					// 求目前位置與目前路段終點的距離
					distance = 
							getDistance
							(
									nowLat,nowLng,
									endPointLat[numOfNowInstruction],
									endPointLng[numOfNowInstruction]
							);
					// 求目前位置到目前路段終點的剩餘時間
					
					remainTime = distance/speed;
					Log.d("InfoService","距離下個路口: "+distance+" 時間: "+remainTime);
					
					// 通知Map改變ListView, 送出所需更動的資料
					
					bundle.putDouble("nowLng", nowLng);
					bundle.putDouble("nowLat", nowLat);
					bundle.putDouble("nowSpeed", speed);
					bundle.putDouble("nowDistance", distance);
					bundle.putDouble("nowRemainTime", remainTime);
					bundle.putInt("nowIndex", numOfNowInstruction);
					intentToMap.putExtras(bundle);
					sendBroadcast(intentToMap);
					Log.d("InfoService", "InfoService 已送出現在經緯度");
					if(distance > 24)
					{
						mbts.BTSend(8, getTurn(htmlInstruction[numOfNowInstruction]));
					}
					if(distance < 24)
					{
						mbts.BTSend((int)(distance/4)-1, getTurn(htmlInstruction[numOfNowInstruction]));
					}	
					if(distance < 10)
					{
						numOfNowInstruction++;
						
						if(numOfNowInstruction>numOfInstruction)
						{
							Log.d("InfoService", "導航結束");
							mbts.BTSend(0, 0);
							numOfNowInstruction--;
						}
					}
				}
			}
			/* 取得兩點經緯度的距離 */
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