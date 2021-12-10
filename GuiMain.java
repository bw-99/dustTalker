
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;

public class GuiMain extends Thread {
    

    public void run() {
        MainFrame mf = new MainFrame();
        ChatMain chat = new ChatMain(MainFrame.textArea, MainFrame.tfMsg);
        chat.run();
    }
}


class MainFrame extends JFrame{
    public static JTextArea textArea; //멤버 참조변수

	public static JTextField tfMsg;

    // Create a lock
	public static Lock lock = new ReentrantLock();
	
	// Create a condition
	public static Condition newDeposit = lock.newCondition();


	JButton btnSend;

    MainFrame(){
        setTitle("Client");
		setBounds(450, 400, 500, 350);
		textArea = new JTextArea();		
		textArea.setEditable(false); //쓰기 금지

		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane,BorderLayout.CENTER);
		
        JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		
        tfMsg = new JTextField();
		btnSend = new JButton("send");
		
        msgPanel.add(tfMsg, BorderLayout.CENTER);
		msgPanel.add(btnSend, BorderLayout.EAST);
		
        add(msgPanel,BorderLayout.SOUTH);

		//send 버튼 클릭에 반응하는 리스너 추가
		btnSend.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		tfMsg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				super.keyPressed(e);
				int keyCode = e.getKeyCode();
				switch(keyCode) {
				case KeyEvent.VK_ENTER:
					sendMessage();
					break;
				}
			}
		});


        setVisible(true);
		tfMsg.requestFocus();
    }

    void sendMessage() {	
        lock.lock();

        String msg = tfMsg.getText();

        newDeposit.signal();

        lock.unlock();

    }
}