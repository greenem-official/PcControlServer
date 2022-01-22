package pcControl.network;

import java.io.BufferedReader;
import java.io.IOException;
import pcControl.PcControlMain;

public class LogListener {
	//private Socket socket;
	private BufferedReader in;
	private String id;
	
	@SuppressWarnings("unused")
	private static final String INFO______________________________________THIS___________IS__________NOT_________USED__________________IDK_WHAT_EVEN_THIS_IS = ""; 
	
	/*public LogListener(Socket socket, BufferedReader in, String id) {
		this.socket = socket;
		this.in = in;
		this.id = id;
	}
	
	public LogListener(Socket socket, String id) {
		this.id = id;
		this.socket = socket;
		try {
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public LogListener(BufferedReader in, String id) {
		this.id = id;
		this.in = in;
	}
	
	public Runnable runnable = new Runnable() {
		@Override
		public void run() {
			String input = "";
			SocketClient sender = PcControlMain.getInstance().ArSender;
			while(input!=null){
				try {
					input = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(input!=null) {
					//ServersData.serverLogs.put(id, ServersData.serverLogs.get(id) + input + "\n");
					sender.sendMessage("$mclog.server.id=" + id + ";text=" + input);
				}
//				ServersData./
			}
		}
	};
	
	private void init() {
		//ServersData.serverInputs.put(id, in);
	}

}
