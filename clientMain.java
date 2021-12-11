
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

public class clientMain {
    public static void main(String[] args)  {
        

        try {
            GuiMain guiMain = new GuiMain();
            guiMain.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        System.out.println("Client FINSISH");
        
    }
}