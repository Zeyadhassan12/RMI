import java.rmi.Remote;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public interface NodeI extends Remote {

    String[] ipAddr = {"192.168.1.4", "192.168.1.4", "192.168.1.4","192.168.1.4"};
    String[] services = {"A", "B", "C","D"};//"D"
    Integer[] ports = {56423, 56951, 56947,58664 };//61753 //63342

    void performOperation(Operations o) throws RemoteException, NotBoundException;

    void ack(Operations o) throws RemoteException;

    Operations downloadFile(String var1) throws RemoteException;

    boolean uploadFile(Operations var1) throws RemoteException;

    boolean searchFiles(String var1) throws RemoteException;

    boolean deleteFile(String var1) throws RemoteException;
    boolean requestVote(int term, int candidateId) throws RemoteException;



}
