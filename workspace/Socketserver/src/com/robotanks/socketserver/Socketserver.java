package com.robotanks.socketserver;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientTokenListener;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketPlugIn;
import org.jwebsocket.api.WebSocketPlugInChain;
import org.jwebsocket.config.JWebSocketClientConstants;
import org.jwebsocket.config.JWebSocketCommonConstants;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.kit.CloseReason;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.token.Token;

public class Socketserver implements WebSocketServerTokenListener {

		  private final Logger log = Logging.getLogger(Socketserver.class);
		  Socket s = null; 
	      BufferedReader in = null; 
	      PrintWriter out = null; 

	      private static String NS_SAMPLE = JWebSocketClientConstants.NS_BASE + ".plugins.socketserver";
	      private static String SAMPLE_VAR = NS_SAMPLE + ".started";

	      public Socketserver () {
	    	  if (log.isDebugEnabled()) {
	    	      log.debug("Instantiating sample plug-in...");
	    	    }
	      }
		  public void processOpened(WebSocketServerEvent aEvent) {
		    log.info("Client '" + aEvent.getSessionId() + "' connected.");
		    try 
	        { 
	            s = new Socket("127.0.0.1", 1234); 
	        }         
	        catch(UnknownHostException uhe) 
	        { 
	            // Host unreachable 
	            System.out.println("Unknown Host"); 
	            s = null; 
	        } 
	        catch(IOException ioe) 
	        { 
	            // Cannot connect to port on given host 
	            System.out.println("Cant connect to server at 1234. Make sure it is running."); 
	            s = null; 
	        } 
	        try 
	        { 
	            // Create the streams to send and receive information 
	            in = new BufferedReader(new InputStreamReader(s.getInputStream())); 
	            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream())); 
	             
	            // Since this is the client, we will initiate the talking. 
	            // Send a string. 
	            out.println("Hello"); 
	            out.flush(); 
	        }
	        catch(IOException ioe) {
	            System.out.println("Cant connect to server at 1234. Make sure it is running."); 

	        }
		  }

		  public void processClosed(WebSocketServerEvent aEvent) {
		    log.info("Client '" + aEvent.getSessionId() + "' disconnected.");
		  }

		

		public void processPacket(WebSocketServerEvent arg0,
				WebSocketPacket arg1) {
				String message=null;
			
				log.info("Client '" + arg0.getSession().getSessionId() + "'.");
				
				
				
				//condition to see if is needed packet
				if(true) {
					 // Send a string. 
		            out.println("request"); 
		            out.flush(); 
		            // receive a reply. 
		            
		            try {
						message=in.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
			            log.info("Well, an error has occured"); 
					}
		            log.info("Server Says : " + message); 
					
				}
				/*
				arg2.setString(arg0, arg1)
			    // here you can interpret the token type sent from the client according to your needs.
			    String lNS = aToken.getNS();
			    String lType = aToken.getType();

			    // check if token has a type and a matching namespace
			    if (lType != null && "my.namespace".equals(lNS)) {
			      // create a response token
			    /* Token lResponse = aEvent.createResponse(aToken);
			      if ("getInfo".equals(lType)) {
			        // if type is "getInfo" return some server information
			        lResponse.put("vendor", JWebSocketCommonConstants.VENDOR);
			        lResponse.put("version", JWebSocketClientConstants.VERSION_STR);
			        lResponse.put("copyright", JWebSocketCommonConstants.COPYRIGHT);
			        lResponse.put("license", JWebSocketCommonConstants.LICENSE);
			      } else {
			        // if unknown type in this namespace, return corresponding error message
			        lResponse.put("code", -1);
			        lResponse.put("msg", "Token type '" + lType + "' not supported in namespace '" + lNS + "'.");
			      }
			      aEvent.sendToken(lResponse);}*/
		}

		
		public void processToken(WebSocketServerTokenEvent arg0, Token arg1) {
			// TODO Auto-generated method stub
			log.info("NS '" + arg1.getNS() + "'.");

			
		}



}
