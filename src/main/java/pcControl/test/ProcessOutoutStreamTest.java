package pcControl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

import pcControl.data.References;

public class ProcessOutoutStreamTest {

	public static void main(String[] args) {
		
		//File directory = new File("C:\\SERVERS\\MANHUNT SERVERS\\Manhunt1");
		//String fileName = "run.bat";
		
		//File directory = new File("G:\\Visual Studio\\ConsoleApplication1\\Debug");
		//String fileName = "ConsoleApplication1.exe";
		
		File directory = new File("C:\\");
		String fileName = "a.exe";
		
		AtomicReference<Process> ps = new AtomicReference<>();		
		Thread subProcessThread = new Thread(new Runnable() {
			@Override
			public void run() {
				/*try {
					
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		});
		subProcessThread.start();
		
		//ProcessBuilder b = new ProcessBuilder("\"" + new File(directory, fileName) + "\"");
		//b.directory(directory);
		//ps.set(b.start());
		
		try {
			ps.set(Runtime.getRuntime().exec("cmd start /c cd \"" + directory + "\" && \"" + fileName + "\""));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//System.out.println("Exit value: " + ps.get().exitValue());
		System.out.println("Children: " + ps.get().children().count());
		System.out.println();
		PrintWriter p = new PrintWriter(ps.get().getOutputStream());
		/*Optional<ProcessHandle> processHandle = ps.get().children().findFirst();
	    processHandle.ifPresent(proc -> proc.children().forEach(child -> System.out.println("PID: [ " + child.pid() + " ], Cmd: [ " + child.info().command() + " ]")));
		//References.currentRunningSubProcess = (ProcessHandle) ps.get().children()..toArray()[0];
		References.currentRunningSubProcessBufferedReader = new BufferedReader(new InputStreamReader(References.currentRunningSubProcess.getInputStream()));
		System.out.println(ps.get().children().count());
		//System.out.println(new BufferedReader(new InputStreamReader(References.currentRunningSubProcess.get().getInputStream())).readLine());
		
		String line = "";
		while(line!=null) {
			try {
				line = References.currentRunningSubProcessBufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("READ: " + line);
		}*/
		
		References.currentRunningSubProcess = ps.get();
		References.currentRunningSubProcessBufferedReader = new BufferedReader(new InputStreamReader(References.currentRunningSubProcess.getInputStream()));
		String line = "";
		boolean didntWriteYet = true;
		while(line!=null) {
			try {
				line = References.currentRunningSubProcessBufferedReader.readLine();
				if(line!=null) {
					System.out.println("READ: " + line);
				}
				if(didntWriteYet) {
					System.out.println("WRITING: sdfsdf");
					p.println("sdfsdf");
					p.flush();
					didntWriteYet = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/*try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		killer(ps.get().toHandle());*/
	}

}
