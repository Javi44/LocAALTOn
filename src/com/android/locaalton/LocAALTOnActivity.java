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


public class LocAALTOnActivity extends Activity
{
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
	private AuxFunctions aux = new AuxFunctions();
	private boolean readable = aux.isExternalStorageWritable();
	private FileOutputStream fOut;
	private OutputStreamWriter myOutWriter;
	private String serverIpAddress = "";
	private SocketIO socket = null;
	private CommTask commTask;
	private Message msg = new Message();
	private int currentBundleSize = 0;

	private String oldLongitude = " ";
	private String oldLatitude = " ";
	private String oldAltitude = " ";
	private String oldAccuracy = " ";
	private String oldSpeed = " ";
	private String oldTime = " ";
	private String oldSoc = " ";

	private String[] times = new String[MAXSIZEBUNDLE];
	private String[] latitudes = new String[MAXSIZEBUNDLE];
	private String[] longitudes = new String[MAXSIZEBUNDLE];
	private String[] altitudes = new String[MAXSIZEBUNDLE];
	private String[] accuracies = new String[MAXSIZEBUNDLE];
	private String[] speeds = new String[MAXSIZEBUNDLE];
	private String[] socs = new String[MAXSIZEBUNDLE];

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
		buttonServerConnect.getBackground().setColorFilter(
				new LightingColorFilter(0xFFFFFFFF, 0x00FFFFF));
		buttonGetLocation.getBackground().setColorFilter(
				0xF96BE600, PorterDuff.Mode.MULTIPLY);
		buttonStopUpdates.getBackground().setColorFilter(
				0xFFFF0000, PorterDuff.Mode.MULTIPLY);

