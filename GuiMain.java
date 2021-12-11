
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;


public class GuiMain extends Thread {

	public static DustMain  dustMain  = null;
	public static ChatMain chat = null;
	public static InitUI initUI= null;
	public static MainFrame mainFrame = null;

	public GuiMain(){

		dustMain = new DustMain();
		dustMain.run("종로구");
		mainFrame = new MainFrame();
	}

    public void run() {
		mainFrame.run();
		chat = new ChatMain(InitUI.textArea, InitUI.tfMsg);
		chat.run();
    }
}


class MainFrame extends Thread{
	public InitUI initUI = null;

	public static String location = "종로구";

	// Create a lock
	public static Lock lock = new ReentrantLock();
	// Create a condition
	public static Condition newDeposit = lock.newCondition();


	public void run(){
		try {
			new InitUI();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


class InitUI extends JFrame{
    public static JTextArea textArea; //멤버 참조변수

	private final static int BUTTON_SIZE = 5;

	public static JTextField tfMsg;	

	public static JLabel Pm10 = new JLabel("1.0");
	public static JLabel Pm2_5 = new JLabel("2.0");
	public static JLabel O3 = new JLabel("2.0");
	public static JLabel NO2 = new JLabel("2.0");
	public static JLabel CO = new JLabel("2.0");
	public static JLabel SO2 = new JLabel("2.0");

	public static JPanel dp= null;

	public JScrollBar cursor = null;

    


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

	private ArrayList<JButton> locButtons = new ArrayList();
	private void addLocation(String[] locs, JPanel dust){
	
		for(int i=0;i<locs.length;i++){
			String location = locs[i];

			JButton temp = new JButton(location);
			temp.setFont(new Font("Serif",Font.BOLD,15));
			temp.setPreferredSize(new DimensionUIResource(100, 30));
			temp.setBackground(Color.WHITE);
			temp.setForeground(Color.BLACK);
			
			temp.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					GuiMain.dustMain.run(location);
					
					for(int i=0;i<locButtons.size();i++){
						locButtons.get(i).setBackground(Color.WHITE);
						locButtons.get(i).setForeground(Color.BLACK);
					}

					temp.setBackground(Color.BLUE);
					temp.setForeground(Color.WHITE);
				
				}
				
			});
			locButtons.add(temp);
			dust.add(temp);
		}
		return;
	}

	private void addCriterioLine(){
		JLabel blueLabel = new JLabel("          ");
		JLabel greenLabel = new JLabel("          ");
		JLabel yellowLabel = new JLabel("          ");
		JLabel redLabel = new JLabel("          ");


		JLabel blueLabelEx = new JLabel("좋음");
		JLabel greenLabelEx = new JLabel("보통");
		JLabel yellowLabelEx = new JLabel("나쁨");
		JLabel redLabelEx = new JLabel("매우 나쁨");

		{
			blueLabel.setOpaque(true);
			blueLabel.setBackground(Color.BLUE);
			blueLabel.setLocation(10, 200);
			blueLabel.setSize(100,15);
			this.add(blueLabel);

			blueLabelEx.setLocation(150, 200);
			blueLabelEx.setSize(100,15);
			this.add(blueLabelEx);
		}
		
		{
			greenLabel.setOpaque(true);
			greenLabel.setBackground(Color.GREEN);
			greenLabel.setLocation(10, 230);
			greenLabel.setSize(100,15);
			this.add(greenLabel);

			greenLabelEx.setLocation(150, 230);
			greenLabelEx.setSize(100,15);

			this.add(greenLabelEx);
		}

		{
			yellowLabel.setOpaque(true);
			yellowLabel.setBackground(Color.YELLOW);
			yellowLabel.setLocation(10, 260);
			yellowLabel.setSize(100,15);
			this.add(yellowLabel);

			yellowLabelEx.setLocation(150, 260);
			yellowLabelEx.setSize(100,15);
			this.add(yellowLabelEx);
		}

		{
			redLabel.setOpaque(true);
			redLabel.setBackground(Color.RED);
			redLabel.setLocation(10, 290);
			redLabel.setSize(100,15);
			this.add(redLabel);

			redLabelEx.setLocation(150, 290);
			redLabelEx.setSize(100,15);
			this.add(redLabelEx);
		}
	}

