
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JTextArea;

public class ListeningThread extends Thread{
    Socket socket = null;

    
    public ListeningThread(Socket soc){
        this.socket = soc;
    }

    public void run(){
        try {
            InputStream input = this.socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while(true){
                String chatchat =  reader.readLine() + "\n";
                InitUI.textArea.append(chatchat);
                System.out.println(chatchat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
