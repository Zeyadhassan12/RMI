import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


public class Node extends UnicastRemoteObject implements NodeI{

//    private RaftNode raftNode;

    private static final long serialVersionUID = 1L;

    private boolean isLeader;

    //Number of nodes
    final static int n = ipAddr.length;

    int Clock,myPort;
    String myIp , myService;
    PriorityQueue<Operations> operations;
    HashMap<String,Integer> operationsAcks;
    Scanner scan;

    public int idx() {
        return this.myPort;
    }

    protected Node(int idx) throws RemoteException {

        isLeader = new Random().nextBoolean();
        myIp = ipAddr[idx];
        myPort = ports[idx];
        myService = services[idx];
        Clock = 0;
        operations = new PriorityQueue<Operations>();
        operationsAcks = new HashMap<String, Integer>();
        scan = new Scanner(System.in);

//        raftNode = new RaftNode(idx);
    }




    public void multicastOperation(Operations o) throws RemoteException, NotBoundException{

        boolean delay = true;
        for(int i=0 ; i<n;i++){
            if(delay && o.sender.equals("A")){
                if(i==3){
                    Delayer delayer = new Delayer(ports[i],services[i],o);
                    new Thread(delayer).start();
                    continue;
                }
            }
            Registry reg = LocateRegistry.getRegistry(ports[i]);
            NodeI e = (NodeI) reg.lookup(services[i]);
            e.performOperation(o);

        }
        System.out.println("End of Multicast");
        displayOperation();
        displayAcks();
        System.out.println("\n");

    }

    @Override
    public void performOperation(Operations o) throws RemoteException, NotBoundException {
        operations.add(o);
        if(!o.sender.equalsIgnoreCase(myService)){
            Clock = Math.max(Clock, o.clock) + 1;
        }

        System.out.println("Perform Operation");
        displayOperation();
        displayAcks();
        MulticastAck(o);

    }

    private void MulticastAck(Operations o) throws RemoteException,NotBoundException{
        boolean delay = true;
        for(int i=0 ; i<n;i++){
            if(delay && myService.equals("A")){
                if(i==3){
                    System.out.println("################## Ack delay ##################");
                    AckDelayer ackDelayer = new AckDelayer(ports[i] , services[i], o);
                    new Thread (ackDelayer).start();
                    System.out.println("Ack sent to : (" + services[i] + ") on operation sent from : " +o.sender );
                    continue;
                }
            }
            Registry reg = LocateRegistry.getRegistry(ports[i]);
            NodeI e = (NodeI) reg.lookup(services[i]);
            System.out.println("Ack is sent to :(" + services[i]+") on operation sent from :"+o.sender);
            e.ack(o);
        }
    }

    @Override
    public void ack(Operations o) throws RemoteException {

        if(operationsAcks.containsKey(o.tId)){
            operationsAcks.put(o.tId,operationsAcks.get(o.tId)+1);

        }
        else{
            operationsAcks.put(o.tId,1);
        }

    }

