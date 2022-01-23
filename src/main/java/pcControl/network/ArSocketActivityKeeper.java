package pcControl.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

import org.apache.logging.log4j.Logger;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.execution.GeneralStuff;
import pcControl.logging.GeneralLogger;

public class ArSocketActivityKeeper implements Runnable {
	private static Logger log = References.log4j;
	
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean stop = false;
	
	public void init () {
		this.socket = References.ArSocket;
		this.in = References.ArInSocket;
		this.out = References.ArOutSocket;
	}
	
	public void stop() {
		stop = true;
	}

	@Override
	public void run() {
		while(!stop && References.ArSocket!=null && !References.ArSocket.isClosed()) {
			if(Calendar.getInstance().getTimeInMillis() - References.lastArInSocketActivity > 35000) {
				PcControlMain.getInstance().ArSender.sendMessage("$heartbeat.timeout");
				log.info("Time out");
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					References.ArSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				stop = true;
				GeneralStuff.cleanDisconnectedUserChanges();
				break;
			}
			if(!stop){
				try {
					Thread.currentThread().sleep(35000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
