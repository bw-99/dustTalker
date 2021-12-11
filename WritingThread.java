
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;



public class WritingThread extends Thread {
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
