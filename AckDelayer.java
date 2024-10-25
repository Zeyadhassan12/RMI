import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AckDelayer implements Runnable {

    private int port;
    private String serviceName;
    private Operations operations;

    public AckDelayer(int port, String serviceName, Operations operations){
        this.port = port;
        this.serviceName = serviceName;
        this.operations = operations;

    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            Registry reg = LocateRegistry.getRegistry(port);
            NodeI e = (NodeI) reg.lookup(serviceName);
            e.ack(operations);


        } catch (RemoteException | NotBoundException | InterruptedException e1) {
            e1.printStackTrace();

        }
    }
}

