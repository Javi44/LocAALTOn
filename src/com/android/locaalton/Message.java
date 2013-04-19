package com.android.locaalton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;
import org.openexi.sax.Transmogrifier;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class Message
{
	// / None of the response methods are used by the client.
	// / They are created so the message can be encode with EXI and
	// / result can be set manually in the server as the response

	public String sessionSetupRequest()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("0000h");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:SessionSetupReq");
			serializer.startTag("", "v2gci_b:EVCCID");
			serializer.text("2323h");
			serializer.endTag("", "v2gci_b:EVCCID");
			serializer.endTag("", "v2gci_b:SessionSetupReq");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}

	public String sessionSetupResponse()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030h");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:SessionSetupRes");
			serializer.startTag("", "v2gci_b:ResponseCode");
			serializer.text("OK");
			serializer.endTag("", "v2gci_b:ResponseCode");
			serializer.startTag("", "v2gci_b:EVESID");
			serializer.text("3232h");
			serializer.endTag("", "v2gci_b:EVESID");
			serializer.startTag("", "v2gci_b:DateTimeNow");
			serializer.text("2013.03.01 15:00:00");
			serializer.endTag("", "v2gci_b:DateTimeNow");
			serializer.endTag("", "v2gci_b:SessionSetupRes");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String serviceDiscoveryRequest()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:ServiceDiscoveryReq");
			serializer.startTag("", "v2gci_b:ServiceScope");
			serializer.text("String");
			serializer.endTag("", "v2gci_b:ServiceScope");
			serializer.startTag("", "v2gci_b:ServiceCategory");
			serializer.text("Load balancer");
			serializer.endTag("", "v2gci_b:ServiceCategory");
			serializer.endTag("", "v2gci_b:ServiceDiscoveryReq");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String serviceDiscoveryResponse()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:ServiceDiscoveryRes");
			serializer.startTag("", "v2gci_b:ResponseCode");
			serializer.text("OK");
			serializer.endTag("", "v2gci_b:ResponseCode");
			serializer.startTag("", "v2gci_b:LoadBalanceService");
			serializer.startTag("", "v2gci_b:ServiceTag");
			serializer.startTag("", "v2gci_b:ServiceID");
			serializer.text("2");
			serializer.endTag("", "v2gci_b:ServiceID");
			serializer.startTag("", "v2gci_b:ServiceName");
			serializer.text("Load balancer updates");
			serializer.endTag("", "v2gci_b:ServiceName");
			serializer.startTag("", "v2gci_b:ServiceScope");
			serializer.text("string");
			serializer.endTag("", "v2gci_b:ServiceScope");
			serializer.endTag("", "v2gci_b:ServiceTag");
			serializer.startTag("", "v2gci_b:FreeService");
			serializer.text("true");
			serializer.endTag("", "v2gci_b:FreeService");
			serializer.endTag("", "v2gci_b:LoadBalanceService");
			serializer.startTag("", "v2gci_b:ServiceList");
			serializer.text("Optional other services");
			serializer.endTag("", "v2gci_b:ServiceList");
			serializer.endTag("", "v2gci_b:ServiceDiscoveryRes");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String chargingDataRequest()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:Timestamp");
			serializer.text("234234567");
			serializer.endTag("", "v2gci_b:Timestamp");
			serializer.startTag("", "v2gci_b:Destination");
			serializer.text("356546456,32324534");
			serializer.endTag("", "v2gci_b:Destination");
			serializer.startTag("", "v2gci_b:ExpectedArrivalTime");
			serializer.text("5234218356");
			serializer.endTag("", "v2gci_b:ExpectedArrivalTime");
			// serializer.startTag("", "v2gci_b:ExpectedDepartureTime");
			// serializer.text("5442445679");
			// serializer.endTag("", "v2gci_b:ExpectedDepartureTime");
			serializer.startTag("", "v2gci_b:ExpectedStateOfCharge");
			serializer.text("54");
			serializer.endTag("", "v2gci_b:ExpectedStateOfCharge");
			serializer.startTag("", "v2gci_b:MayUsePublicTransport");
			serializer.text("no");
			serializer.endTag("", "v2gci_b:MayUsePublicTransport");
			serializer.startTag("", "v2gci_b:ChargingRatesSupported");
			serializer.text("normal/quick");
			serializer.endTag("", "v2gci_b:ChargingRatesSupported");
			serializer.startTag("", "v2gci_b:PriceImportance");
			serializer.text("High");
			serializer.endTag("", "v2gci_b:PriceImportance");
			serializer.startTag("", "v2gci_b:WaitingImportance");
			serializer.text("High");
			serializer.endTag("", "v2gci_b:WaitingImportance");
			serializer.startTag("", "v2gci_b:WalkingImportance");
			serializer.text("Low");
			serializer.endTag("", "v2gci_b:WalkingImportance");
			serializer.startTag("", "v2gci_b:WalkingDistanceImportance");
			serializer.text("High");
			serializer.endTag("", "v2gci_b:WalkingDistanceImportance");
			serializer.startTag("", "v2gci_b:PaymentMeans");
			serializer.text("User name 8888 8888 8888 8888 777");
			serializer.endTag("", "v2gci_b:PaymentMeans");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String chargingDataResponse()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030h");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:ChargingDataRes");
			serializer.startTag("", "v2gci_b:ResponseCode");
			serializer.text("OK");
			serializer.endTag("", "v2gci_b:ResponseCode");
			serializer.endTag("", "v2gci_b:ChargingDataRes");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String sessionStopRequest()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030h");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:SessionStopReq");
			serializer.endTag("", "v2gci_b:SessionStopReq");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String sessionStopResponse()
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030h");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:SessionStopRes");
			serializer.startTag("", "v2gci_t:ResponseCode");
			serializer.text("OK");
			serializer.endTag("", "v2gci_t:ResponseCode");
			serializer.endTag("", "v2gci_b:SessionStopRes");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}

	public String location(String time, String longitude, String latitude,
			String altitude, String accuracy, String speed, String soc)
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:Location");
			serializer.startTag("", "v2gci_b:Timestamp");
			serializer.text(time);
			serializer.endTag("", "v2gci_b:Timestamp");
			serializer.startTag("", "v2gci_b:Longitude");
			serializer.text(longitude);
			serializer.endTag("", "v2gci_b:Longitude");
			serializer.startTag("", "v2gci_b:Latitude");
			serializer.text(latitude);
			serializer.endTag("", "v2gci_b:Latitude");
			serializer.startTag("", "v2gci_b:Altitude");
			serializer.text(altitude);
			serializer.endTag("", "v2gci_b:Altitude");
			serializer.startTag("", "v2gci_b:Accuracy");
			serializer.text(accuracy);
			serializer.endTag("", "v2gci_b:Accuracy");
			serializer.startTag("", "v2gci_b:Speed");
			serializer.text(speed);
			serializer.endTag("", "v2gci_b:Speed");
			serializer.startTag("", "v2gci_b:StateOfCharge");
			serializer.text(soc);
			serializer.endTag("", "v2gci_b:StateOfCharge");
			serializer.endTag("", "v2gci_b:Location");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String bundleLocations(int bundleSize, String[] times,
			String[] longitudes, String[] latitudes, String[] altitudes,
			String[] accuracies, String[] speeds, String[] socs)
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try 
		{
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "v2gci_d:V2G_Message");
			serializer.attribute("", "xmlns:v2gci_b",
								"urn:iso:15118:2:2010:MsgBody");
			serializer.attribute("", "xmlns:xmlsig",
								"http://www.w3.org/2000/09/xmldsig#");
			serializer.attribute("", "xmlns:v2gci_d",
								"urn:iso:15118:2:2010:MsgDef");
			serializer.attribute("", "xmlns:v2gci_t",
								"urn:iso:15118:2:2010:MsgDataTypes");
			serializer.attribute("", "xmlns:v2gci_h",
								"urn:iso:15118:2:2010:MsgHeader");
			// Header
			serializer.startTag("", "v2gci_d:Header");
			serializer.startTag("", "v2gci_h:SessionInformation");
			serializer.startTag("", "v2gci_t:SessionID");
			serializer.text("3030");
			serializer.endTag("", "v2gci_t:SessionID");
			serializer.startTag("", "v2gci_t:ProtocolVersion");
			serializer.text("Ver.1.1");
			serializer.endTag("", "v2gci_t:ProtocolVersion");
			serializer.endTag("", "v2gci_h:SessionInformation");
			serializer.endTag("", "v2gci_d:Header");
			// Body
			serializer.startTag("", "v2gci_d:Body");
			serializer.startTag("", "v2gci_b:LocationBundle");
			for (int i = 0; i <= bundleSize - 1; i++) {
				serializer.startTag("", "v2gci_b:Timestamp_" + i);
				serializer.text(times[i]);
				serializer.endTag("", "v2gci_b:Timestamp_" + i);
				serializer.startTag("", "v2gci_b:Longitude_" + i);
				serializer.text(longitudes[i]);
				serializer.endTag("", "v2gci_b:Longitude_" + i);
				serializer.startTag("", "v2gci_b:Latitude_" + i);
				serializer.text(latitudes[i]);
				serializer.endTag("", "v2gci_b:Latitude_" + i);
				serializer.startTag("", "v2gci_b:Altitude_" + i);
				serializer.text(altitudes[i]);
				serializer.endTag("", "v2gci_b:Altitude_" + i);
				serializer.startTag("", "v2gci_b:Accuracy_" + i);
				serializer.text(accuracies[i]);
				serializer.endTag("", "v2gci_b:Accuracy_" + i);
				serializer.startTag("", "v2gci_b:Speed_" + i);
				serializer.text(speeds[i]);
				serializer.endTag("", "v2gci_b:Speed_" + i);
				serializer.startTag("", "v2gci_b:StateOfCharge_" + i);
				serializer.text(socs[i]);
				serializer.endTag("", "v2gci_b:StateOfCharge_" + i);
			}
			serializer.endTag("", "v2gci_b:LocationBundle");
			serializer.endTag("", "v2gci_d:Body");
			serializer.endTag("", "v2gci_d:V2G_Message");
			serializer.endDocument();
			return writer.toString();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public String encodeEXI(String inputXML)
	{
		// FileInputStream in = null;
		// FileOutputStream out = null;
		GrammarCache grammarCache;
		short options = GrammarOptions.DEFAULT_OPTIONS;
		try
		{
			// Encoding always requires the same steps.
			// 1. Instantiate a Transmogrifier
			Transmogrifier transmogrifier = new Transmogrifier();
			// 2. Set the schema and EXI options in the Grammar Cache.
			grammarCache = new GrammarCache(null, options);
			// 3. Set the configuration options in the Transmogrifier.
			transmogrifier.setEXISchema(grammarCache);
			// 4. Set the output stream.
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			transmogrifier.setOutputStream(result);
			// 5. Encode the input stream.
			byte inputBytes[] = inputXML.getBytes();
			ByteArrayInputStream in = new ByteArrayInputStream(inputBytes);
			transmogrifier.encode(new InputSource(in));
			AuxFunctions aux = new AuxFunctions();
			return aux.utf8izer(result.toString());
		} 
		catch (Exception e)
		{
			System.out.println("Encoding Exception: " + e);
			return "encoding error";
		}
	}
}
