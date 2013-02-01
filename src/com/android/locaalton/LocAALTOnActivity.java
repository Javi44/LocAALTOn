package com.android.locaalton;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


public class LocAALTOnActivity extends Activity {

	private EditText serverIp;
	private EditText editTextShowLocation;
	private ImageButton buttonServerConnect;
	private ImageButton buttonGetLocation;
	private ImageButton buttonStopUpdates;
	private ProgressBar batteryBar;
	private int cont = 0;
	private LocationManager locManager;
	private LocationListener locListener;
	private LocationListener locListenerNET;
	private int provider = 0;
	private int distance = 0;
	private boolean netFlag = false;
	private boolean gpsFlag = false;
	private Location mobileLocation;
	private boolean readable = isExternalStorageWritable();
    private String currentDateTimeString;
    private FileOutputStream fOut;
    private OutputStreamWriter myOutWriter;
    private String serverIpAddress = "";
    private SocketIO socket = null;
    private boolean connected = false;
  
	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		
		serverIp = (EditText) findViewById(R.id.editServerIp);
		buttonServerConnect = (ImageButton) findViewById(R.id.buttonServerConnect);
		buttonServerConnect.setOnClickListener(connectListener);
		
		editTextShowLocation = (EditText) findViewById(R.id.editTextShowLocation);
		
