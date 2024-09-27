import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.JTextField;

//sadfasdf
public class ChatMain extends Thread{
    JTextArea textArea = null; 
    JTextField tfMsg = null;

    public ChatMain(JTextArea textArea, JTextField tfMsg){
        this.textArea = textArea;
        this.tfMsg = tfMsg;
    }

    public void run()  {
        try {
            Socket soc = new Socket("localhost",5000);

            ListeningThread t1 = new ListeningThread(soc);
            WritingThread t2 = new WritingThread(soc);

            t1.start();
            t2.start();

            t1.join();
            t2.join();


        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
}



class WritingThread extends Thread {
    Socket socket = null;
    Scanner scanner = new Scanner(System.in);


    public WritingThread(Socket soc){
        this.socket = soc;

    }

    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            while (true) {
                MainFrame.lock.lock();

                MainFrame.newDeposit.await();
                
                String chatchat = InitUI.tfMsg.getText();
                InitUI.tfMsg.setText(""); 

                InitUI.textArea.append(this.socket.getInetAddress() + "/" + this.socket.getLocalPort() + " : " + chatchat + "\n");

                dos.writeUTF(chatchat);
                dos.flush();
                
                MainFrame.lock.unlock();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class ListeningThread extends Thread{
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
                InitUI.cursorMaximum();
                System.out.println(chatchat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
