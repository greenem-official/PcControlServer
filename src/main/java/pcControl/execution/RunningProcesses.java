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
	
	public static SocketClient sender; //create one in references later
	public static Thread currentThread;
	
	public static void checkExetutionPerms(SocketClient sender, String file) {
		RunningProcesses.sender = sender;
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
		
		sender.sendMessage("$system.files.executefile.result.success");
		
		References.subProcessNeedToBeOnline = true;
		
		currentThread = new Thread(startListeningRunnable);
		currentThread.start();
	}
	
	private static Runnable startListeningRunnable = new Runnable() {
		
		@Override
		public void run() {
			String line = "";
			while(line!=null && References.subProcessNeedToBeOnline) {
				try {
					line = References.currentRunningSubProcessBufferedReader.readLine();
					if(line!=null) {
						sender.sendMessage("$system.execution.output="+line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//sender.sendMessage("$rscmessage.Process has stopped by itself");
			sender.sendMessage("$system.execution.stopped.justend");
		}
	};
	
	public static void sendInput(String input) {
		PrintWriter p = new PrintWriter(References.currentRunningSubProcess.getOutputStream());
		p.println(input);
		p.flush();
	}
	
	public static void stopProcess() throws ClassNotFoundException { // NoClassDefFoundError: pcControl/execution/RunningProcesses    cleanDisconnectedUserChanges
		References.subProcessNeedToBeOnline = false;
		if(currentThread!=null) {
			currentThread.interrupt();
			killProcessCompletely(References.currentRunningSubProcess.toHandle());
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
