import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

//깃허브 커밋 날짜 수정?
public class GuiMain extends Thread {
	public static DustMain  dustMain  = null;
	public static ChatMain chat = null;
	public static MainFrame mainFrame = null;
	public static Timer timer = null;

    public void run() {
		
		// * GUI Thread
		mainFrame = new MainFrame();
		mainFrame.start();

		// * Timer Thread
		timer = new Timer();
		timer.run();

		// * DustApi Thread
		dustMain = new DustMain();
		dustMain.start();

		// * Timer Thread
		chat = new ChatMain(InitUI.textArea, InitUI.tfMsg);
		chat.start();
    }
}


class MainFrame extends Thread{
	public static String location = "종로구";

	// * Chat Lock & Condition
	public static Lock lock = new ReentrantLock();
	public static Condition newDeposit = lock.newCondition();

	// * Api Lock & Condition
	public static Lock ApiLock = new ReentrantLock();
	public static Condition ApiCondition = ApiLock.newCondition();


	public void run(){
		try {
			new InitUI();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}



class InitUI extends JFrame{
    public static JTextArea textArea;
	public static JTextField tfMsg;	


	public static JLabel Pm10 = new JLabel("");
	public static JLabel Pm2_5 = new JLabel("");
	public static JLabel labelTimer = new JLabel("timer");

	public static JScrollBar cursor = null;

	private ArrayList<JButton> locButtons = new ArrayList();


	private void initFetch(){
				
		MainFrame.ApiLock.lock();
		MainFrame.location = "종로구";
		MainFrame.ApiCondition.signal();
		MainFrame.ApiLock.unlock();

		InitUI.textArea.append("[ADMIN]" + " [" +LocalTime.now() +"] : Selected  " + "\"종로구\"\n");


		for(int i=0;i<locButtons.size();i++){
			locButtons.get(i).setBackground(Color.WHITE);
			locButtons.get(i).setForeground(Color.BLACK);
		}

		locButtons.get(0).setBackground(Color.BLUE);
		locButtons.get(0).setForeground(Color.WHITE);
		
		GuiMain.timer.initTimer();
		return;
	}

	// * Constructor
    InitUI() throws InterruptedException{
        JPanel pn = new JPanel();
		setTitle("Dust Talker");
		GridBagConstraints[] gbc = new GridBagConstraints[5];
        GridBagLayout gbl = new GridBagLayout();
        pn.setLayout(gbl);
		for (int i = 0; i < 5; i++) {
            gbc[i] = new GridBagConstraints();
        }
		// * Message Input UI
		{
			JPanel msgPanel = initMsgPanel();
			gbc[0].gridx = 0;
			gbc[0].gridy = 5;
			gbc[0].weightx = 4;
			gbc[0].weighty = 0.1;
			gbc[0].fill = GridBagConstraints.BOTH;
			pn.add(msgPanel,gbc[0]);
		}
		// * Message Area UI
		{	
			textArea = new JTextArea();		
			textArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(textArea);
			cursor = scrollPane.getVerticalScrollBar();
			gbc[1].gridx = 0;
			gbc[1].gridy = 2;
			gbc[1].weightx = 4;
			gbc[1].weighty = 2;
			gbc[1].fill = GridBagConstraints.BOTH;
			pn.add(scrollPane,gbc[1]);
		}
		// * Dust Menu UI
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
		// * Dust Main UI
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

		initFetch();

		add(pn);
		setSize(1000,600);
    	setVisible(true);
    }

	// * Message Input UI
    private JPanel initMsgPanel(){
        JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
		
        tfMsg = new JTextField();
		JButton btnSend = new JButton("send");
		
        msgPanel.add(tfMsg, BorderLayout.CENTER);
		msgPanel.add(btnSend, BorderLayout.EAST);

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

	// * Add Seoul Location in locs
	private void addLocation(String[] locs, JPanel dust){
	
		for(int i=0;i<locs.length;i++){
			String location = locs[i];

			JButton temp = new JButton(location);
			temp.setFont(new Font("Serif",Font.BOLD,15));
			temp.setPreferredSize(new DimensionUIResource(100, 30));
			temp.setBackground(Color.LIGHT_GRAY);
			temp.setForeground(Color.BLACK);
			
			temp.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					InitUI.textArea.append("[ADMIN]" + " [" +LocalTime.now() +"] : Selected  " + "\"" + location + "\"\n");
					cursorMaximum();

					MainFrame.ApiLock.lock();
					MainFrame.location = location;
					MainFrame.ApiCondition.signal();
					MainFrame.ApiLock.unlock();
	
					for(int i=0;i<locButtons.size();i++){
						locButtons.get(i).setBackground(Color.WHITE);
						locButtons.get(i).setForeground(Color.BLACK);
					}

					temp.setBackground(Color.BLUE);
					temp.setForeground(Color.WHITE);

					
					GuiMain.timer.initTimer();
				
				}
				
			});
			locButtons.add(temp);
			dust.add(temp);
		}
		return;
	}

