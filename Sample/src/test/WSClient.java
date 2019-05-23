package test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class WSClient {

	
	
	
	
	
	public static void startValidationService(String requestId, String subRequestId, String accountNumber, String mSISDN, String serial1, List<DataModel> dataCollection) throws IOException {

        String responseString = "";
        String outputString = "";
        Random  rand = new Random();

        String wsURL = "http://AS2444DXBDB05.etisalat.corp.ae:8181/ws/ValidationServiceWorkQueue";

        URL url = new URL(wsURL);

        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        
        if(dataCollection.size() > 0) {
        	String singleRow = null;
        	for(DataModel data: dataCollection) {
        		
        		singleRow = "		    <urn:row>"
        				+ "                    <urn:requestId>"+data.getRequestId()+"</urn:requestId>"
        				+ "                    <urn:subRequestId>"+data.getSubRequestId()+"</urn:subRequestId>"
        				+ "                    <urn:accountNumber>"+data.getAccountNumber()+"</urn:accountNumber>"
        				+ "                    <urn:msisdn>"+data.getMsisdn()+"</urn:msisdn>"
        				+ "                    <urn:serial1>"+data.getSerial1()+"</urn:serial1>"
        				+ " 				   <urn:tagName>"+data.getTagName()+ "</urn:tagName>"
        				+ " 				   <urn:testPlan>"+data.getTestPlan()+ "</urn:testPlan>"
        				+ " 				   <urn:channel>"+data.getChannel()+ "</urn:channel>"
        				+ " 				   <urn:testingType>"+data.getTestingType()+ "</urn:testingType>"
        				+ "             </urn:row>";
        		sb.append(singleRow);  
        	}
        }
        
        System.out.println("SB ================= "+sb);

        String xmlInput =
			"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:blueprism:webservice:validationservice\">"
			+ "   	<soapenv:Header/>"
			+ "			<soapenv:Body>"
			+ "			<urn:ValidationServiceWorkQueue soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
				+ "		    <requestId xsi:type=\"xsd:string\">"+requestId+"</requestId>"
				+ "			<subRequestId xsi:type=\"xsd:string\">"+subRequestId+"</subRequestId>"
				+ "			<accountNumber xsi:type=\"xsd:string\">"+accountNumber+"</accountNumber>"
				+ "			<msisdn xsi:type=\"xsd:string\">"+mSISDN+"</msisdn>"
				+ "			<serial1 xsi:type=\"xsd:string\">"+serial1+"</serial1>"
				+ "         <urn:QueueCollection>"
				+ "				<!--Zero or more repetitions:-->"
				+ sb 
				+ "         </urn:QueueCollection>"
				+ "		</urn:ValidationServiceWorkQueue>"
			+ "		</soapenv:Body>"
			+ "	</soapenv:Envelope>";
        

        System.out.println("Request: " + xmlInput);

        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();

        String SOAPAction = "";

        httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("GET");
        
        String userpass = "admin" + ":" + "Etisalat@20195";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        httpConn.setRequestProperty ("Authorization", basicAuth);
    
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();

        out.write(b);
        out.close();

        System.out.println("Response code: " + httpConn.getResponseCode());
        System.out.println("Response message: " + httpConn.getResponseMessage());
        
        if (!(httpConn.getResponseCode() == 200)) {
             System.out.println("Response code: " + httpConn.getResponseCode() + ", Response message: " + httpConn.getResponseMessage());
        }

        //Read the response.
        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        //Write the SOAP message response to a String.
        while ((responseString = br.readLine()) != null) {
             outputString = outputString + responseString;
        }
        String formattedResponse = outputString;
        System.out.println(formattedResponse);
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        
        try (PrintWriter outPut = new PrintWriter("E:\\XMLResponseFiles\\"+fileName+".xml")) {
        	outPut.println(formattedResponse);
        }
        
  }
	
	
	public static void serviceActive(Integer p_input1, Integer p_input2) throws IOException {

        String responseString = "";
        String outputString = "";

        String wsURL = "http://AS2444DXBDB05.etisalat.corp.ae:8181/ws/ProcessWS";

        URL url = new URL(wsURL);

        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection)connection;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        String xmlInput =
 
			   "  <soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:blueprism:webservice:processws\">\n" +
			   "     <soapenv:Header/>\n" +
			   "    <soapenv:Body>\n" +
			   "        <urn:ProcessWS soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
			   "           <param1 xsi:type=\"xsd:decimal\">" + p_input1 + "</param1>\n" +
			   "           <param2 xsi:type=\"xsd:decimal\">" + p_input2 + "</param2>\n" +
			   "        </urn:ProcessWS>\n" +
			   "     </soapenv:Body>\n" +
			   "  </soapenv:Envelope>";
        
        
        System.out.println("Request: " + xmlInput);

        byte[] buffer = new byte[xmlInput.length()];
        buffer = xmlInput.getBytes();
        bout.write(buffer);
        byte[] b = bout.toByteArray();

        String SOAPAction = "";

        httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("SOAPAction", SOAPAction);
        httpConn.setRequestMethod("GET");
        
        String userpass = "admin" + ":" + "Etisalat@2019";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        httpConn.setRequestProperty ("Authorization", basicAuth);
    
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        OutputStream out = httpConn.getOutputStream();

        out.write(b);
        out.close();

        System.out.println("Response code: " + httpConn.getResponseCode());
        System.out.println("Response message: " + httpConn.getResponseMessage());
        
        if (!(httpConn.getResponseCode() == 200)) {
             System.out.println("Response code: " + httpConn.getResponseCode() + ", Response message: " + httpConn.getResponseMessage());
        }

        //Read the response.
        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        //Write the SOAP message response to a String.
        while ((responseString = br.readLine()) != null) {
             outputString = outputString + responseString;
        }
        String formattedResponse = outputString;
        System.out.println(formattedResponse);
        
  }


}
