package pcControl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import pcControl.network.SocketClient;

public class References {
	public static String mainVersion = "1.0.0";
	public static String patchVerion = "3";
	
//	private int socketPort = 60000;
	
	public static SocketClient sender = null;
	
	public static Socket PlSocket = null;
	public static ServerSocket PlServerSocket = null;
	public static int PlSocketPort = -1;
	public static PrintWriter PlOutSocket = null;
	public static BufferedReader PlInSocket = null;
	
	public static Socket socket = null;
	public static ServerSocket serverSocket = null;
	public static int serverSocketPort = -1;
	public static PrintWriter outSocket = null;
	public static BufferedReader inSocket = null;
	public static OutputStream outputStream = null;
	public static Thread arThread = null;
	
	public static boolean currentSocketVerified = false;
	public static String password = "0000";
	
	public static boolean connected = false;
	
	public static boolean STOPPING = false;
	
	public static long lastArInSocketActivity = -1L;
	
	public static File arLocation;
	
	public static boolean subProcessNeedToBeOnline = false;
	
	//settings
//	public static boolean printHeartbeats = true; //false, debug: true
//	public static boolean printFileDataSendingMessage = true;
//	public static boolean printFileDataSendingList = false;
//	public static boolean printFileDataSendingSilent = false;
//	public static boolean fixEmptyLines = true;
//	public static boolean updateConfigOnNewVersion = false; // false, debug: false
//	public static boolean printSocketException = true; // false, debug: true
//	public static boolean realShutdown = false; // true, debug: false
//	public static boolean printFirstConnectMessage = false; // false, debug: true
//	public static boolean printMiscellaneousDebug = false; // false, debug: true
//	public static int hearbeatThreshold = 45000;
	
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
	
	public volatile static Process currentRunningSubProcess = null;
	public static BufferedReader currentRunningSubProcessBufferedReader = null;
	public static PrintWriter currentRunningSubProcessPrintWriter = null;
	public static File appExecutionDir = null;
	public static File configsDir = null;
	
	public static ArrayList<String> foldersAllowedToSee = null;
	public static ArrayList<String> foldersAndFilesAllowedToExecute = null;
	//public static ArrayList<String> foldersAndFilesAllowedToOnlyExecute = null;
	
	public static Logger log4j = null;
	
	public static boolean folderSizeGotLimit = false;
	public static long folderSizeHowManyChecked = 0;
}