		buttonGetLocation = (ImageButton) findViewById(R.id.buttonGetLocation);
		buttonGetLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonGetLocationClick();
			}
		});
		
		buttonStopUpdates = (ImageButton) findViewById(R.id.buttonStopUpdates);
		buttonStopUpdates.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopUpdatesClick();
			}
			
		});
		batteryBar = (ProgressBar) findViewById(R.id.batteryBar);
		
		Date cDate = new Date();
		currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
		
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if(readable)
		{		
			try
			{
//				fOut = new FileOutputStream("/sdcard/LocationData.txt",true);
				fOut = new FileOutputStream("/sdcard/locations/"+currentDateTimeString+".txt",true);
				myOutWriter= new OutputStreamWriter(fOut);			
			}
			catch (FileNotFoundException e)
			{
				Toast.makeText(getBaseContext(), e.getMessage(),
			    Toast.LENGTH_SHORT).show();
			}
			try {
				myOutWriter.append("-------------------------------------------------------\n");
				myOutWriter.flush();
			} catch (IOException e) {
				Toast.makeText(getBaseContext(), e.getMessage(),
				Toast.LENGTH_SHORT).show();
			}        
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   switch (item.getItemId()) {
	        case R.id.gpsButton:
	           provider = 0;
	           return true;
	        case R.id.networkButton:
		       provider = 1;
		       return true;	       
	        case R.id.bothProvidersButton:
		       provider = 2;
		       return true;
	        case R.id.btInfo:
	           Toast.makeText(getApplicationContext(), "LocAALTOn v 1.0\nCoded by Javier PŽrez", Toast.LENGTH_SHORT).show();
	           return true;
	        case R.id.d0:
	           distance = 0;
	           return true;
	        case R.id.d10:
	           distance = 10;
		       return true;	       
	        case R.id.d100:
	           distance = 100;
		       return true;
	        case R.id.d1000:
	           distance = 1000;
		       return true;
	        default:
	           return super.onOptionsItemSelected(item);
	    }
	}

	
	@Override
	public void onDestroy()
	{
		stopUpdatesClick();
		try
		{
			myOutWriter.close();
			fOut.close();
		}
		catch (IOException e)
		{
			Toast.makeText(getBaseContext(), e.getMessage(),
			Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onBackPressed() {
//		Toast.makeText(getBaseContext(),"Back pressed",Toast.LENGTH_LONG).show();
//		Toast.makeText(getBaseContext(),"But...",Toast.LENGTH_LONG).show();
		Toast.makeText(getBaseContext(),"Nothing happened",Toast.LENGTH_LONG).show();
	}
	
	/** Gets the current location and update the mobileLocation variable*/
	public LocationListener getLocationListener()
	{
		return new LocationListener()
		{		
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{
				//Toast.makeText(getBaseContext(),"onStatusChanged",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onProviderEnabled(String provider)
			{
				//Toast.makeText(getBaseContext(),"onProviderEnabled",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onProviderDisabled(String provider)
			{
				Toast.makeText(getBaseContext(),"The selected provider\n is disabled",Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLocationChanged(Location location)
			{
				cont += 1;
				mobileLocation = location;
				if (mobileLocation != null)
				{			
					double longitude = mobileLocation.getLongitude();
					double latitude = mobileLocation.getLatitude();
					double altitude = mobileLocation.getAltitude();
					float accuracy = mobileLocation.getAccuracy();
					float speed = mobileLocation.getSpeed();
					long time = mobileLocation.getTime();
					String provider = mobileLocation.getProvider();
					String decoratedS = "Longitude: "+longitude+"\nLatitude: "+latitude+"\nAltitude: "
						+altitude+"\nAccuracy: "+accuracy+"\nSpeed: "+speed+"\nTime: "+time+
						"\nProvider: "+provider+"\nCharging: "+isCharging() +"\nSoC: "+
						(int)(batteryLvl()*100)+"%\nCont: "+cont;	
						batteryBar.setProgress((int)(batteryLvl()*100));
						String notDecoratedS = "<"+cont+","+longitude+","+latitude+","+altitude+","
						+accuracy+","+speed+","+time+">\n";
					editTextShowLocation.setText(decoratedS);
					if(readable)
					{
						writeToSDFile(notDecoratedS);
					}
					if(connected)
					{
						socket.emit("location", notDecoratedS);
					}
				}
				else
				{
					editTextShowLocation.setText("Sorry, location is not determined");
				}				
			}
		};
	}
	
	public void buttonGetLocationClick()
	{		
		switch (provider) {
        case 0:
        	gpsFlag = true;
        	locListener = getLocationListener();
        	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, distance, locListener);
        	Toast.makeText(getApplicationContext(), "Location obtained from GPS", Toast.LENGTH_SHORT).show();
        	break;
        case 1:
        	netFlag = true;	
        	locListenerNET = getLocationListener();
        	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, distance, locListenerNET);
        	Toast.makeText(getApplicationContext(), "Location obtained from Network", Toast.LENGTH_SHORT).show();
        	break;
        case 2:
        	gpsFlag = true;
        	netFlag = true;	
        	locListener = getLocationListener();
        	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, distance, locListener);
        	locListenerNET = getLocationListener();
        	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, distance, locListenerNET);
        	Toast.makeText(getApplicationContext(), "Location obtained from both providers", Toast.LENGTH_SHORT).show();	
        	break;
        default:
        	Toast.makeText(getApplicationContext(), "Not provider selected", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void stopUpdatesClick()
	{
		if(gpsFlag)
		{
			locManager.removeUpdates(locListener);
			gpsFlag = false;
			Toast.makeText(getBaseContext(),"GPS Updates stopped",Toast.LENGTH_SHORT).show();	
		}
		if(netFlag)
		{
			locManager.removeUpdates(locListenerNET);
			netFlag = false;
			Toast.makeText(getBaseContext(),"Network Updates stopped",Toast.LENGTH_SHORT).show();	
		}
	}
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable()
	{	
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state))
	    {
	        return true;
	    }
	    return false;
	}
	
	public void writeToSDFile(String s)
	{
		try
		{
	        myOutWriter.append(s);
	        myOutWriter.flush();
	        //Toast.makeText(getBaseContext(),"Done writing SD 'LocationData.txt'",Toast.LENGTH_SHORT).show();	        
		}
		catch(Exception e)
		{
			Toast.makeText(getBaseContext(), e.getMessage(),
            Toast.LENGTH_SHORT).show();
		}		
	}
	
	public float batteryLvl()
	{
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		return batteryPct;
	}
	
	public boolean isCharging()
	{
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
		return isCharging;
	}
	
	private OnClickListener connectListener = new OnClickListener() {
		 
        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };
    
    public class ClientThread implements Runnable {
    	 
        public void run() {
            try {
                Log.d("ClientActivity", "C: Connecting...");
                socket = new SocketIO("http://"+serverIpAddress+":3000/");
                socket.connect(new IOCallback() {
                    @Override
                    public void onMessage(JSONObject json, IOAcknowledge ack) {
                        try {
                            System.out.println("Server said:" + json.toString(2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onMessage(String data, IOAcknowledge ack) {
                        System.out.println("Server said: " + data);
                    }

                    @Override
                    public void onError(SocketIOException socketIOException) {
                        System.out.println("an Error occured");
                        socketIOException.printStackTrace();
                    }

                    @Override
                    public void onDisconnect() {
                        System.out.println("Connection terminated.");                        
                    }

                    @Override
                    public void onConnect() {
                        System.out.println("Connection established");
                    }

                    @Override
                    public void on(String event, IOAcknowledge ack, Object... args) {
                        System.out.println("Server triggered event '" + event + "'");
                    }
                });

                // This line is cached until the connection is established.
                //socket.send("Hello Server!");
                connected = true;
                Message msg = new Message();
                Log.d("ClientActivity",msg.encodeEXI("test.xml", "test.exi"));
                socket.emit("location", msg.writeXml());
                
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}