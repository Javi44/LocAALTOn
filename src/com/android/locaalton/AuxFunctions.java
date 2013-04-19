package com.android.locaalton;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

public class AuxFunctions
{
	public boolean isExternalStorageWritable()
	/* Checks if external storage is available for read and write */
	{	
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}

	public void writeToSDFile(OutputStreamWriter osw, String s)
	/* Append the String s into the OutputStreamWriter osw */
	{
		try
		{
			osw.append(s);
			osw.flush();
		}
		catch(Exception e)
		{
			Log.d("Client activity","Error writing into the file");
		}		
	}

	public float batterySoC(IntentFilter ifilter,  Intent batteryStatus)
	/* Returns the battery state of charge */
	{
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		return batteryPct;
	}

	public boolean isCharging(IntentFilter ifilter,  Intent batteryStatus)
	/* Returns a boolean value indicating if the mobile phone is charging */
	{
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
		return isCharging;
	}

	public String diff(String oldValue, String newValue)
	/* This method return the difference between two values (old and new).
	 * For this purpose it starts analyzing the similarities from left
	 * to right. When a different character is found, it is considered
	 * that the remaining characters may also change and it is returned
	 * the slice of string remaining in the right*/
	{
		String diff = "e";
		if(!oldValue.equals(newValue))
		{
			/* This loop is to equate the length of the words and 
			 * to indicate to the server if the new value has less digits
			 * than the previous one */
			while(!(oldValue.length() == newValue.length()))
			{
				if(oldValue.length() < newValue.length())
				{
					oldValue += "<";
				}
				else
				{
					newValue += "<";
				}
			}
			//This loop find the first different character from left to right
			int i = 0;
			while((i < oldValue.length()-1) && (oldValue.charAt(i) == 
				newValue.charAt(i)))
			{
				i +=1;
			}
			diff = newValue.substring(i, newValue.length()-1);
		}
		return diff;
	}

	public String utf8izer(String input)
	/* Replace characters no included in charset UTF-8 by spaces and return the
	 * String with the changes */
	{
		String utf8output = "";
		try
		{
			byte[] utf8Bytes = input.getBytes("UTF-8");
			utf8output = new String(utf8Bytes, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]", 
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8output);

		System.out.println("Before: " + utf8output);
		utf8output = unicodeOutlierMatcher.replaceAll(" ");
		System.out.println("After: " + utf8output);

		return utf8output;
	}
}
