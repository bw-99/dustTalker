import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerTread extends Thread{

    public  Socket soc;
    private int tid;
    private  BufferedReader reader = null;
    private  FileReader fileReader = null;

    public ServerTread(Socket soc, int tid){
        this.soc = soc;
        this.tid = tid;
    }

    public void run(){
        InputStream in = null;
        DataInputStream din = null;
        OutputStream out = null;
        PrintWriter writer = null;

        try {
            in = soc.getInputStream();
            din = new DataInputStream(in);

            out = soc.getOutputStream();
            writer = new PrintWriter(out,true);   
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
                    if(ServerMain.threadList.get(i).second().getPort() != origPort){
                        OutputStream newOut = ServerMain.threadList.get(i).second().getOutputStream();
                        PrintWriter newDot = new PrintWriter(newOut,true);
                        newDot.println(soc.getInetAddress()  + "/"+  soc.getPort()  + " : " +chatchat);
                    }
                    
                }
            } catch (IOException e) {
                return;
            }
        }   
    }
}