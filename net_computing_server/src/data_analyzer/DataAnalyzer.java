package data_analyzer;

import java.rmi.Naming;

import message_inbox.Connection;
import message_inbox.ConnectionList;
import message_inbox.Message;
import message_inbox.MessageQueue;
import rmi.Measurement;
import rmi.TaskInfo;
import rmi.TaskServer;
import task_distributor.TaskList;

public class DataAnalyzer implements Runnable {
	
	private MessageQueue messages;
	private ConnectionList workers;
	private TaskList tasklist;
	
	public DataAnalyzer(MessageQueue messages, ConnectionList c, TaskList tl) {
		this.messages = messages;
		this.workers = c;
		this.tasklist = tl;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		while (true) {
			int messageType = messages.getFirstType();
			if (messageType == 1) { 
				// it's a measurement
				Message<Measurement> message = (Message<Measurement>) messages.dequeue();
				Measurement measure = message.getMessageContent();
				
				// update the connection that sent the message
				Connection conn = message.getConn();
				conn.setLastMeasurement(measure);
				this.workers.update(conn);
			} else if (messageType == 2) {
				// it's taskinfo
				Message<TaskInfo> message = (Message<TaskInfo>) messages.dequeue();
				TaskInfo taskinfo = message.getMessageContent();
				
				// Get relevant data from the taskinfo
				int pid = taskinfo.getPid();
				Connection conn = message.getConn();
				System.out.println("data analyzer pid = " + pid + ", conn = " + conn);
				
				// Finish the taskinfo's respective task
				try {
					TaskServer stub=(TaskServer)Naming.lookup("rmi://" + conn.getInetAddress().getHostAddress() + ":1099/taskManager");  
					String[] result = stub.getOutput(pid);
					tasklist.finishTask(pid, conn, taskinfo.getFinishTime(), result[0], result[1], taskinfo.getStatus());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