	// * BLUE, GREEN, YELLOW, RED LINE UI
	private void addCriterioLine(){
		JLabel blueLabel = new JLabel("          ");
		JLabel greenLabel = new JLabel("          ");
		JLabel yellowLabel = new JLabel("          ");
		JLabel redLabel = new JLabel("          ");


		JLabel blueLabelEx = new JLabel("좋음");
		JLabel greenLabelEx = new JLabel("보통");
		JLabel yellowLabelEx = new JLabel("나쁨");
		JLabel redLabelEx = new JLabel("매우 나쁨");

		// * blue
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
		
		// * green
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

		// * yellow
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

		// * red
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

	// * Dust Main UI
	private JPanel initDustMainPanel() throws InterruptedException{
		JPanel dustMain = new JPanel();

        GridBagConstraints[] gbc_main = new GridBagConstraints[5];

        GridBagLayout gbl = new GridBagLayout();
        dustMain.setLayout(gbl);

		for (int i = 0; i < 5; i++) {
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
			labelTimer.setBackground(Color.WHITE);
			labelTimer.setForeground(Color.BLACK);
			labelTimer.setOpaque(true);
			labelTimer.setLocation(770,260);
			labelTimer.setFont(new Font("Serif",Font.BOLD,20));
			labelTimer.setSize(200,50);
			this.add(labelTimer);
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


	


	// * Move Cursor To End
	public static void cursorMaximum(){
		cursor.setValue(cursor.getMaximum());
		return;
	}

	// * Send Chat
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
		cursorMaximum();
    }
}


class Timer extends Thread{
	int countdownStarter = 60 * 1000;
	int guard = 60;
	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public Timer(){
		countdownStarter = 60 * 1000;	
		guard = 60;
	}

	// * initiating
	public void initTimer(){
		countdownStarter = 60 * 1000;	
		guard = 60;
	}

	
	// * Correcting Timer In A Second
	final Runnable timerGuard = new Runnable() {
		@Override
		public void run() {
			guard--;
			countdownStarter = guard * 1000;
			if(guard == 0){
				InitUI.textArea.append("[ADMIN][" + LocalTime.now() + "] : DUST UPDATED\n");
				InitUI.cursorMaximum();
			}
			else if(guard < 0 ){
				guard =60;
			}
		}
		
	};

	// * Timer In A Ms
	final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			float a = (float)countdownStarter/1000.0f;
			InitUI.labelTimer.setText("Refresh Timer : " + Float.toString(a));
			countdownStarter--;

			if (countdownStarter < 0) {
				countdownStarter =  60 * 1000;
			}
		}
	};

	public void run(){
		scheduler.scheduleAtFixedRate(runnable, 0, 1,TimeUnit.MILLISECONDS );
		scheduler.scheduleAtFixedRate(timerGuard, 0, 1,TimeUnit.SECONDS );
	}
}