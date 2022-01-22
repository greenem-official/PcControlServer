package pcControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URISyntaxException;

import pcControl.data.References;
import pcControl.deprecated.SocketRunnable;
import pcControl.execution.GeneralStuff;
import pcControl.execution.Permissions;
import pcControl.logging.GeneralLogger;
import pcControl.network.AndroidListener;
import pcControl.network.PortTools;
import pcControl.network.SocketClient;

public class PcControlMain {

	// serves-playersCount-settings

	public static ProcessBuilder procBuilder = null;
	public static Process proc = null;
	public InputStream inp = null;
	public OutputStream output = null;
	public boolean logging = true;
	public SocketClient ArSender;

	private static volatile PcControlMain INSTANCE;

	public static PcControlMain getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PcControlMain();
		}
		return INSTANCE;
	}

	// MAIN

	public static void main(String[] args) {
		boolean startNow = false;

		GeneralLogger.log("REMOTE PC CONTROL APP - SERVER SIDE");
		GeneralLogger.log("Starting input thread");
		Thread inputThread = new Thread(new InputRunnable());

		inputThread.start();

		// getInstance().printTaskList();

		if (startNow) {
			References.PlSocketPort = PortTools.getInstance().getAvaliable();
		}
//		References.ArSocketPort = PortTools.getInstance().getAvaliable();
		References.ArSocketPort = 12345;

		if (startNow) {
			Thread socketThread = new Thread(SocketRunnable.getInstance());
			socketThread.start();
		}

		// Initialization

		Thread androidSocketThread = new Thread(AndroidListener.getInstance());

		androidSocketThread.start();

		SocketClient pluginSocketClient = null;

		if (startNow) {
			pluginSocketClient = new SocketClient();
			pluginSocketClient.startConnection("localhost", References.PlSocketPort);
		}
//		socketClient.sendMessage("message 1");

		// SocketClient androidSocketClient = new SocketClient();
		getInstance().ArSender = new SocketClient();
//		androidSocketClient.startConnection("localhost", References.ArSocketPort);

		// getInstance().test2();

		File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		References.appExecutionDir = jarDir;
		//GeneralLogger.log(References.appExecutionDir);
		
		GeneralStuff.setConstants();
		Permissions.init();
		GeneralStuff.reloadFiles();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// GeneralLogger.log("Running Shutdown Hook");
				InputRunnable.onExit();
				try {
					References.ArSocket.close();
					References.PlSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				GeneralLogger.log("Succesfully stopped");
			}
		});
	}

	private void test2() {
		int a = 16;
		String current = "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15";
		String[] array = current.split("\n");
		// String[] newArray = new String[GeneralData.maxOutputLines];
		String newString = "";

		if (array.length > a) {
			for (int i = (array.length - a); i < array.length; i++) {
				newString += array[i] + "\n";
			}
			System.out.println(newString);
		} else {
			System.out.println(current);
		}

	}

	public String getHelpText() {
		String s = "";
		s += "Available commands:\n" + "\n" + "help - for help\n" + "exit - for stopping\n" + "\n";
		return s;
	}

	public void onConsoleCommand(String s) {
		s = s.trim();
		String unknownCommand = "Unknown command or wrong args!";
		String[] separated = s.split(" ");
		// GeneralLogger.log(separated.length);
		if (separated.length == 0) {
			GeneralLogger.log("An empty command"); // test
		}
		if (separated.length == 1) {
			if (s.equalsIgnoreCase("help")) {
				GeneralLogger.log(getHelpText());
			} else if (s.equalsIgnoreCase("exit")) {
				References.STOPPING = true;
				doExit();
			} else if (s.equals("test1")) {
				try {
					GeneralLogger.log("1: " + References.ArSocket);
					GeneralLogger.log("2: " + References.ArSocket.getKeepAlive());
					GeneralLogger.log("3: " + References.ArSocket.getOOBInline());
					GeneralLogger.log("4: " + References.ArSocket.getReceiveBufferSize());
					GeneralLogger.log("5: " + References.ArSocket.getReuseAddress());
					GeneralLogger.log("6: " + References.ArSocket.getSendBufferSize());
					GeneralLogger.log("7: " + References.ArSocket.getSoLinger());
					GeneralLogger.log("8: " + References.ArSocket.getSoTimeout());
					GeneralLogger.log("9: " + References.ArSocket.getTcpNoDelay());
					GeneralLogger.log("10: " + References.ArSocket.getTrafficClass());
					GeneralLogger.log("11: " + References.ArSocket.isBound());
					GeneralLogger.log("12: " + References.ArSocket.isClosed());
					GeneralLogger.log("13: " + References.ArSocket.isConnected());
					GeneralLogger.log("14: " + References.ArSocket.isInputShutdown());
					GeneralLogger.log("15: " + References.ArSocket.isOutputShutdown());
				} catch (SocketException e) {
					e.printStackTrace();
				}
			} else {
				GeneralLogger.log("Unknown command!");
			}
		} else {
			String cmd = separated[0];
			String[] args = new String[separated.length - 1];
			for (int i = 0; i < args.length; i++) {
				args[i] = separated[i + 1];
			}
			if (cmd.equals("menu")) {

			} else if (cmd.equals("send")) {
				PcControlMain.getInstance().ArSender.sendMessage("$rscmessage." + s.substring(5));
			} else if (cmd.equals("reload")) {
				if(separated.length>1) {
					if(separated[1].equals("config")) {	
						GeneralStuff.reloadFiles();
					}
				}
			} else if (cmd.equals("some-string-for-replacement-of-already-used")) {
				if (output != null) {
					PrintWriter p = new PrintWriter(output);
					// p.println(args[0]);
					p.println("the-actual-string");
					p.flush();
					p.close();
				}
			} else {
				GeneralLogger.log(unknownCommand);
			}
		}
	}

	public static void doExit() {
		System.exit(0);
	}
}
