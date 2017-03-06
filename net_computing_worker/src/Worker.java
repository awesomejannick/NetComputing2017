import java.net.InetAddress;
import java.net.UnknownHostException;

import resource_monitor.ResourceMonitor;

public class Worker {	
	public static void main(String [] args) {
		if(args.length<=0) {
			System.out.println("Give a port to send results too!");
			return;
		}
		
		int serverPort;
		InetAddress serverAddress;
		ResourceMonitor monitor;
		
		try {
			if(args.length==2) {
				serverAddress = InetAddress.getByName(args[0]);
				serverPort = Integer.parseInt(args[1]);
			} else {
				serverAddress = InetAddress.getByName("localhost");
				serverPort = Integer.parseInt(args[0]);
			}
		} catch (UnknownHostException e) {
			System.out.println("That is not a valid address");
			return;
		}
		
		monitor = new ResourceMonitor(serverAddress, serverPort);
		monitor.start();
	}
}
