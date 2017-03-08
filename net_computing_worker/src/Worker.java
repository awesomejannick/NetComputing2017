import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import resource_monitor.ResourceMonitor;
import task_manager.TaskManager;



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
		
		
		// initialize taskManager
		TaskManager tm = new TaskManager();
		tm.initSecurityManager();
		
		try {
			TaskManager stub = (TaskManager) UnicastRemoteObject.exportObject(tm, 0);
			Registry registry = LocateRegistry.getRegistry();
            registry.rebind("taskManager", stub);
            System.out.println("TaskManager bound");
		} catch (RemoteException e) {

			System.out.println("remote exception in taskManager");
			e.printStackTrace();
		}
	}
}