    @Override
    public Operations downloadFile(String fileName) throws RemoteException {
        System.out.println("Downloading File...");

        try {
            File f = new File(Operations.PATH_1 + fileName);
            Operations fs = new Operations();
            int fileSize = (int)f.length();
            byte[] buffer = new byte[fileSize];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
            in.read(buffer, 0, buffer.length);
            in.close();
            fs.setData(buffer);
            fs.setName(fileName);
            fs.setLastModifiedDate(new Date(f.lastModified()));
            return fs;
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean uploadFile(Operations fs) throws RemoteException {
        System.out.println("Uploading File...");
        File localFile = new File(Operations.PATH_1 + fs.getName());
        if (!localFile.exists()) {
            localFile.getParentFile().mkdir();
        }

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
            out.write(fs.getData(), 0, fs.getData().length);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return false;

    }

    @Override
    public boolean searchFiles(String fileName) throws RemoteException {
        System.out.println("Searching for File...");
        File f = new File(Operations.PATH_1 + Operations.FileName);
        return f.exists();
    }

    @Override
    public boolean deleteFile(String fileName) throws RemoteException {
        System.out.println("Deleting File...");
        File f = new File(Operations.PATH_1 + Operations.FileName);
        return f.delete();
    }

    @Override
    public boolean requestVote(int term, int candidateId) throws RemoteException {
        return false;
    }


    public void fetchNewOperation() throws RemoteException{
        if(operations.size() > 0 && operationsAcks.containsKey(operations.peek().tId) && operationsAcks.get(operations.peek().tId) == n){
            System.out.println("\n Fetch Operation Updates:");
            displayOperation();
            displayAcks();


            Operations o = operations.poll();
            operationsAcks.remove(o.tId);

            System.out.println("After fetching Operation updates:");
            displayOperation();
            displayAcks();

            System.out.println("Perform Operation: "+o);
            if(o.operation == Operations.DOWNLOAD || o.operation == Operations.SEARCH){
//raftNode.getState()==RaftNode.RaftState.LEADER
                if(isLeader){
                    if(o.operation == Operations.DOWNLOAD){
                        NodeI fs = (NodeI) new Operations();
                        String fileName = o.getName();
                        download(fs,fileName);
                    }}else if(o.operation == Operations.SEARCH){
                    NodeI fs = (NodeI) new Operations();
                    String fileName = o.getName();
                    search(fs,fileName);

                }

            }else if(o.operation == Operations.UPLOAD){

                NodeI fs = (NodeI) new Operations();
                String fileName = o.getName();
                upload(fs,fileName);


            }else if(o.operation == Operations.DELETE){
                NodeI fs = (NodeI) new Operations();
                String fileName = o.getName();
                delete(fs,fileName);

            }

            System.out.println("\n 1-Download\n 2-Upload\n 3- Search\n 4- Delete");



        }
    }



    public void displayOperation(){
        System.out.println("\n---------------Operation---------------- ");
        for(Operations o: operations){
            System.out.println("("+(o.operation == Operations.DOWNLOAD)+o.sender+","+o.clock+","+o.tId+")");
        }
        System.out.println("----------------------------\n");
    }

    public void displayAcks(){
        System.out.println("\n---------------Acks---------------- ");

        Set<String> keys = operationsAcks.keySet();
        for(String k:keys){
            System.out.println("("+k+","+operationsAcks.get(k)+")");

        }
        System.out.println("----------------------------\n");
    }


    public void download(NodeI fileServer, String fileName) throws RemoteException {
        Operations fs = fileServer.downloadFile(fileName);
        File localFile = new File(Operations.PATH_2 + fileName);
        if (!localFile.exists()) {
            localFile.getParentFile().mkdir();
        }

        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
            out.write(fs.getData(), 0, fs.getData().length);
            out.flush();
            out.close();
        } catch (FileNotFoundException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

    }


    public void upload(NodeI fileServer, String fileName) throws RemoteException {
        Operations fs = new Operations();
        File localFile = new File(Operations.PATH_2 + fileName);
        int fileSize = (int)localFile.length();
        byte[] buffer = new byte[fileSize];

        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(localFile));
            in.read(buffer, 0, buffer.length);
            in.close();
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        fs.setData(buffer);
        fs.setName(fileName);
        fs.setLastModifiedDate(new Date(localFile.lastModified()));
        boolean res = fileServer.uploadFile(fs);
        if (res) {
            System.out.println("File uploaded successfully.");
        } else {
            System.out.println("An error occurred. Try again later");
        }

    }

    public void search(NodeI fileServer, String fileName) throws RemoteException {
        boolean res = fileServer.searchFiles(fileName);
        if (res) {
            System.out.println("File found");
        } else {
            System.out.println("No matches found");
        }

    }


    public void delete(NodeI fileServer, String fileName) throws RemoteException {
        boolean res = fileServer.deleteFile(fileName);
        if (res) {
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("An error occurred. Try again later");
        }

    }



    }





