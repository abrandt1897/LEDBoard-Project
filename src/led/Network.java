package led;

import java.net.*;
import java.io.*;

public class Network {
	
	private Socket socket = null; 
    private DataOutputStream out = null; 
    
	public Network(String ip, int port) {
		try {
			socket = new Socket(ip, port);
			out = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean sendData(String data) {
		try {
		      BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		      bufferedWriter.write(data);
		      bufferedWriter.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public boolean close() {
		try {
			out.close();
			socket.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
