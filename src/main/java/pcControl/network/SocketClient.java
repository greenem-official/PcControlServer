package pcControl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;

import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class SocketClient {
	private static Logger log = References.log4j;
	
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private InputStream inStream;
    private OutputStream outputStream;
//    private int port = -1;

//    @Deprecated
    public void startConnection(String ip, int port) { //DEPRECATED
        try {
			socket = new Socket(ip, port);
        } catch (IOException e) {
			e.printStackTrace();
		}
        //log.info(socket);
        if(socket!=null) {
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				inStream = socket.getInputStream();
				in = new BufferedReader(new InputStreamReader(inStream, "windows-1251"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				References.PlInSocket = in;
				References.PlOutSocket = out;
				References.PlSocket = socket;
				References.PlSocketPort = port;
//				this.port = port;
			}
        }
        References.sender = this;
    }
    
//    public void startConnection(BufferedReader in, PrintWriter out) { //DEPRECATED
//        this.in = in;
//        this.out = out;
//        References.sender = this;
//    }
    
    public void startConnection(BufferedReader in, PrintWriter out, OutputStream outputStream) {
        this.in = in;
        this.out = out;
        this.outputStream = outputStream;
        References.sender = this;
    }

    public void sendMessage(String msg) {
    	//msg = new String(msg.getBytes(Charset.forName("Cp1251"))); // Cp1251 windows-1251
    	
    	//msg = new String(msg.getBytes(Charset.forName("windows-1251"))); // Cp1251 windows-1251
    	
    	//System.out.println(msg);
    	//log.info(out.toString() + " | " + in.toString());
    	
    	// Check
    	if(out==null) {
    		log.info("null socket");
    	}
    	if(out==null) {
    		log.info("The app is not online!");
    		return;
    	}
    	
    	// Flush, "writeln"
    	msg += "\n";
    	
    	// Send
    	try {
			outputStream.write(msg.getBytes(Charset.forName("windows-1251")));
    	} catch (SocketException e) {
    		// closed I think
		} catch (IOException e) {
			e.printStackTrace();
		}
    	try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	//out.println(msg);
    	
    	msg = msg.substring(0, msg.length()-1);
    	
    	// Log
    	boolean toLog = true;
    	//String customText = "";
    	if(!References.printFileDataSendingList && (msg.startsWith("$system.files.fileslist") || msg.startsWith("$system.files.folderslist") || msg.startsWith("$system.files.nonfolderslist"))) {
    		toLog = false;
    	}
    	if(References.printFileDataSendingMessage && (msg.startsWith("$system.files.getlocation") || msg.startsWith("$system.files.getpathseparator"))) {
    		toLog = false;
    	}
    	if(msg.startsWith("$system.getinfo.tasklist.result.text=")) {
    		toLog = false;
    	}
    	if(msg.trim().equals("")) {
    		toLog = false;
    	}
    	if(References.fixEmptyLines) {
    		msg = msg.replaceAll("\n\n+", "\n");
    	}
    	if(msg.startsWith("$system.files.dirinfo.result.info=")) {
    		msg = "[Dir info]"; // could do log.info(...); toLog = false;
    	}
    	if(toLog) {
    		log.info("PC sending: " + msg);
    	}
//      String resp = "";
//		try {
//			resp = in.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }

    public void stopConnection() {
        try {
        	in.close();
        	out.close();
        	socket.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
}
