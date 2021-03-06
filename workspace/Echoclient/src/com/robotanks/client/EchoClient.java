package com.robotanks.client;
import java.net.*; 
import java.io.*; 

// A client for our multithreaded EchoServer. 
public class EchoClient 
{ 
    public static void main(String[] args) 
    { 
        // First parameter has to be machine name 
        if(args.length == 0) 
        { 
            System.out.println("Usage : EchoClient <serverName>"); 
            return; 
        } 
         
        Socket s = null; 
         
        // Create the socket connection to the EchoServer. 
        try 
        { 
            s = new Socket(args[0], 1234); 
        }         
        catch(UnknownHostException uhe) 
        { 
            // Host unreachable 
            System.out.println("Unknown Host :" + args[0]); 
            s = null; 
        } 
        catch(IOException ioe) 
        { 
            // Cannot connect to port on given host 
            System.out.println("Cant connect to server at 1234. Make sure it is running."); 
            s = null; 
        } 
         
        if(s == null) 
            System.exit(-1); 
         
        BufferedReader in = null; 
        PrintWriter out = null; 
         
        try 
        { 
            // Create the streams to send and receive information 
            in = new BufferedReader(new InputStreamReader(s.getInputStream())); 
            out = new PrintWriter(new OutputStreamWriter(s.getOutputStream())); 
             
            // Since this is the client, we will initiate the talking. 
            // Send a string. 
            out.println("Hello"); 
            out.flush(); 
            // receive the reply. 
            System.out.println("Server Says : " + in.readLine()); 
             
            // Send a string. 
            out.println("request"); 
            out.flush(); 
            // receive a reply. 
            System.out.println("Server Says : " + in.readLine()); 
             
            // Send a string. 
            out.println("request"); 
            out.flush(); 
            // receive a reply. 
            System.out.println("Server Says : " + in.readLine()); 
             
            // Send a string. 
            out.println("request"); 
            out.flush(); 
            // receive a reply. 
            System.out.println("Server Says : " + in.readLine()); 

            // Send a string. 
            out.println("request"); 
            out.flush(); 
            // receive a reply. 
            System.out.println("Server Says : " + in.readLine()); 
             
            // Send the special string to tell server to quit. 
           // out.println("Quit"); 
//          /  out.flush(); 
        } 
        catch(IOException ioe) 
        { 
            System.out.println("Exception during communication. Server probably closed connection."); 
        } 
        finally 
        { 
            try 
            { 
                // Closee(); 
                in.close(); 
                out.close(); 
                // Close the socket before quitting 
                s.close(); 
            } 
            catch(Exception e) 
            { 
                e.printStackTrace(); 
            }                 
        }         
    } 
} 
