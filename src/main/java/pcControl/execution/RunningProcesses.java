package pcControl.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.Logger;

import pcControl.data.References;
import pcControl.network.SocketClient;

public class RunningProcesses {
	private static Logger log = References.log4j;
	
	//public static SocketClient sender; //create one in references later
	public static Thread LcurrentThread;
	
	public static void checkExetutionPerms(SocketClient sender, String file) {
		//References.sender = sender;
		File f = new File(References.arLocation, file);
		if(f.exists()) {
			try {
				if(Permissions.hasFolderAccess(f.getCanonicalPath(), References.foldersAndFilesAllowedToExecute)) {
					startProcess(References.arLocation, file);
				}
				else {
					sender.sendMessage("$servermessage.text=You don't have permission to execute that file!");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			sender.sendMessage("$system.files.executefile.result.notfound");
		}
	}
	
	public static void startProcess(File directory, String fileName) {
		AtomicReference<Process> ps = new AtomicReference<>();
		
		try {
			ps.set(Runtime.getRuntime().exec("cmd start /c cd \"" + directory + "\" && \"" + fileName + "\""));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		References.currentRunningSubProcess = ps.get();
		References.currentRunningSubProcessBufferedReader = new BufferedReader(new InputStreamReader(References.currentRunningSubProcess.getInputStream()));
		
		References.sender.sendMessage("$system.files.executefile.result.success");
		
		References.subProcessNeedToBeOnline = true;
		
		LcurrentThread = new Thread(startListeningRunnable);
		LcurrentThread.start();
	}
	
	private static Runnable startListeningRunnable = new Runnable() {
		
		@Override
		public void run() {
			String line = "";
			while(line!=null && References.subProcessNeedToBeOnline) {
				try {
					line = References.currentRunningSubProcessBufferedReader.readLine();
					if(line!=null) {
						References.sender.sendMessage("$system.execution.output="+line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//sender.sendMessage("$rscmessage.Process has stopped by itself");
			References.sender.sendMessage("$system.execution.stopped.justend");
		}
	};
	
	public static void sendInput(String input) {
		PrintWriter p = new PrintWriter(References.currentRunningSubProcess.getOutputStream());
		p.println(input);
		p.flush();
	}
	
	public static void stopProcess() throws ClassNotFoundException { // NoClassDefFoundError: pcControl/execution/RunningProcesses    cleanDisconnectedUserChanges
		try {
			References.subProcessNeedToBeOnline = false;
			if(LcurrentThread!=null) {
				System.out.println(References.currentRunningSubProcess.getClass().getName());
				LcurrentThread.interrupt();
				killProcessCompletely(((java.lang.Process)References.currentRunningSubProcess).toHandle());
				References.sender.sendMessage("$servermessage.text=Stopped the process.");
			}
		}
		catch(NoSuchMethodError e) {
			log.debug("NoSuchMethodError", e);
		}
	}
	
	public static void killProcessCompletely(ProcessHandle ph) {
		ph.children().forEach(ph2-> {
			killProcessCompletely(ph2);
		});
		//System.out.println("Stage " + stage + ", " + ph.toString());
		ph.destroyForcibly();
	}
}
