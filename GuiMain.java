
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;


public class GuiMain extends Thread {

	public static DustMain  dustMain  = null;
	public static ChatMain chat = null;

	public GuiMain(){
		dustMain = new DustMain();
		dustMain.run("종로구");
		chat = new ChatMain(InitUI.textArea, InitUI.tfMsg);
	}

    public void run() {
        MainFrame mf = new MainFrame();
		try {
			mf.initUI = new InitUI(mf);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        chat.run();

    }
}


class MainFrame extends JFrame{
	public InitUI initUI = null;
	public DustTalkUI dustTalkUI = null;


	public static String location = "종로구";

	// Create a lock
	public static Lock lock = new ReentrantLock();
	
	// Create a condition
	public static Condition newDeposit = lock.newCondition();

	// public static Lock dustLock = new ReentrantLock();

	// public static Condition dustDeposit = lock.newCondition();


	// public static Lock dustRun = new ReentrantLock();

	// public static Condition dustRunDeposit = lock.newCondition();

	public void change(String panelName){
		if(panelName.equals("initUI")){
			getContentPane().removeAll();
			getContentPane().add(initUI);
			revalidate();
			repaint();
		}
		else {
			getContentPane().removeAll();
			getContentPane().add(dustTalkUI);
			revalidate();
			repaint();
		}
	}
}


class InitUI extends JFrame{
    public static JTextArea textArea; //멤버 참조변수

	private final static int BUTTON_SIZE = 5;

	public static JTextField tfMsg;

	private MainFrame mf;

	private JPanel dustPanel = null;
	

	public static JLabel Pm10 = new JLabel("1.0");

	public static JLabel Pm2_5 = new JLabel("2.0");

    


	JButton btnSend;

    private JPanel initMsgPanel(){
        JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		
        tfMsg = new JTextField();
		btnSend = new JButton("send");
		
        msgPanel.add(tfMsg, BorderLayout.CENTER);
		msgPanel.add(btnSend, BorderLayout.EAST);

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
		tfMsg.requestFocus();


        return msgPanel;
    }