		buttonStopUpdates.setEnabled(false);

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
				String currentDateTimeString = 
					new SimpleDateFormat("dd-MM-yyyy").format(cDate);
				fOut = 
					new FileOutputStream("/sdcard/locations/"+currentDateTimeString+".txt",true);
				myOutWriter= new OutputStreamWriter(fOut);			
			}
			catch (FileNotFoundException e)
			{
				Toast.makeText(getBaseContext(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onDestroy()
	// Closing stuff opened before closing the app
	{
		Log.i("User activity","destroying");
		super.onDestroy();
		if(gpsFlag)
		{
			locManager.removeUpdates(locListener);		
		}
		if(netFlag)
		{
			locManager.removeUpdates(locListenerNET);
		}
		if(commTask.transport().equals("websocket"))
		{
			if(commTask.isConnected())
			{
				commTask.SendDataToServer("stopSession", msg.encodeEXI(
						msg.sessionStopRequest()));
			}
		}
		else if (commTask.transport().equals("https"))
		{
			commTask.postData(msg.encodeEXI(msg.sessionStopRequest()));
		}else if (commTask.transport().equals("https"))
		{
			commTask.SendDataToServerTCP(
					msg.encodeEXI(msg.sessionStopRequest()));
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
	/* When the buttonGetLocation is clicked then, the LocationManager start
	 * requesting GPS locations to the provider selected (GPS, Network or both)
	 * unless the locations updates are already started.
	 * Moreover,  the time between samples and distance between sample settings
	 * are given as input to the LocationManager.
	 */
	{
		@Override
		public void onClick(View v) 
		{	
			if(gpsUpdatesEnabled() && !gpsFlag)
			{
				gpsFlag = true;
				locListener = getLocationListener();
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						getTimeSettings(), getDistanceSettings(), locListener);
				Toast.makeText(getBaseContext(),"Locations obtained from GPS",
						Toast.LENGTH_SHORT).show();
				// Disable buttonGetLocation and enable the buttonStopUpdates
				buttonGetLocation.setEnabled(false);
				buttonStopUpdates.setEnabled(true);

			}
			if(networkUpdatesEnabled() && !netFlag)
			{
				netFlag = true;	
				locListenerNET = getLocationListener();
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						getTimeSettings(), getDistanceSettings(), locListenerNET);
				Toast.makeText(getBaseContext(),"Locations obtained from network",
						Toast.LENGTH_SHORT).show();
				// Disable buttonGetLocation and enable the buttonStopUpdates
				buttonGetLocation.setEnabled(false);
				buttonStopUpdates.setEnabled(true);
			}
			if(!netFlag && !gpsFlag)
			{
				Toast.makeText(getBaseContext(),"No provider selected",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private OnClickListener buttonStopUpdatesClick = new OnClickListener()
	/* Remove the location updates of the corresponding location provider */
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
			if(!toastMsg.equals(""))Toast.makeText(getBaseContext(),toastMsg+
					" updates stopped",Toast.LENGTH_SHORT).show();
			// Enable buttonGetLocation and disable the buttonStopUpdates
			buttonGetLocation.setEnabled(true);
			buttonStopUpdates.setEnabled(false);
		}
	};

	private OnClickListener buttonServerConnectClick = new OnClickListener()
	/* Initialize the AsyncTask and try to establish the connection using
	 * the selected transport.
	 */
	{
		@Override
		public void onClick(View v)
		{
			if (!commTask.isConnected())
			{
				serverIpAddress = serverIp.getText().toString();
				if (!serverIpAddress.equals(""))
				{
					commTask = new CommTask(serverIpAddress, socket, textStatus,
							getTransportSettings());
					commTask.execute();     	
				}
			}
		}
	};

	private OnClickListener buttonSettingsClick = new OnClickListener()
	// Opens settings menu
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
			}

			@Override
			public void onProviderEnabled(String provider)
			{
			}

			@Override
			public void onProviderDisabled(String provider)
			/* If the location provider selected is disable, the user is redirected
			 * to the location settings screen to enable it.
			 */
			{
				Toast.makeText(getBaseContext(),"Enable the location provider " +
						"to continue",Toast.LENGTH_SHORT).show();
				Intent myIntent = 
					new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(myIntent);
			}

			@Override
			public void onLocationChanged(Location location)
			// This method is executed each time that the GPS has an update
			{
				cont += 1;
				mobileLocation = location;
				if (mobileLocation != null)
				{	
					// The data of the new location and status of the phone is collected
					IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
					Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
					String longitude = Double.toString(mobileLocation.getLongitude());
					String latitude = Double.toString(mobileLocation.getLatitude());
					String altitude = Double.toString(mobileLocation.getAltitude());
					String accuracy = Float.toString(mobileLocation.getAccuracy());
					String speed = Float.toString(mobileLocation.getSpeed());
					String time = Long.toString(mobileLocation.getTime());
					String provider = mobileLocation.getProvider();
					String soc = Integer.toString((int)(
							aux.batterySoC(ifilter, batteryStatus)*100));
					batteryBar.setProgress(Integer.parseInt(soc));

					// Variables to be used if delta compression is selected
					String diffTime = new String();
					String diffLongitude = new String();
					String diffLatitude = new String();
					String diffAltitude = new String();
					String diffAccuracy = new String();
					String diffSpeed = new String();
					String diffSoc = new String();

					if(getDeltaCompSettings())
						/* if delta compression is selected the difference of the new
						 * values and last measure values is calculated and stored in
						 * "dif-" variables
						 */
					{
						//Log.d("Differences", "-------------------------------");
						diffTime = aux.diff(oldTime, time);
						//Log.d("Diff time: ", oldTime+" "+diffTime);
						diffLongitude = aux.diff(oldLongitude, longitude);
						//Log.d("Diff Long: ", oldLongitude+" "+diffLongitude);
						diffLatitude = aux.diff(oldLatitude, latitude);
						//Log.d("Diff Lat: ", oldLatitude+" "+diffLatitude);
						diffAltitude = aux.diff(oldAltitude, altitude);
						//Log.d("Diff Alt: ", oldAltitude+" "+diffAltitude);
						diffAccuracy = aux.diff(oldAccuracy, accuracy);
						//Log.d("Diff Acc: ", oldAccuracy+" "+diffAccuracy);
						diffSpeed = aux.diff(oldSpeed, speed);
						//Log.d("Diff speed: ", oldSpeed+" "+diffSpeed);
						diffSoc = aux.diff(oldSoc, soc);
						//Log.d("Diff soc: ", oldSoc+" "+diffSoc);
					}

					String decoratedS = "Longitude: "+longitude+"\nLatitude: "
							+latitude+"\nAltitude: "+altitude+"\nAccuracy: "
							+accuracy+"\nSpeed: "+speed+"\nTime: "+time
							+"\nProvider: "+provider+"\nCharging: "
							+aux.isCharging(ifilter, batteryStatus) +"\nSoC: "
							+soc+"%\nCont: "+cont;	

					String notDecoratedS = cont+","+provider+","+longitude+","
							+latitude+","+altitude+","+soc+","
							+accuracy+","+speed+","+time+"\n";

					// New values are displayed in the screen
					editTextShowLocation.setText(decoratedS);
					// and written to a file
					if(readable)
					{
						if(cont==1)
						{
							aux.writeToSDFile(myOutWriter, "---------------" +
									"------------------\n");
						}
						aux.writeToSDFile(myOutWriter, notDecoratedS);
					}					
					String xml = "nothing";
					String exi = "nothing";

					if(bundlesEnabled())
						/* If bundles are enabled, measurements are stored in 
						 * arrays until the bundle size is reached, then it is 
						 * sent, otherwise (else branch) always that there is a
						 * new measure it is sent.*/
					{
						int bundleSize = getBundleSettings()-1;
						if(currentBundleSize == bundleSize)
						{			
							/* when the data of the bundle is ready it is 
							 * converted to XML and compressed with EXI before
							 * sending it */
							xml = msg.bundleLocations(bundleSize, times, 
									longitudes, latitudes, altitudes, 
									accuracies, speeds, socs);
							exi =  msg.encodeEXI(xml);
							send(exi, notDecoratedS+"\n"+xml+"\n\n"+exi);
							currentBundleSize = 0;
						}
						else
						{
							if(getDeltaCompSettings())
								/* If delta compression is enabled instead of 
								 * the complete values only the difference is 
								 * stored */
							{
								times[currentBundleSize] = diffTime;
								longitudes[currentBundleSize] = diffLongitude;
								altitudes[currentBundleSize] = diffAltitude;
								accuracies[currentBundleSize] = diffAccuracy;	
								speeds[currentBundleSize] = diffSpeed;
								socs[currentBundleSize] = diffSoc;
								latitudes[currentBundleSize] = diffLatitude;
								currentBundleSize +=1;
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
					}
					else
						/* when no bundles enabled if delta compression is 
						 * enabled then the difference values are sent after 
						 * being encapsulated in an XML and compressed with EXI,
						 * otherwise full values are encapsulated, compressed 
						 * and sent. */
					{	

						if(getDeltaCompSettings())
						{
							xml = msg.location(diffTime, diffLongitude, 
									diffLatitude, diffAltitude, diffAccuracy, 
									diffSpeed, diffSoc);
						}
						else
						{			
							xml = msg.location(time, longitude, latitude, 
									altitude, accuracy, speed, soc);
						}
						exi =  msg.encodeEXI(xml);
						send(exi, notDecoratedS+"\n"+xml+"\n\n"+exi);
					}

					/* Values of the last measure are stored to be compared with
					 *  the next one */
					oldTime = time;
					oldLongitude = longitude;
					oldAltitude = altitude;
					oldLatitude = latitude;
					oldSpeed = speed;
					oldAccuracy = accuracy;
					oldSoc = soc;
				}
				else
				{
					editTextShowLocation.setText("Sorry, location is not " +
							"determined");
				}				
			}
		};
	}

	private void send(String data, String update)
	/* Depending on the transport selected is used un method or other to send 
	 * the data */
	{
		
		
		if(commTask.transport().equals("websocket"))
		{
			if(commTask.isConnected())
			{
				commTask.SendDataToServer("location", data);
			}
		}
		else if (commTask.transport().equals("https"))
		{
			commTask.postData(data);
		}
		else if (commTask.transport().equals("tcp"))
		{
			commTask.SendDataToServerTCP(data);
		}
		textSent.setText(update);	
	}

	//SETTINGS METHODS (Methods to retrieve the settings values)
	private boolean gpsUpdatesEnabled()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		return prefs.getBoolean("gpsUpdates", false);
	};

	private boolean networkUpdatesEnabled()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		return prefs.getBoolean("networkUpdates", false);
	};

	private boolean bundlesEnabled()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		return prefs.getBoolean("bundlesEnabled", false);
	};

	private int getDistanceSettings()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		int d;
		try
		{
			d = Integer.parseInt(prefs.getString("prefDistance", "0"));
		}
		catch(Exception e)
		{
			d = 0;
		}
		return d;
	};

	private int getTimeSettings()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		int t;
		try
		{
			t = Integer.parseInt(prefs.getString("prefMinTime", "0"));
		}
		catch(Exception e)
		{
			t = 1;
		}
		return t*1000;
	};

	private int getBundleSettings()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
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
		}
		catch(Exception e)
		{
			b = 2;
		}
		return b;
	};

	private String getTransportSettings()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		String t;
		try
		{
			t = prefs.getString("transportSelected", "w");
		}
		catch(Exception e)
		{
			t = "w";
		}
		return t;
	};

	private boolean getDeltaCompSettings()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				LocAALTOnActivity.this);
		return prefs.getBoolean("deltaCompEnabled", false);
	};
}