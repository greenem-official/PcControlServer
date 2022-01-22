package pcControl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class SocketClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
//    private int port = -1;

//    @Deprecated
    public void startConnection(String ip, int port) { //DEPRECATED
        try {
			socket = new Socket(ip, port);
        } catch (IOException e) {
			e.printStackTrace();
		}
        GeneralLogger.log(socket);
        if(socket!=null) {
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
    }
    
    public void startConnection(BufferedReader in, PrintWriter out) { //DEPRECATED
        this.in = in;
        this.out = out;
    }

    public void sendMessage(String msg) {
    	msg = new String(msg.getBytes(Charset.forName("Cp1251")));
    	//System.out.println(msg);
    	//GeneralLogger.log(out.toString() + " | " + in.toString());
    	if(out==null) {
    		GeneralLogger.log("null socket");
    	}
    	if(out==null) {
    		GeneralLogger.log("The app is not online!");
    		return;
    	}
    	out.println(msg);
    	boolean toLog = true;
    	if(!References.printFileDataSendingList && (msg.startsWith("$system.files.fileslist") || msg.startsWith("$system.files.folderslist") || msg.startsWith("$system.files.nonfolderslist"))) {
    		toLog = false;
    	}
    	if(References.printFileDataSendingMessage && (msg.startsWith("$system.files.getlocation") || msg.startsWith("$system.files.getpathseparator"))) {
    		toLog = false;
    	}
    	if(msg.trim().equals("")) {
    		toLog = false;
    	}
    	if(References.fixEmptyLines) {
    		msg = msg.replaceAll("\n\n", "\n");
    	}
    	if(toLog) {
    		GeneralLogger.log("RSC sending: " + msg);
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
