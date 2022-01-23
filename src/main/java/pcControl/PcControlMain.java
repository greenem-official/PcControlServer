package pcControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pcControl.data.References;
import pcControl.deprecated.SocketRunnable;
import pcControl.execution.GeneralStuff;
import pcControl.execution.Permissions;
import pcControl.logging.GeneralLogger;
import pcControl.network.AndroidListener;
import pcControl.network.PortTools;
import pcControl.network.SocketClient;

public class PcControlMain {
	private static Logger log = null;
	
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
		File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		References.appExecutionDir = jarDir;
		doLoggerFileStuff();
		
		References.log4j = LogManager.getLogger();
		log = References.log4j;

		log.info("REMOTE PC CONTROL APP - SERVER SIDE");
		log.info("Starting input thread");
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

		//log.info(References.appExecutionDir);
		
		GeneralStuff.setConstants();
		Permissions.init();
		GeneralStuff.reloadFiles();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// log.info("Running Shutdown Hook");
				InputRunnable.onExit();
				try {
					References.ArSocket.close();
					References.PlSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					// e.printStackTrace();
				}
				log.info("Succesfully stopped");
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
		References.log4j.info("> " + s);
		s = s.trim();
		String unknownCommand = "Unknown command or wrong args!";
		String[] separated = s.split(" ");
		// log.info(separated.length);
		if (separated.length == 0) {
			log.info("An empty command"); // test
		}
		if (separated.length == 1) {
			if (s.equalsIgnoreCase("help")) {
				log.info(getHelpText());
			} else if (s.equalsIgnoreCase("exit")) {
				References.STOPPING = true;
				doExit();
			} else if (s.equals("test1")) {
				try {
					log.info("1: " + References.ArSocket);
					log.info("2: " + References.ArSocket.getKeepAlive());
					log.info("3: " + References.ArSocket.getOOBInline());
					log.info("4: " + References.ArSocket.getReceiveBufferSize());
					log.info("5: " + References.ArSocket.getReuseAddress());
					log.info("6: " + References.ArSocket.getSendBufferSize());
					log.info("7: " + References.ArSocket.getSoLinger());
					log.info("8: " + References.ArSocket.getSoTimeout());
					log.info("9: " + References.ArSocket.getTcpNoDelay());
					log.info("10: " + References.ArSocket.getTrafficClass());
					log.info("11: " + References.ArSocket.isBound());
					log.info("12: " + References.ArSocket.isClosed());
					log.info("13: " + References.ArSocket.isConnected());
					log.info("14: " + References.ArSocket.isInputShutdown());
					log.info("15: " + References.ArSocket.isOutputShutdown());
				} catch (SocketException e) {
					e.printStackTrace();
				}
			} else {
				log.info("Unknown command!");
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
				log.info(unknownCommand);
			}
		}
	}
	
	public static void doLoggerFileStuff() {
		File logs = new File(References.appExecutionDir, "logs");
		logs.mkdirs();
		File last = new File(logs, "last.log");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD-MM-YYYY_HH-MM-SS");  
		LocalDateTime now = LocalDateTime.now();  
		last.renameTo(new File(References.appExecutionDir, "/logs/before_" + dtf.format(now) + ".log"));
	}

	public static void doExit() {
		System.exit(0);
	}
}
