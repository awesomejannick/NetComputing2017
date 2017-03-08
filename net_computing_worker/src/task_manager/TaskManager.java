package task_manager;

import java.rmi.*;

public class TaskManager implements RMI {
	public TaskManager() {
		
	}
	
	public <T> T executeTask(Task<T> t) {
		return t.execute();
	}
	
	public void initSecurityManager() {
		if (System.getSecurityManager() == null) {
		    System.setSecurityManager(new SecurityManager());
		}
	}
}
