package com.android.locaalton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.openexi.proc.common.GrammarOptions;
import org.openexi.proc.grammars.GrammarCache;
import org.openexi.sax.Transmogrifier;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

public class Message {
	
	public String writeXml(){
	    XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "v2gci_d:V2G_Message");
	        serializer.attribute("", "xmlns:v2gci_b", "urn:iso:15118:2:2010:MsgBody");
	        serializer.attribute("", "xmlns:xmlsig", "http://www.w3.org/2000/09/xmldsig#");
	        serializer.attribute("", "xmlns:v2gci_d", "urn:iso:15118:2:2010:MsgDef");
	        serializer.attribute("", "xmlns:v2gci_t", "urn:iso:15118:2:2010:MsgDataTypes");
	        serializer.attribute("", "xmlns:v2gci_h", "urn:iso:15118:2:2010:MsgHeader");
	        // Header
	        serializer.startTag("", "v2gci_d:Header");
	        serializer.startTag("", "v2gci_h:SessionInformation");
	        serializer.startTag("", "v2gci_t:SessionID");
	        serializer.text("0000000000000000");
	        serializer.endTag("", "v2gci_t:SessionID");
	        serializer.endTag("", "v2gci_h:SessionInformation");
	        serializer.endTag("", "v2gci_d:Header");
	        // Body
	        serializer.startTag("", "v2gci_d:Body");
	        serializer.startTag("", "v2gci_b:SessionSetupReq");
	        serializer.startTag("", "v2gci_b:EVCCID");
	        serializer.text("000000000000000F");
	        serializer.endTag("", "v2gci_b:EVCCID");
	        serializer.endTag("", "v2gci_b:SessionSetupReq");
	        serializer.endTag("", "v2gci_d:Body");
	        serializer.endTag("", "v2gci_d:V2G_Message");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 
	} 
	
	public String encodeEXI(String inputXML){
//	    FileInputStream in = null;
//	    FileOutputStream out = null;
	    GrammarCache grammarCache;
	
	//All EXI options can be stored in a single short integer. DEFAULT_OPTIONS=2.
	    short options = GrammarOptions.DEFAULT_OPTIONS;
	    try {
	
	//Encoding always requires the same steps.
	        
	//1. Instantiate a Transmogrifier
	        Transmogrifier transmogrifier = new Transmogrifier();
	        
	//3. Set the schema and EXI options in the Grammar Cache. This example uses default options and no schema.
	        grammarCache = new GrammarCache(null, options);

	//4. Set the configuration options in the Transmogrifier. Later examples will show more possible settings.
	        transmogrifier.setEXISchema(grammarCache);
	        
	//5. Set the output stream.
	        ByteArrayOutputStream result = new ByteArrayOutputStream();
	        transmogrifier.setOutputStream(result);
	
	//6. Encode the input stream.
	        
	        byte inputBytes[] = inputXML.getBytes();
	        ByteArrayInputStream in = new ByteArrayInputStream(inputBytes); 

	        transmogrifier.encode(new InputSource(in));
	        
	        return result.toString();
	    }catch(Exception e){
	    	System.out.println("Encoding Exception: "+e);
	    	return "encoding error";
	    }
	}
	
	public String createXML(double latitude, double longitude){
		return "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><v2gci_d:V2G_Message xmlns:v2gci_b='urn:iso:15118:2:2010:MsgBody' xmlns:xmlsig='http://www.w3.org/2000/09/xmldsig#' xmlns:v2gci_d='urn:iso:15118:2:2010:MsgDef' xmlns:v2gci_t='urn:iso:15118:2:2010:MsgDataTypes' xmlns:v2gci_h='urn:iso:15118:2:2010:MsgHeader'><v2gci_d:Header><v2gci_h:SessionInformation><v2gci_t:SessionID>"+latitude+"</v2gci_t:SessionID></v2gci_h:SessionInformation></v2gci_d:Header><v2gci_d:Body><v2gci_b:SessionSetupReq><v2gci_b:EVCCID>"+longitude+"</v2gci_b:EVCCID></v2gci_b:SessionSetupReq></v2gci_d:Body></v2gci_d:V2G_Message>";
	}
	
	public String createXmlHeader(String headerContent){
		return "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><v2gci_d:V2G_Message xmlns:v2gci_b='urn:iso:15118:2:2010:MsgBody' xmlns:xmlsig='http://www.w3.org/2000/09/xmldsig#' xmlns:v2gci_d='urn:iso:15118:2:2010:MsgDef' xmlns:v2gci_t='urn:iso:15118:2:2010:MsgDataTypes' xmlns:v2gci_h='urn:iso:15118:2:2010:MsgHeader'><v2gci_d:Header>"+headerContent+"</v2gci_d:Header>";
	}
	
	public String createXmlBody(String bodyContent){
		return "<v2gci_d:Body>"+bodyContent+"</v2gci_d:Body></v2gci_d:V2G_Message>";
	}
	
	public String addContentToHeader(double latitude){
		return "<v2gci_h:SessionInformation><v2gci_t:SessionID>"+latitude+"</v2gci_t:SessionID></v2gci_h:SessionInformation>";
	}
	
	public String addContentToBody(double longitude){
		return "<v2gci_b:SessionSetupReq><v2gci_b:EVCCID>"+longitude+"</v2gci_b:EVCCID></v2gci_b:SessionSetupReq>";
	}
	
	
}
