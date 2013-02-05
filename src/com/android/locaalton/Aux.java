package com.android.locaalton;

import java.io.OutputStreamWriter;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

public class Aux {

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
	
	public void writeToSDFile(OutputStreamWriter osw, String s)
	{
		try
		{
	        osw.append(s);
	        osw.flush();
	        //Toast.makeText(getBaseContext(),"Done writing SD 'LocationData.txt'",Toast.LENGTH_SHORT).show();	        
		}
		catch(Exception e)
		{
//			Toast.makeText(getBaseContext(), e.getMessage(),
//            Toast.LENGTH_SHORT).show();
			Log.d("Client activity","Error writing the file");
		}		
	}
	
	public float batteryLvl(IntentFilter ifilter,  Intent batteryStatus)
	{
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		return batteryPct;
	}
	
	public boolean isCharging(IntentFilter ifilter,  Intent batteryStatus)
	{
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
		return isCharging;
	}
}
