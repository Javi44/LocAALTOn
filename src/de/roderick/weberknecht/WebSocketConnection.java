/*
 *  Copyright (C) 2011 Roderick Baier
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package de.roderick.weberknecht;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class WebSocketConnection
		implements WebSocket
{
	private URI url = null;
	private WebSocketEventHandler eventHandler = null;
	
	private volatile boolean connected = false;
	
	private SSLSocket socket = null;
	private InputStream input = null;
	private BufferedOutputStream output = null;
	
	private WebSocketReceiver receiver = null;
	private WebSocketHandshake handshake = null;
	
	private static SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
	
	public static void setDefaultSSLSocketFactory(SSLSocketFactory socketFactory) {
		sslSocketFactory = socketFactory;
	}
	
	public WebSocketConnection(URI url)
			throws WebSocketException
	{
		this(url, null);
	}
	

	public WebSocketConnection(URI url, String protocol)
			throws WebSocketException
	{
		this.url = url;
		handshake = new WebSocketHandshake(url, protocol);
	}
	

	public void setEventHandler(WebSocketEventHandler eventHandler)
	{
		this.eventHandler = eventHandler;
	}
	
	
	public WebSocketEventHandler getEventHandler()
	{
		return this.eventHandler;
	}
	

	public void connect()
			throws WebSocketException
	{
		try {
			if (connected) {
				throw new WebSocketException("already connected");
			}
			
			socket = createSocket();
			input = socket.getInputStream();
			output = new BufferedOutputStream(socket.getOutputStream());
			
			output.write(handshake.getHandshake());
			output.flush();
						
			boolean handshakeComplete = false;
			boolean header = true;
			int len = 2000;
			byte[] buffer = new byte[len];
			int pos = 0;
			ArrayList<String> handshakeLines = new ArrayList<String>();
			
			byte[] serverResponse = new byte[16];
			
			while (!handshakeComplete) {
				int b = input.read();
				buffer[pos] = (byte) b;
				pos += 1;
				
				if (!header) {
					serverResponse[pos-1] = (byte)b;
					if (pos == 16) {
						handshakeComplete = true;
					}
				}
				else if (buffer[pos-1] == 0x0A && buffer[pos-2] == 0x0D) {
					String line = new String(buffer, "ISO-8859-1");
					if (line.trim().equals("")) {
						header = false;
					}
					else {
						handshakeLines.add(line.trim());
					}
					
					buffer = new byte[len];
					pos = 0;
				}
			}
			
			handshake.verifyServerStatusLine(handshakeLines.get(0));
			handshake.verifyServerResponse(serverResponse);
			
			handshakeLines.remove(0);
			
			HashMap<String, String> headers = new HashMap<String, String>();
			for (String line : handshakeLines) {
				String[] keyValue = line.split(": ", 2);
				headers.put(keyValue[0], keyValue[1]);
			}
			handshake.verifyServerHandshakeHeaders(headers);
			
			receiver = new WebSocketReceiver(input, this);
			receiver.start();
			connected = true;
			eventHandler.onOpen();
		}
		catch (WebSocketException wse) {
			throw wse;
		}
		catch (IOException ioe) {
			throw new WebSocketException("error while connecting: " + ioe.getMessage(), ioe);
		}
	}
	

	public synchronized void send(String data)
			throws WebSocketException
	{
		if (!connected) {
			throw new WebSocketException("error while sending text data: not connected");
		}
		
		try {
			output.write(0x00);
			output.write(data.getBytes(("ISO-8859-1")));
			output.write(0xff);
			output.flush();
		}
		catch (UnsupportedEncodingException uee) {
			throw new WebSocketException("error while sending text data: unsupported encoding", uee);
		}
		catch (IOException ioe) {
			throw new WebSocketException("error while sending text data", ioe);
		}
	}

	
	public void handleReceiverError()
	{
		try {
			if (connected) {
				close();
			}
		}
		catch (WebSocketException wse) {
			wse.printStackTrace();
		}
	}
	

	public synchronized void close()
		throws WebSocketException
	{
		if (!connected) {
			return;
		}
		
		sendCloseHandshake();
				
		if (receiver.isRunning()) {
			receiver.stopit();
		}
		
		closeStreams();

		eventHandler.onClose();
	}
	
	
	private synchronized void sendCloseHandshake()
		throws WebSocketException
	{
		if (!connected) {
			throw new WebSocketException("error while sending close handshake: not connected");
		}
		
		try {
			output.write(0xff);
			output.write(0x00);
			output.flush();
		}
		catch (IOException ioe) {
			throw new WebSocketException("error while sending close handshake", ioe);
		}

		connected = false;
	}
	

	private SSLSocket createSocket()
			throws WebSocketException
	{
		String scheme = url.getScheme();
		String host = url.getHost();
		int port = url.getPort();
		//modified to use SSLSocket, it will never enter into the first case.
		SSLSocket socket = null;
		
		if (scheme != null && scheme.equals("ws")) {
			if (port == -1) {
				port = 80;
			}
			try {
				socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
			    String[] suites = socket.getSupportedCipherSuites();
			    socket.setEnabledCipherSuites(suites);
			}
			catch (UnknownHostException uhe) {
				throw new WebSocketException("unknown host: " + host, uhe);
			}
			catch (IOException ioe) {
				throw new WebSocketException("error while creating socket to " + url, ioe);
			}
		}
		else if (scheme != null && scheme.equals("wss")) {
			if (port == -1) {
				port = 443;
			}
			try {
				socket = (SSLSocket) sslSocketFactory.createSocket(host, port);
				 String[] suites = socket.getSupportedCipherSuites();
				 socket.setEnabledCipherSuites(suites);
			}
			catch (UnknownHostException uhe) {
				throw new WebSocketException("unknown host: " + host, uhe);
			}
			catch (IOException ioe) {
				throw new WebSocketException("error while creating secure socket to " + url, ioe);
			}
		}
		else {
			throw new WebSocketException("unsupported protocol: " + scheme);
		}
		
		return socket;
	}
	
	
	private void closeStreams()
		throws WebSocketException
	{
		try {
			input.close();
			output.close();
			socket.close();
		}
		catch (IOException ioe) {
			throw new WebSocketException("error while closing websocket connection: ", ioe);
		}
	}
}
