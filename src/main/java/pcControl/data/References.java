package pcControl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class References {
	public static String mainVersion = "1.0.0";
	public static String patchVerion = "2";
	
//	private int socketPort = 60000;
	
	public static Socket PlSocket = null;
	public static ServerSocket PlServerSocket = null;
	public static int PlSocketPort = -1;
	public static PrintWriter PlOutSocket = null;
	public static BufferedReader PlInSocket = null;
	
	public static Socket ArSocket = null;
	public static ServerSocket ArServerSocket = null;
	public static int ArSocketPort = -1;
	public static PrintWriter ArOutSocket = null;
	public static BufferedReader ArInSocket = null;
	
	public static boolean currentSocketVerified = false;
	public static String password = "0000";
	
	public static boolean STOPPING = false;
	
	public static long lastArInSocketActivity = -1L;
	
	public static File arLocation;
	
	public static boolean subProcessNeedToBeOnline = false;
	
	//settings
	public static boolean printHeartbeats = false;
	public static boolean printFileDataSendingMessage = true;
	public static boolean printFileDataSendingList = false;
	public static boolean printFileDataSendingSilent = false;
	public static boolean fixEmptyLines = true;
	public static boolean updateConfigOnNewVersion = true; // false
	
	public static Process currentRunningSubProcess = null;
	public static BufferedReader currentRunningSubProcessBufferedReader = null;
	public static File appExecutionDir = null;
	
	public static ArrayList<String> foldersAllowedToSee = null;
	public static ArrayList<String> foldersAndFilesAllowedToExecute = null;
	
	public static Logger log4j = null;
}
