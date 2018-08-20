import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;


public class ServerTread extends Thread{

    public  Socket soc;
    private int tid;

    public ServerTread(Socket soc, int tid){
        this.soc = soc;
        this.tid = tid;
    }

    public void run(){
        InputStream in = null;
        DataInputStream din = null;
        try {
            in = soc.getInputStream();
            din = new DataInputStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        while (true) {
            String chatchat;
            try {
                chatchat = din.readUTF();
                System.out.println(chatchat);
                int origPort = soc.getPort();
                
                for (int i = 0; i < ServerMain.threadList.size(); i++) {
                    // * IP나 port가 다른 소켓만 (다른 유저)
                    if(ServerMain.threadList.get(i).second().getPort() != origPort || !(ServerMain.threadList.get(i).second().getInetAddress().equals(soc.getInetAddress()))){
                        OutputStream newOut = ServerMain.threadList.get(i).second().getOutputStream();
                        PrintWriter newDot = new PrintWriter(newOut,true);
                        newDot.println("[" + soc.getInetAddress()  + "/"+  soc.getPort()  + "] ["+ LocalTime.now()  +"]" + " : " +chatchat);
                    }

                    
                }
            } catch (IOException e) {
                return;
            }
        }   
    }
}