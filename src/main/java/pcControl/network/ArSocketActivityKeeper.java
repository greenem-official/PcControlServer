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
		this.socket = References.socket;
		this.in = References.inSocket;
		this.out = References.outSocket;
	}
	
	public void stop() {
		stop = true;
	}

	@Override
	public void run() {
		while(!stop && References.socket!=null && !References.socket.isClosed()) {
			if(Calendar.getInstance().getTimeInMillis() - References.lastArInSocketActivity > References.hearbeatThreshold) {
				References.sender.sendMessage("$heartbeat.timeout");
				log.info("Time out");
				try {
					Thread.currentThread().sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					References.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				stop = true;
				GeneralStuff.cleanDisconnectedUserChanges();
				break;
			}
			if(!stop){
				try {
					Thread.currentThread().sleep(References.hearbeatThreshold);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
