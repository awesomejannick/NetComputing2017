package message_inbox;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Connection {
	private Socket socket;
	
	public Connection(Socket s) {
		this.socket = s;
	}
	
	public int getPort() {
		return this.socket.getPort();
	}
	
	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}
	
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}
	
	@Override
	public boolean equals(Object other) {
		Connection c;
		if(other instanceof Connection) {
			c = (Connection)other;
			if(c.getInetAddress() == this.getInetAddress() &&
					c.getPort() == this.getPort()) {
				return true;
			}
		}
		return false;
	}
}