	private void addLocation(String[] locs, JPanel dust){
		for(int i=0;i<locs.length;i++){
			String location = locs[i];

			JButton temp = new JButton(location);
			temp.setPreferredSize(new DimensionUIResource(100, 30));
			temp.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					GuiMain.dustMain.run(location);
					Pm10.setText(DustMain.map.get("PM10"));
					Pm2_5.setText(DustMain.map.get("PM2_5"));
				}
				
			});
			dust.add(temp);
		}
		return;
	}

	private JPanel initDustMainPanel() throws InterruptedException{
		// MainFrame.dustLock.lock();

		// MainFrame.dustDeposit.await();

		
		JPanel dustMain = new JPanel();

        GridBagConstraints[] gbc_main = new GridBagConstraints[BUTTON_SIZE];

        GridBagLayout gbl = new GridBagLayout();
        dustMain.setLayout(gbl);

		for (int i = 0; i < BUTTON_SIZE; i++) {
            gbc_main[i] = new GridBagConstraints();
        }

		dustMain.setBackground(Color.BLUE);
		
		JLabel labelPm10 = new JLabel("PM 10");
		JLabel labelPm2_5 = new JLabel("PM 2.5");


		{
			labelPm10.setBackground(Color.RED);
			labelPm10.setFont(new Font("Serif",Font.BOLD,20));
			labelPm10.setLocation(10, 130);
			labelPm10.setSize(200,50);
			labelPm10.setBackground(Color.RED);

			this.add(labelPm10);
		}

		{
			Pm10.setText(DustMain.map.get("PM10"));
			Pm10.setBackground(Color.RED);
			Pm10.setFont(new Font("Serif",Font.BOLD,20));
			Pm10.setLocation(110, 130);
			Pm10.setSize(200,50);
			Pm10.setBackground(Color.RED);

			this.add(Pm10);
		}

		{
			labelPm2_5.setFont(new Font("Serif",Font.BOLD,20));
			labelPm2_5.setLocation(450, 130);
			labelPm2_5.setSize(200,50);
			this.add(labelPm2_5);
		}

		{
			Pm2_5.setText(DustMain.map.get("Pm2_5"));

			Pm2_5.setFont(new Font("Serif",Font.BOLD,20));
			Pm2_5.setLocation(550, 130);
			Pm2_5.setSize(200,50);
			this.add(Pm2_5);
		}

		// MainFrame.dustLock.unlock();

		return dustMain;
	}

	private JPanel initDustMenuPanel(){
		JPanel dust = new JPanel();
        dust.setLayout(new FlowLayout(0, 0, 10));
		// 도심권
		String[] loc = {"종로구","중구","용산구"};
		addLocation(loc, dust);

		//동북권
		String[] loc2 = {"광진구","성동구","중랑구","동대문구","성북구"};
		addLocation(loc2, dust);

		//동남권
		String[] loc3 = {"강남구","서초구","송파구","강동구"};
		addLocation(loc3, dust);
		
		//서북권
		String[] loc4 = {"은평구","서대문구","마포구"};
		addLocation(loc4, dust);
		
		//서남권
		String[] loc5 = {"강서구","구로구","영등포구","동작구","관악구"};
		addLocation(loc5, dust);
		

		return dust;
	}

    InitUI(MainFrame mf) throws InterruptedException{
		this.mf = mf;
		JFrame fr = new JFrame("This 프레임");
        JPanel pn = new JPanel();

        // JButton[] bt = new JButton[BUTTON_SIZE];
        GridBagConstraints[] gbc = new GridBagConstraints[BUTTON_SIZE];

        GridBagLayout gbl = new GridBagLayout();
        pn.setLayout(gbl);


		for (int i = 0; i < BUTTON_SIZE; i++) {
            /* Button 초기화 */
            // bt[i] = new JButton("Button" + i);

            /* GridBagConstraints 초기화 */
            gbc[i] = new GridBagConstraints();
        }
		
		
		{
			JPanel msgPanel = initMsgPanel();
			// add(msgPanel);
			gbc[0].gridx = 0;
			gbc[0].gridy = 5;
			gbc[0].weightx = 4;
			gbc[0].weighty = 0.1;
			gbc[0].fill = GridBagConstraints.BOTH;
			pn.add(msgPanel,gbc[0]);
		}
		

		{	
			textArea = new JTextArea();		
			textArea.setEditable(false); //쓰기 금지
			JScrollPane scrollPane = new JScrollPane(textArea);
			// add(scrollPane);
			gbc[1].gridx = 0;
			gbc[1].gridy = 2;
			gbc[1].weightx = 4;
			gbc[1].weighty = 2;
			gbc[1].fill = GridBagConstraints.BOTH;
			pn.add(scrollPane,gbc[1]);
		}

		{
			JPanel dust = initDustMenuPanel();
			// add(dust,BorderLayout.NORTH);
			gbc[2].gridx = 0;
			gbc[2].gridy = 0;
			gbc[2].weightx = 4;
			gbc[2].weighty = 1;
			gbc[2].fill = GridBagConstraints.BOTH;
			pn.add(dust,gbc[2]);
		}

		{
			JPanel dp = initDustMainPanel();
			dp.setBackground(Color.white);
			gbc[3].gridx = 0;
			gbc[3].gridy = 1;
			gbc[3].weightx = 4;
			gbc[3].weighty = 2;
			gbc[3].fill = GridBagConstraints.BOTH;
			pn.add(dp,gbc[3]);
		}
		
		// fr.setContentPane(pn);
		add(pn);
		setSize(1000,600);
    	setVisible(true);
    }

    void sendMessage() {	
        MainFrame.lock.lock();
        String msg = tfMsg.getText();
		if(msg==null || msg.isEmpty()){
			MainFrame.lock.unlock();
		}
		else{
			MainFrame.newDeposit.signal();
			MainFrame.lock.unlock();
		}
    }
}





class DustTalkUI extends JFrame{
	private int BUTTON_SIZE = 5;
	private MainFrame mf;
	public DustTalkUI(MainFrame mf, String selected){
		this.mf = mf;

		DustMain dustMain = new DustMain();



        JPanel pn = new JPanel();

        GridBagConstraints[] gbc = new GridBagConstraints[BUTTON_SIZE];

        GridBagLayout gbl = new GridBagLayout();
        pn.setLayout(gbl);


		for (int i = 0; i < BUTTON_SIZE; i++) {
            /* Button 초기화 */
            // bt[i] = new JButton("Button" + i);

            /* GridBagConstraints 초기화 */
            gbc[i] = new GridBagConstraints();
        }

		pn.setBackground(Color.WHITE);


		{
			JButton backButton = new JButton("뒤로 가기");
			backButton.setPreferredSize(new DimensionUIResource(100,50));
			gbc[0].gridx = 2;
			gbc[0].gridy = 2;
			gbc[0].gridwidth =100;
			gbc[0].gridheight = 50;

			backButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			pn.add(backButton,gbc[0]);
		}
		// fr.setContentPane(pn);
		add(pn);
		setSize(1000,600);
		setVisible(true);
	}
}