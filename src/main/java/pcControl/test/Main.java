package pcControl.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
	
	public static File directory;
	
	public static void killProcessCompletely(ProcessHandle ph) {
		ph.children().forEach(ph2-> {
			killProcessCompletely(ph2);
		});
		//System.out.println("Stage " + stage + ", " + ph.toString());
		ph.destroyForcibly();
	}
	
	public static String launchFileName = "ConsoleApplication1.exe";
	
	public static boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	public static void main(String[] args) {
		
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		AtomicReference<Process> ps = new AtomicReference<>();
		directory = new File("G:\\Visual Studio\\ConsoleApplication1\\Debug");
		
		if(new File(directory, launchFileName).exists()) {
			Thread serverThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ps.set(Runtime.getRuntime().exec("cmd.exe /c \"cd \"" + directory + "\" && start cmd /c " + launchFileName + "\""));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			serverThread.start();
		}
		
		/*try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		killer(ps.get().toHandle());*/
	}
}
