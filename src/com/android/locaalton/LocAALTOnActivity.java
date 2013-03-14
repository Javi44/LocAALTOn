package com.android.locaalton;

import io.socket.SocketIO;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class LocAALTOnActivity extends Activity {

	static final int MAXSIZEBUNDLE = 40;
	
	
	private EditText serverIp;
	private EditText editTextShowLocation;
	private ImageButton buttonServerConnect;
	private ImageButton buttonGetLocation;
	private ImageButton buttonStopUpdates;
	private ImageButton btnPrefs;
	private ProgressBar batteryBar;
	private TextView textSent;
	private TextView textStatus;

	private int cont = 0;
	private LocationManager locManager;
	private LocationListener locListener;
	private LocationListener locListenerNET;
	private boolean netFlag = false;
	private boolean gpsFlag = false;
	private Location mobileLocation;
	private Aux aux = new Aux();
	private boolean readable = aux.isExternalStorageWritable();
    private FileOutputStream fOut;
    private OutputStreamWriter myOutWriter;
    private String serverIpAddress = "";
    private SocketIO socket = null;
    private CommTask commTask;
    private Message msg = new Message();
    private int currentBundleSize = 0;

    private long[] times = new long[MAXSIZEBUNDLE];
    private double[] latitudes = new double[MAXSIZEBUNDLE];
    private double[] longitudes = new double[MAXSIZEBUNDLE];
    private double[] altitudes = new double[MAXSIZEBUNDLE];
    private float[] accuracies = new float[MAXSIZEBUNDLE];
    private float[] speeds = new float[MAXSIZEBUNDLE];
    private int[] socs = new int[MAXSIZEBUNDLE];
    
    
    
  
	@Override
	public void onCreate(Bundle savedInstanceState)
	{	
		super.onCreate(savedInstanceState);
		//Fix the position of the screen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		
		// Editext fields
		serverIp = (EditText) findViewById(R.id.editServerIp);
		editTextShowLocation = (EditText) findViewById(R.id.editTextShowLocation);
		
		// Setting buttons' view and listeners
		buttonServerConnect = (ImageButton) findViewById(R.id.buttonServerConnect);
		buttonServerConnect.setOnClickListener(buttonServerConnectClick);	
		buttonGetLocation = (ImageButton) findViewById(R.id.buttonGetLocation);
		buttonGetLocation.setOnClickListener(buttonGetLocationClick);		
		buttonStopUpdates = (ImageButton) findViewById(R.id.buttonStopUpdates);
		buttonStopUpdates.setOnClickListener(buttonStopUpdatesClick);
		btnPrefs = (ImageButton) findViewById(R.id.btnPrefs);
		btnPrefs.setOnClickListener(buttonSettingsClick);

		// Giving color the buttons
		buttonServerConnect.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0x00FFFFF));
		buttonGetLocation.getBackground().setColorFilter(0xF96BE600, PorterDuff.Mode.MULTIPLY);
		buttonStopUpdates.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);

		// Progress bar for showing the battery status
		batteryBar = (ProgressBar) findViewById(R.id.batteryBar);
		
		// Textviews to show info about the messages received and sent
        textStatus = (TextView)findViewById(R.id.textStatus);
        textSent = (TextView)findViewById(R.id.textSent);

        // Instance of the object which will manage the communication
		commTask = new CommTask();
		
		// Initializing the location manager
		locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Openning the file to write the data if possible
		if(readable)
		{		
			try
			{
				Date cDate = new Date();
				String currentDateTimeString = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
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
	
	@Override
	public void onDestroy()
	// Closing stuff opend before closing the app
	{
		Log.i("User activity","destroying");
		super.onDestroy();
		if(gpsFlag)locManager.removeUpdates(locListener);		
		if(netFlag)locManager.removeUpdates(locListenerNET);
		if(commTask.isConnected()){
			commTask.SendDataToServer("stopSession", msg.encodeEXI(msg.sessionStopRequest()));
			commTask.cancelCommTask();
		}
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
	
	//ONCLICK LISTENERS
	private OnClickListener buttonGetLocationClick = new OnClickListener()
	{
        @Override
        public void onClick(View v) 
        {	
        	if(gpsUpdatesEnabled() && !gpsFlag)
        	{
        		gpsFlag = true;
	        	locListener = getLocationListener();
	        	locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, getTimeSettings(), getDistanceSettings(), locListener);
	        	Toast.makeText(getBaseContext(),"Locations obtained from GPS",Toast.LENGTH_SHORT).show();

        	}
        	if(networkUpdatesEnabled() && !netFlag)
        	{
        		netFlag = true;	
	        	locListenerNET = getLocationListener();
	        	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, getTimeSettings(), getDistanceSettings(), locListenerNET);
	        	Toast.makeText(getBaseContext(),"Locations obtained from network",Toast.LENGTH_SHORT).show();
        	}
        	if(!netFlag && !gpsFlag)
        	{
        		Toast.makeText(getBaseContext(),"No provider selected",Toast.LENGTH_SHORT).show();
        	}
        }
    };
    	
    private OnClickListener buttonStopUpdatesClick = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {    	
        	String toastMsg = "";
    		if(gpsFlag)
    		{
    			locManager.removeUpdates(locListener);
    			gpsFlag = false;
    			toastMsg = "GPS";
    		}
    		if(netFlag)
    		{
    			locManager.removeUpdates(locListenerNET);
    			netFlag = false;
    			if(toastMsg.equals("")){
        			toastMsg = "Network";
    			}else{
    				toastMsg = toastMsg+" & network";
    			}
    		}
    		if(!toastMsg.equals(""))Toast.makeText(getBaseContext(),toastMsg+" updates stopped",Toast.LENGTH_SHORT).show();
        }
    };
	
	private OnClickListener buttonServerConnectClick = new OnClickListener()
	{
        @Override
        public void onClick(View v)
        {
            if (!commTask.isConnected())
            {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals(""))
                {
                    commTask = new CommTask(serverIpAddress, socket, textStatus);
                	commTask.execute();     	
                }
            }
        }
    };
    
    private OnClickListener buttonSettingsClick = new OnClickListener()
    {	 
        @Override
        public void onClick(View v)
        {
        	Intent intent = new Intent(LocAALTOnActivity.this,
        		UserSettingsActivity.class);
        		startActivity(intent);
        }
    };
    
    //LOCATION LISTENER
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
				Toast.makeText(getBaseContext(),"Enable the location provider to continue",Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			    startActivity(myIntent);
			}
			
			@Override
			public void onLocationChanged(Location location)
			{
				cont += 1;
				mobileLocation = location;
				if (mobileLocation != null)
				{	
					IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
					Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
					double longitude = mobileLocation.getLongitude();
					double latitude = mobileLocation.getLatitude();
					double altitude = mobileLocation.getAltitude();
					float accuracy = mobileLocation.getAccuracy();
					float speed = mobileLocation.getSpeed();
					long time = mobileLocation.getTime();
					String provider = mobileLocation.getProvider();
					int soc = (int)(aux.batteryLvl(ifilter, batteryStatus)*100);
					batteryBar.setProgress(soc);
					
					String decoratedS = "Longitude: "+longitude+"\nLatitude: "+latitude+"\nAltitude: "
						+altitude+"\nAccuracy: "+accuracy+"\nSpeed: "+speed+"\nTime: "+time+
						"\nProvider: "+provider+"\nCharging: "+aux.isCharging(ifilter, batteryStatus) +"\nSoC: "+
						soc+"%\nCont: "+cont;	
						
					String notDecoratedS = "<"+cont+","+longitude+","+latitude+","+altitude+","
						+accuracy+","+speed+","+time+">\n";
					
					editTextShowLocation.setText(decoratedS);
					if(readable)
					{
						aux.writeToSDFile(myOutWriter, notDecoratedS);
					}
					if(commTask.isConnected())
					{
						if(bundlesEnabled())
						{
							int bundleSize = getBundleSettings()-1;
							if(currentBundleSize == bundleSize)
							{			
								String xml = msg.bundleLocations(bundleSize, times, longitudes, latitudes, altitudes, accuracies, speeds, socs);
								String exi =  msg.encodeEXI(xml);
								commTask.SendDataToServer("location", exi);
								textSent.setText(notDecoratedS+"\n"+xml+"\n\n"+exi);
								currentBundleSize = 0;
							}
							else
							{
								times[currentBundleSize] = time;
								longitudes[currentBundleSize] = longitude;
								altitudes[currentBundleSize] = altitude;
								accuracies[currentBundleSize] = accuracy;	
								speeds[currentBundleSize] = speed;
								socs[currentBundleSize] = soc;
								latitudes[currentBundleSize] = latitude;
								currentBundleSize +=1;
							}
						}
						else
						{
							String xml = msg.location(time, longitude, latitude, altitude, accuracy, speed, soc);
							String exi =  msg.encodeEXI(xml);
							commTask.SendDataToServer("location", exi);
							textSent.setText(notDecoratedS+"\n"+xml+"\n\n"+exi);
						}
						
					}
				}
				else
				{
					editTextShowLocation.setText("Sorry, location is not determined");
				}				
			}
		};
	}
    
    //SETTINGS METHODS
    private boolean gpsUpdatesEnabled()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	return prefs.getBoolean("gpsUpdates", false);
    };
    
    private boolean networkUpdatesEnabled()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	return prefs.getBoolean("networkUpdates", false);
    };
    
    private boolean bundlesEnabled()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	return prefs.getBoolean("bundlesEnabled", false);
    };
    
    private int getDistanceSettings()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	int d;
    	try
    	{
    		d = Integer.parseInt(prefs.getString("prefDistance", "0"));
    	}catch(Exception e){
    		d = 0;
    	}
    	return d;
    };
    
    private int getTimeSettings()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	int t;
    	try
    	{
    		t = Integer.parseInt(prefs.getString("prefMinTime", "1"));
    	}catch(Exception e){
    		t = 1;
    	}
    	return t*1000;
    };
    
    private int getBundleSettings()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LocAALTOnActivity.this);
    	int b;
    	try
    	{
    		b = Integer.parseInt(prefs.getString("prefBundleSize", "2"));
    		if(b<2)
    		{
    			b=2;
    		}
    		if(b>MAXSIZEBUNDLE)
    		{
    			b = MAXSIZEBUNDLE;
    		}
    	}catch(Exception e){
    		b = 2;
    	}
    	return b;
    };
    
    
}