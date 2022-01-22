package pcControl.deprecated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class SocketRunnable implements Runnable {
	
	
	private static volatile SocketRunnable INSTANCE;
	
	public static SocketRunnable getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SocketRunnable();
		}
		return INSTANCE;
	}

	
	@Override
	public void run() {
		try {
			//ServerSocket serverSocket = new ServerSocket(References.getInstance().portServerSide);
			References.PlServerSocket = new ServerSocket(References.PlSocketPort);
			//can be a loop here
			References.PlSocket = References.PlServerSocket.accept();
//			References.PlServerSocket.close();
			References.PlOutSocket = new PrintWriter(References.PlSocket.getOutputStream(), true);
			References.PlInSocket = new BufferedReader(new InputStreamReader(References.PlSocket.getInputStream()));
			
			String inputLine;
			while (true) {
				inputLine = References.PlInSocket.readLine();
				if(inputLine != null && inputLine != ""){
//					if (inputLine.equalsIgnoreCase("servermanager stop serversocket")) {
//						References.getInstance().outSocket.println("Socket Closed");
//						break;
//					}
//					References.outSocket.println("message back to PX");                                                //useful
					GeneralLogger.log("SM got: " + inputLine + "\n");
				}
//				if(References.getInstance().clientSocket.isClosed()) {
//					GeneralLogger.log("closed");
//				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
