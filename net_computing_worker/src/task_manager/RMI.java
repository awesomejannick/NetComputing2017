package task_manager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {
	<T> T executeTask(Task<T> t) throws RemoteException;
}
