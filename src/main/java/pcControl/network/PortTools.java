package pcControl.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Properties;

public class PortTools {
	Properties props = new Properties();
	private static volatile PortTools INSTANCE;
	private ArrayList<Integer> usedPorts = new ArrayList<>();
	
	public static PortTools getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PortTools();
		}
		return INSTANCE;
	}
	
	public boolean isAvailable(int port) {
	    if (port <= 1024 || port > 65535) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
	
	public int getAvaliable() {
		int port = 1025;
		try{
			while(!isAvailable(port) || usedPorts.contains(port)) {
				port++;
			}
		}
		catch(IllegalArgumentException e) {
			return -1;
		}
		usedPorts.add(port);
		return port;
	}
}
