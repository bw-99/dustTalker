
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WritingThread extends Thread {
    Socket socket = null;
    Scanner scanner = new Scanner(System.in);
    JTextArea textArea = null; 
    JTextField tfMsg = null;

    public WritingThread(Socket soc, JTextArea textArea, JTextField tfMsg){
        this.socket = soc;
        this.textArea = textArea;
        this.tfMsg = tfMsg;
    }

    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);

            while (true) {
                MainFrame.lock.lock();

                MainFrame.newDeposit.await();
                
                String chatchat = tfMsg.getText();
                tfMsg.setText(""); 

                textArea.append(this.socket.getInetAddress() + "/" + this.socket.getLocalPort() + " : " + chatchat + "\n");

                dos.writeUTF(chatchat);
                dos.flush();
                
                MainFrame.lock.unlock();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
