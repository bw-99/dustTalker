import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerMain {
    private static Integer SERVER_PORT = 5000;
    private static ServerSocket ss = null;
    public static ArrayList<Pair> threadList = new ArrayList<Pair>();
    private static Integer tid = 0;

    // * Create Server Socket
    private static void initServerSocket(){
        try {
            ss = new ServerSocket(SERVER_PORT);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // * Manage Thread
    private static void manageThread(){
        Pair temp = null;
        Iterator<Pair> iter = threadList.iterator();
        while(iter.hasNext()){
            temp = iter.next();
            if(!temp.first().isAlive()){
                iter.remove();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        initServerSocket();
        while (true) {
            Socket soc = ss.accept();
            Thread t = new ServerTread(soc,tid++);
            Pair pp = new Pair(t, soc);
            threadList.add(pp);
            t.start();     
            manageThread();
        }
    }
}

class Pair{
    Thread t;
    Socket soc;
    public Pair(Thread t, Socket soc){
        this.t = t;
        this.soc =soc;
    }
    public Thread first(){
        return this.t;
    }

    public Socket second(){
        return this.soc;
    }
}