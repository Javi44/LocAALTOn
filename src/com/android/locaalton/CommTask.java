package com.android.locaalton;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class CommTask extends AsyncTask<Void, Object, Boolean> {

	String serverIpAddress;
	SocketIO socket;
	Boolean lost = false;
	TextView textStatus;
	Boolean connected = false;

	public CommTask(){
		serverIpAddress = "";
		socket = null;
		textStatus = null;
	};
	
	public CommTask(String servIp, SocketIO sio, TextView tStatus){
		serverIpAddress = servIp;
		socket = sio;
		textStatus = tStatus;
	};
	
    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Void... params) { //This runs on a different thread
        boolean result = false;
        try {
            Log.i("ClientActivity", "C: Connecting...");
            socket = new SocketIO("http://"+this.serverIpAddress+":3000/");
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
                    Log.i("AsyncTask", "onMessage");
                }

                @Override
                public void onError(SocketIOException socketIOException) {
                	Log.i("AsyncTask", "an Error occured");
                    socketIOException.printStackTrace();
                }

                @Override
                public void onDisconnect() {
                	Log.i("AsyncTask","Connection terminated.");                        
                }

                @Override
                public void onConnect() {
                	Log.i("AsyncTask","Connection established");
                	String update[] = {"Connection established!"};
                	connected = true;
                	publishProgress(update);
                }

                @Override
                public void on(String event, IOAcknowledge ack, Object... args) {
                    System.out.println("Server triggered event '" + event + "'");
                    if(event.equals("ACK")){
                        publishProgress(args);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ClientActivity", "C: Error", e);
//            connected = false;
        } 
        return result;
    }

    public void SendDataToServer(String event, String msg) { //You run this from the main thread.
        try {
            if (socket.isConnected()) {
            	lost=false;
                Log.i("AsyncTask", "Sending "+event+": "+msg);
                socket.emit(event, msg);
            } else {
            	
                Log.i("AsyncTask", "SendDataToNetwork: Cannot send message. Socket is closed");
                if(lost==false){
                	Date cDate = new Date();
            		String lostDateTimeString = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cDate);
                    String update[] = {"CONNECTION LOST! ("+lostDateTimeString+")"};
                    publishProgress(update);
                    lost=true;
                }
            }
        } catch (Exception e) {
            Log.i("AsyncTask", "SendDataToNetwork: Message send failed. Caught an exception: "+e);
        }
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if (values.length > 0) {
            Log.i("AsyncTask", "onProgressUpdate");
            textStatus.setText("Update: "+values[0].toString());
        }
    }
    @Override
    protected void onCancelled() {
        Log.i("AsyncTask", "Cancelled.");
    }
    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
            textStatus.setText("There was a connection error.");
        } else {
            Log.i("AsyncTask", "onPostExecute: Completed.");
//            textStatus.setText("Completed without");
        }
    }
    public void cancelCommTask(){
    	this.cancel(true);
    	socket.disconnect();
    }
    
    public boolean isConnected(){
    	return connected;
    }
    
}
