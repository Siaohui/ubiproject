package com.nchu.motoguider;

import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;

public class Bluetooth
{
	private static final String TAG = "bluetoothtest";  
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "00:12:09:29:47:40";
	/*
	 * Define communitcation code with arduino
	 * */
	

}