	private JPanel initDustMainPanel() throws InterruptedException{
		JPanel dustMain = new JPanel();

        GridBagConstraints[] gbc_main = new GridBagConstraints[BUTTON_SIZE];

        GridBagLayout gbl = new GridBagLayout();
        dustMain.setLayout(gbl);

		for (int i = 0; i < BUTTON_SIZE; i++) {
            gbc_main[i] = new GridBagConstraints();
        }

		dustMain.setBackground(Color.BLUE);
		
		JLabel labelPm10 = new JLabel("        PM 10");
		JLabel labelPm2_5 = new JLabel("        PM 2.5");

		{
			labelPm10.setBackground(Color.RED);
			labelPm10.setFont(new Font("Serif",Font.BOLD,30));
			labelPm10.setForeground(Color.RED);
			labelPm10.setLocation(10, 130);
			labelPm10.setOpaque(true);
			labelPm10.setBackground(Color.PINK);
			labelPm10.setSize(200,50);

			this.add(labelPm10);
		}

		{
			Pm10.setFont(new Font("Serif",Font.BOLD,30));
			Pm10.setLocation(210, 130);
			Pm10.setSize(100,50);
			Pm10.setOpaque(true);
			this.add(Pm10);
		}

		{
			labelPm2_5.setFont(new Font("Serif",Font.BOLD,30));
			labelPm2_5.setForeground(Color.BLUE);

			labelPm2_5.setLocation(450, 130);
			labelPm2_5.setOpaque(true);
			labelPm2_5.setBackground(Color.PINK);
			labelPm2_5.setSize(200,50);
			this.add(labelPm2_5);
		}

		{
			Pm2_5.setFont(new Font("Serif",Font.BOLD,30));
			Pm2_5.setOpaque(true);
			Pm2_5.setLocation(650, 130);
			Pm2_5.setSize(100,50);
			this.add(Pm2_5);
		}

		addCriterioLine();

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

    InitUI() throws InterruptedException{
        JPanel pn = new JPanel();
       
		GridBagConstraints[] gbc = new GridBagConstraints[BUTTON_SIZE];

        GridBagLayout gbl = new GridBagLayout();
        pn.setLayout(gbl);

		for (int i = 0; i < BUTTON_SIZE; i++) {
            gbc[i] = new GridBagConstraints();
        }
		
		
		{
			JPanel msgPanel = initMsgPanel();
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
			cursor = scrollPane.getVerticalScrollBar();
			gbc[1].gridx = 0;
			gbc[1].gridy = 2;
			gbc[1].weightx = 4;
			gbc[1].weighty = 2;
			gbc[1].fill = GridBagConstraints.BOTH;
			pn.add(scrollPane,gbc[1]);
		}

		{
			JPanel dust = initDustMenuPanel();
			dust.setBackground(Color.WHITE);
			gbc[2].gridx = 0;
			gbc[2].gridy = 0;
			gbc[2].weightx = 4;
			gbc[2].weighty = 1;
			gbc[2].fill = GridBagConstraints.BOTH;
			pn.add(dust,gbc[2]);
		}

		{
			dp = initDustMainPanel();
			dp.setBackground(Color.white);
			gbc[3].gridx = 0;
			gbc[3].gridy = 1;
			gbc[3].weightx = 4;
			gbc[3].weighty = 2;
			gbc[3].fill = GridBagConstraints.BOTH;
			pn.add(dp,gbc[3]);
		}
		
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
		cursor.setValue(cursor.getMaximum());
    }
}

