import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.JTextField;

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

            ListeningThread t1 = new ListeningThread(soc, textArea);
            WritingThread t2 = new WritingThread(soc, textArea, tfMsg);

            t1.start();
            t2.start();

            t1.join();
            t2.join();


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("fuinish");
        
    }
}
