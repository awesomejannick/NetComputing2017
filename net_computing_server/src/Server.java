import java.io.ObjectOutputStream;
import java.net.Socket;

import message_inbox.ConnectionList;
import message_inbox.Message;
import message_inbox.MessageInbox;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Server {
	private ConnectionList workers;
	private MessageInbox message_inbox;
	private static Sigar sigar = new Sigar();
	private int port;
	
	public Server(int port) {
		this.workers = new ConnectionList();
		this.message_inbox = new MessageInbox(this.workers, port);
		this.port = port;
	}
	
	public void start() {
		Thread t = new Thread(this.message_inbox);
		t.start();
	}
	
	public void takeMeasurement() {
		/*Mem mem = null;
		CpuPerc[] cpu = null;
        try {
            mem = sigar.getMem();
            cpu = sigar.getCpuPercList();
        } catch (SigarException se) {
            se.printStackTrace();
        }
        
        for(int i = 0; i<cpu.length; i++) {
        	System.out.println("CPU core " + i + ":" + cpu[i].getCombined()*100 + "%");
        }
        
        System.out.println("Actual total free system memory: "
                + mem.getActualFree() / 1024 / 1024+ " MB");
        System.out.println("Actual total used system memory: "
                + mem.getActualUsed() / 1024 / 1024 + " MB");
        System.out.println("Total free system memory ......: " + mem.getFree()
                / 1024 / 1024+ " MB");
        System.out.println("System Random Access Memory....: " + mem.getRam()
                + " MB");
        System.out.println("Total system memory............: " + mem.getTotal()
                / 1024 / 1024+ " MB");
        System.out.println("Total used system memory.......: " + mem.getUsed()
                / 1024 / 1024+ " MB");

        System.out.println("\n**************************************\n");*/
	}
	
	public void test() {
		System.out.println("Started messageInbox, sleeping for 500ms");
		try {
			Thread.sleep(500);
			this.takeMeasurement();
			Socket s = new Socket("localhost", 5000);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			for(int i=0; i<1000; i++) {
				Message msg = new Message(String.format("test%d",i));
				oos.writeObject(msg);
				System.out.println("Sent message to inbox");
				this.takeMeasurement();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		this.takeMeasurement();
	}
	
	public static void main(String [] args) {
		int port = Integer.parseInt(args[0]);
		Server s = new Server(port);
		s.start();
		
		s.test();
	}
}
