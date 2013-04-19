package com.android.locaalton;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class CommTask extends AsyncTask<Void, Object, Boolean>
{
	private String serverIpAddress;
	private SocketIO socketIO;
	private SSLSocket socket;
	private OutputStream outTCP;
	private PrintWriter output;
	private Boolean lost = false;
	private TextView textStatus;
	private Boolean connected = false;
	private String transport = "websocket";

	public CommTask()
	{
		serverIpAddress = "";
		socketIO = null;
		textStatus = null;
	};

	public CommTask(String servIp, SocketIO sio, TextView tStatus, String trans)
	{
		serverIpAddress = servIp;
		socketIO = sio;
		textStatus = tStatus;
		transport = trans;
	};

	@Override
	protected void onPreExecute()
	/* Before the execution, thread policy is changed to allow the correct
	 * behaviour of the AsyncTask.
	 */
	{
		Log.i("AsyncTask", "onPreExecute");

		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) >= 9)
		{
			try
			{
				// StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
				Class<?> strictModeClass = Class.forName("android.os.StrictMode",
						true, Thread.currentThread().getContextClassLoader());
				Class<?> threadPolicyClass = Class.forName(
						"android.os.StrictMode$ThreadPolicy", true,
						Thread.currentThread().getContextClassLoader());
				Field laxField = threadPolicyClass.getField("LAX");
				Method setThreadPolicyMethod = strictModeClass.getMethod(
						"setThreadPolicy", threadPolicyClass);
				setThreadPolicyMethod.invoke(strictModeClass, laxField.get(null));
			} 
			catch (Exception e) 
			{ 
				Log.e("AsyncTask","Error on onPreExecute "+e);
			}
		}
	}

	@Override
	protected Boolean doInBackground(Void... params)
	/* This method that runs in the background initialize the medium
	 * that will be used for the communication depending on the transport
	 * selected, and also send to the server the session establishment messages.
	 */
	{
		boolean result = false;
		if(transport.equals("websocket"))
		{
			try 
			{
				Log.i("ClientActivity", "C: Connecting...");
				socketIO = new SocketIO("https://"+this.serverIpAddress+":3000/");
				//socketIO = new SocketIO("http://"+this.serverIpAddress+":3000/");
				socketIO.connect(new IOCallback()
				{	
					@Override
					public void onMessage(JSONObject json, IOAcknowledge ack)
					{
						try
						{
							System.out.println("Server said:" + json.toString(2));
						} 
						catch (JSONException e)
						{
							e.printStackTrace();
						}
					}
					@Override
					public void onMessage(String data, IOAcknowledge ack)
					{
						Log.i("AsyncTask", "onMessage");
					}

					@Override
					public void onError(SocketIOException socketIOException)
					{
						Log.i("AsyncTask", "an error occured while connecting");
						socketIOException.printStackTrace();
						String update[] = {"SocketIO Exception, may be " +
								"problems with the network"};
						publishProgress(update);
						cancel(true);		//to cancel the AsyncTask
					}

					@Override
					public void onDisconnect()
					{
						Log.i("AsyncTask","Connection terminated.");                        
					}

					@Override
					public void onConnect()
					{
						Log.i("ClientActivity", "Entering onConnect");
						Message msg = new Message();
						/*When the connection is established always are sent 
						 * these messages */
						// 1 - To setup the session
						SendDataToServer("setupSession", msg.encodeEXI(
								msg.sessionSetupRequest()));
						// 2 - To ask for the services provided
						SendDataToServer("serviceDiscovery", msg.encodeEXI(
								msg.serviceDiscoveryRequest()));
						// 3 - To inform the server about user useful data
						SendDataToServer("chargingData", msg.encodeEXI(
								msg.chargingDataRequest()));
						Log.i("AsyncTask","Connection established");
						String update[] = {"Connection established! (WEBSOCKET)"};
						connected = true;
						publishProgress(update);
					}
					@Override
					public void on(String event, IOAcknowledge ack, Object... args)
					{
						System.out.println("Server triggered event '" + event + "'");
					}
				});
			}
			catch (Exception e)
			{
				Log.e("ClientActivity", "C: Error", e);
			}
		}
		else if(transport.equals("https"))
		{
			/*Since the server is really simple is not going to read the content
			 *  of the POSTs, thus, the client send both, request and response
			 *   messages, so they are counted for the total traffic data 
			 *   analysis */
			Message msg = new Message();
			postData(msg.encodeEXI(msg.sessionSetupRequest()));
			postData(msg.encodeEXI(msg.sessionSetupResponse()));
			postData(msg.encodeEXI(msg.serviceDiscoveryRequest()));
			postData(msg.encodeEXI(msg.serviceDiscoveryResponse()));
			postData(msg.encodeEXI(msg.chargingDataRequest()));
			postData(msg.encodeEXI(msg.serviceDiscoveryResponse()));
			Log.i("AsyncTask","Connection established");
			String update[] = {"Connection established!"};
			connected = true;
			publishProgress(update);
		}
		else if(transport.equals("tcp"))
		{
			try
			{
				//socket = new Socket(serverIpAddress, 3000);
				SSLSocketFactory sslSocketFactory =
					(SSLSocketFactory)SSLSocketFactory.getDefault();
				socket = (SSLSocket) sslSocketFactory.createSocket(
						serverIpAddress, 3000);
				socket.setKeepAlive(true);
				outTCP = socket.getOutputStream();
				output = new PrintWriter(outTCP);
				/*Since the server is really simple is not going to read the
				 * content of the messages, thus, the client send both, request
				 * and response, so they are counted for the total traffic
	        	 * data analysis */
				Message msg = new Message();
				SendDataToServerTCP(msg.encodeEXI(msg.sessionSetupRequest()));
				SendDataToServerTCP(msg.encodeEXI(msg.sessionSetupResponse()));
				SendDataToServerTCP(msg.encodeEXI(msg.serviceDiscoveryRequest()));
				SendDataToServerTCP(msg.encodeEXI(msg.serviceDiscoveryResponse()));
				SendDataToServerTCP(msg.encodeEXI(msg.chargingDataRequest()));
				SendDataToServerTCP(msg.encodeEXI(msg.serviceDiscoveryResponse()));

				Log.i("AsyncTask","Connection established");
				String update[] = {"Connection established! (TCP)"};
				connected = true;
				publishProgress(update);
			}
			catch(IOException e){
				Log.e("AsyncTask","IOException while using TCP transport "+e);
				String update[] = {"IOException. Check your internet " +
						"connection and if the server is up"};
				publishProgress(update);
			}
		}
		return result;
	}

	public void SendDataToServer(String event, String msg) 
	// Method used to send data to the server when websockets are used
	{
		try
		{
			if (socketIO.isConnected())
			{
				lost=false;
				Log.i("AsyncTask", "Sending "+msg);
				socketIO.emit(event, msg);
			}
			else
			/* If at this point the socket is not connected means that the 
			 * connection is lost */
			{	
				Log.i("AsyncTask", "SendDataToNetwork: Cannot send message." +
						" WebSocket is closed");
				if(lost==false)
				{
					Date cDate = new Date();
					String lostDateTimeString = 
						new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cDate);
					String update[] = {"CONNECTION LOST! ("+lostDateTimeString+")"};
					publishProgress(update);
					lost=true;
				}
			}
		}
		catch (Exception e)
		{
			Log.e("AsyncTask", "SendDataToNetwork: Message send failed." +
					" Caught an exception: "+e);
		}
	}

	public void postData(String message)
	// Method used to send data to the server when POSTs are used
	{
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("https://"+this.serverIpAddress+":3000/");
		try
		{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("Location", message));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);          
		}
		catch (ClientProtocolException e) 
		{
			Log.e("AsyncTask", "CLIENT PROTOCOL EXCEPTION while sending POST: "+e);
			if(lost==false)
			{
				Date cDate = new Date();
				String lostDateTimeString = 
					new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cDate);
				String update[] = {"Unable to send POST! " +"("+
						lostDateTimeString+") ClientProtocolException "+ e};
				publishProgress(update);
				lost=true;
			}
		}
		catch (IOException e)
		{
			Log.e("AsyncTask", "IOEXCEPTION while sending POST: "+e);
			if(lost==false)
			{
				Date cDate = new Date();
				String lostDateTimeString = 
					new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cDate);
				String update[] = {"Unable to send POST! " +"("+
						lostDateTimeString+") IOException "+ e};
				publishProgress(update);
				lost=true;
			}
		}
	}

	public void SendDataToServerTCP(String msg)
	// Method used to send data to the server when TCP sockets are used
	{
		try
		{
			if (socket.isConnected())
			{
				lost=false;
				Log.i("AsyncTask", "Sending TCP"+msg);
				output.println(msg);
				output.flush();
			}
			else
			{	
				Log.i("AsyncTask", "SendDataToNetwork: Cannot send message." +
						" TCP Socket is closed");
				if(lost==false)
				{
					Date cDate = new Date();
					String lostDateTimeString =
						new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cDate);
					String update[] = {"CONNECTION LOST! ("+lostDateTimeString+")"};
					publishProgress(update);
					lost=true;
				}
			}
		}
		catch (Exception e)
		{
			Log.e("AsyncTask", "EXCEPTION while sending TCP message: "+e);
		}
	}

	@Override
	protected void onProgressUpdate(Object... values)
	// Method to show updates in the interface thread
	{
		if (values.length > 0)
		{
			Log.i("AsyncTask", "onProgressUpdate");
			textStatus.setText("Update: "+values[0].toString());
		}
	}
	@Override
	protected void onCancelled()
	{
		Log.i("AsyncTask", "Cancelled.");
	}
	@Override
	protected void onPostExecute(Boolean result)
	{
		if (result)
		{
			Log.i("AsyncTask", "onPostExecute: Completed with an Error.");
			textStatus.setText("There was a connection error.");
		}
		else
		{
			Log.i("AsyncTask", "onPostExecute: Completed.");
		}
	}
	public void cancelCommTask()
	{
		this.cancel(true);
		if(socketIO.isConnected())
		{
			socketIO.disconnect();
		}
		if(socket.isConnected())
		{
			try
			{
				socket.close();
			}
			catch(IOException e)
			{
				Log.e("AsyncTask","IOException while closing the TCP socket");
			}
		}
	}

	public boolean isConnected()
	// Returns if any of the transport has established a connection
	{
		return connected;
	}

	public String transport()
	// Return the transport selected
	{
		return transport;
	}
}
