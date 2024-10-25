
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



public class Main {
    public static String PATH_1 = "/Users/zeyadhassan/Desktop/Test-RMI/";
    public static String PATH_2 = "/Users/zeyadhassan/Desktop/Test-RMI/Local";
    public static String FileName = "test";
    public Main(){

    }

    public static void main (String [] args){

        byte pId = 0;

        try {
            System.out.println("Process: " + NodeI.services[pId]);
            Node obj = new Node(pId);
            initServer(obj);
            initClient(obj, pId);
        } catch (RemoteException var3) {
            System.out.println(var3.getMessage());
            var3.printStackTrace();
        } catch (NotBoundException var4) {
            System.out.println(var4.getMessage());
            var4.printStackTrace();
        }



    }

    public static void initServer(Node obj) throws RemoteException {
        Registry reg = LocateRegistry.createRegistry(obj.myPort);
        reg.rebind(obj.myService, obj);
    }

    private static void initClient(final Node obj, int pId) throws RemoteException, NotBoundException {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    obj.fetchNewOperation();
                } catch (RemoteException var2) {
                    System.out.println(var2.getMessage());
                }

            }
        }, 0L, 100L);

        while(true) {
            int operation;
            do {
                System.out.println("1 - Download\n2 - Upload\n 3 - Search\n 4 - Delete");
                operation = obj.scan.nextInt();
            } while(operation != 1 && operation != 2);//operation != 1 && operation != 2

            String tId = UUID.randomUUID().toString();
            String sender = obj.myService;
            ++obj.Clock;


            Operations o = null;
            if (operation == 1) {
                o = new Operations(tId, pId, sender, 1, PATH_1 , obj.Clock,FileName );
            } else if(operation == 2) {
                o = new Operations(tId, pId, sender, 2, PATH_1 , obj.Clock, FileName);
            }else if(operation ==3){
                o = new Operations(tId, pId, sender, 3, PATH_1 , obj.Clock, FileName);
            }else if(operation ==4){
                o = new Operations(tId, pId, sender, 4, PATH_1 , obj.Clock,FileName );
            }else{
                System.out.println("No other Operation");
            }

            obj.multicastOperation(o);
        }
    }


}
