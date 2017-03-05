import resource_monitor.ResourceMonitor;

public class Worker {	
	public static void main(String [] args) {
		if(args.length<=0) {
			System.out.println("Give a port to send results too!");
			return;
		}
		int serverPort = Integer.parseInt(args[0]);
		
		ResourceMonitor monitor = new ResourceMonitor(serverPort);
		monitor.start();
	}
}
