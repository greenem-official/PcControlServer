package pcControl.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

public abstract class ConnectionPerDevice implements Runnable {
	public Socket socket;
	public PrintWriter outSocket;
	public BufferedReader inSocket;
	
	public static SocketClient sender = null;
		
	public static int socketPort = -1;
	public static OutputStream outputStream = null;
	public static Thread arThread = null;
	
	public static boolean currentSocketVerified = false;
	
	public static boolean connected = false;
	public static long lastArInSocketActivity = -1L;
	public static File arLocation;
	
	public static boolean subProcessNeedToBeOnline = false;
	
	//settings	
	public static boolean printHeartbeats = false; //false, debug: true
	public static boolean printFileDataSendingMessage = true;
	public static boolean printFileDataSendingList = false;
	public static boolean printFileDataSendingSilent = false;
	public static boolean fixEmptyLines = true;
	public static boolean updateConfigOnNewVersion = false; // false, debug: false
	public static boolean printSocketException = false; // false, debug: true
	public static boolean realShutdown = false; // true, debug: false
	public static boolean printFirstConnectMessage = false; // false, debug: true
	public static boolean printMiscellaneousDebug = false; // false, debug: true
	public static int hearbeatThreshold = 45000;
	
	//process
	public volatile static Process currentRunningSubProcess = null;
	public static BufferedReader currentRunningSubProcessBufferedReader = null;
	public static PrintWriter currentRunningSubProcessPrintWriter = null;
}
