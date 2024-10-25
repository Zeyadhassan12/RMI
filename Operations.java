import java.io.Serializable;
import java.util.Date;

public class Operations  implements Comparable <Operations>, Serializable{

    private static final long serialVersionUID = 1L;

    public static final int DOWNLOAD = 1;
    public static final int UPLOAD = 2;
    public static final int SEARCH = 3;
    public static final int DELETE = 4;
    public static String PATH_1 = "/Users/zeyadhassan/Desktop/Test-RMI";
    public static String PATH_2 = "/Users/zeyadhassan/Desktop/Test-RMI/Local";
    public static String FileName = "test";
    private String name;
    private String path;
    private byte[] data;
    private Date lastModifiedDate;
    private int timestamp;

    String tId , sender;
    int pId , clock, operation;


    public Operations() {
        this.data = new byte[0];
        this.lastModifiedDate = new Date();
    }
    // Constructor for Download operation
//    public Operations(String name, String path, int timestamp, String tId, String sender, int pId, int clock) {
//        this.name = name;
//        this.path = path;
//        this.timestamp = timestamp;
//        this.tId = tId;
//        this.sender = sender;
//        this.pId = pId;
//        this.clock = clock;
//        this.operation = DOWNLOAD;
//    }

    public Operations(String tId, int pId, String sender, int operation,String path,  int clock, String FileName) {
        this.tId = tId;
        this.pId = pId;
        this.sender = sender;
        this.operation = operation;
        this.path = path;
        this.clock = clock;
        this.FileName = FileName;
    }




    // Constructor for Upload operation
    public Operations(String name, String path, byte[] data, int timestamp, String tId, String sender, int pId, int clock) {
        this.name = name;
        this.path = path;
        this.data = data;
        this.timestamp = timestamp;
        this.tId = tId;
        this.sender = sender;
        this.pId = pId;
        this.clock = clock;
        this.operation = UPLOAD;
    }
    // Constructor for Search operation
    public Operations(String name, int timestamp, String tId, String sender, int pId, int clock, String FileName) {
        this.name = name;
        this.timestamp = timestamp;
        this.tId = tId;
        this.sender = sender;
        this.pId = pId;
        this.clock = clock;
        this.operation = SEARCH;
        this.FileName = FileName;
    }
    // Constructor for Delete operation
    public Operations(String name, int timestamp, boolean isDelete, String tId, String sender, int pId, int clock) {
        this.name = name;
        this.timestamp = timestamp;
        this.tId = tId;
        this.sender = sender;
        this.pId = pId;
        this.clock = clock;
        this.operation = DELETE;
    }



    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public int getTimeStamp(){
        return this.timestamp;
    }

    public void setTimestamp(int timestamp){
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Operations o) {
        // Tie Breaker
        if (this.clock == o.clock)
            return this.pId - o.pId;
        return this.clock - o.clock;


    }
}