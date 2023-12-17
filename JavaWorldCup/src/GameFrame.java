import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

public class GameFrame extends JFrame{	
	private boolean gameState = false; // true면 게임 종료
	private int round = 8;
	private int level;
	private String name;
	
	private JButton startBtn = new JButton("start");
	private JButton pauseBtn = new JButton("pause");
	private JButton exitBtn = new JButton("exit");
	private JButton muteBtn = new JButton("mute");
	private JButton audioBtn = new JButton("audio");
	
	private LineBorder border = new LineBorder(Color.BLACK, 1, true);
	private Clip clip;
	
	private ScorePanel scorePanel = new ScorePanel();
	private ShootingPanel shootingPanel = new ShootingPanel(scorePanel,round);
	private TimerPanel timerPanel;
	private GamePanel gamePanel;
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private InetAddress ip;
	final static int ServerPort = 5019;
	
	public GameFrame(int round, int level, String name) {
		super(name + ": 2022 JAVA World Cup");
		this.round = round;
		this.level = level;
		this.name = name;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		makeToolBar();
		makeContent();
		
		MainThread th = new MainThread();
		th.start();
		runAudio();
		
		setSize(800,630);
		setVisible(true);
		
		try {
			ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, ServerPort);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            
            SendMessage(name);
            ListenNetwork net = new ListenNetwork();
            net.start();
        } catch (NumberFormatException | IOException e) {
            e.printStackTrace();
            System.out.println("Socket Not Connected");
        }
	}
	
	public boolean getGameState() { return gameState; } 
	public boolean getGoalState() { return scorePanel.getGoalState();}
	public int getMyGoal() {return scorePanel.getMyGoal();}
	
	private void loadAudio(String pathName) {
		try {
			clip = AudioSystem.getClip();
			File audioFile = new File(pathName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioStream);
		}
		catch(LineUnavailableException e) {}
		catch(UnsupportedAudioFileException e) {}
		catch(IOException e) {}
	}
	private void runAudio() {
		if(round == 8) {
			loadAudio("media/quotar.wav");
			clip.start();
		}
		else if(round == 4) {
			loadAudio("media/semi.wav");
			clip.start();
		}
		else {
			loadAudio("media/final.wav");
			clip.start();
		}
	}
	public void stopAudio() { clip.stop();}
	
	private void makeToolBar() {
		// ToolBar는 contentPane에 붙임
		JToolBar tBar = new JToolBar(); // JToolBar는 핸들링 가능 BorderLayout이기 떄문에 n,s,e,w,center 지정 가능
		Font btnFont = new Font("Abalone Smile", Font.PLAIN, 20);
		
		//startBtn(게임 스타트)
//		startBtn.setFont(btnFont);
//		startBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				timerPanel.tgt.resumeThread();
//				gamePanel.groundPanel.wt.resumeThread();
//				scorePanel.restartGame();
//			}
//		});
//		tBar.add(startBtn);
//		
//		//pauseBtn(게임 중단)
//		pauseBtn.setFont(btnFont);
//		pauseBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				timerPanel.tgt.stopFlag();
//				gamePanel.groundPanel.wt.stopFlag();
//				scorePanel.pauseGame();
//			}
//		});
//		tBar.add(pauseBtn);
		
		//exitBtn(메인 화면 돌아가기)
//		exitBtn.setFont(btnFont);
//		exitBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				new StartFrame();
//				setVisible(false);
//			}
//		});
//		tBar.add(exitBtn);
		
		muteBtn.setFont(btnFont);
		muteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clip.stop();
			}
		});
		tBar.add(muteBtn);
		
		audioBtn.setFont(btnFont);
		audioBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clip.start();
			}
		});
		tBar.add(audioBtn);
		
		
		startBtn.setFocusable(false);
		pauseBtn.setFocusable(false);
		exitBtn.setFocusable(false);
		muteBtn.setFocusable(false);
		audioBtn.setFocusable(false);
		tBar.setBorder(border);
		
		tBar.setSize(800,30);
		tBar.setLocation(0,0);
		getContentPane().add(tBar);
	}
	// timerPanel, scorePanel 붙이기
	private void makeContent() {
		timerPanel = new TimerPanel(scorePanel, round, level, name);
		timerPanel.setSize(200, 310);
		scorePanel.setSize(200, 310);
		
		scorePanel.setLocation(600, 30);
		timerPanel.setLocation(600, 300);
		
		timerPanel.setBorder(border);
		scorePanel.setBorder(border);
		
		getContentPane().add(timerPanel);
		getContentPane().add(scorePanel);
		
		showGamePanel();
	}
	
	class MainThread extends Thread {
		private boolean flag = false;
		public boolean getFlag() {return flag;}
		public void stopThread() {flag = true;}
		
		synchronized public void resumeThread() {
			flag = false;
			this.notify();
		}
		synchronized private void waitFlag() {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		@Override
		public void run() {
			while(true) {
				try {
					sleep(100);
					// point가 5가 되면 shoot chance
					if(scorePanel.getScore() == 5) { 
						showShootingPanel();
						scorePanel.setShootingChance();
					}
					// shootingPanel 골 넣었을 때 -> point 0, gamePanel 전
					if(scorePanel.getIndex() == 1) {
						sleep(1000);
						scorePanel.setScore();
						SendMessage("Goal");
						showGamePanel();
					}
					// 경기 종료
					if(timerPanel.getCount() == 90) {
						timerPanel.tgt.stopFlag();
						gamePanel.groundPanel.wt.stopFlag();
						scorePanel.pauseGame();
						gameState = true; // 경기 종료
					}
				}catch(InterruptedException e) {return;}
			}
		}
	}
	// panel 전환(gamePanel -> shootingPanel)
	private void showShootingPanel() {
		shootingPanel = new ShootingPanel(scorePanel,round);
		shootingPanel.setSize(600, 600);
		shootingPanel.setLocation(0,50);
		shootingPanel.setBorder(border);
		getContentPane().add(shootingPanel);
		gamePanel.setVisible(false);
	}
	// panel 전환(gamePanel -> shootingPanel)
	private void showGamePanel() {
		gamePanel = new GamePanel(scorePanel, round, level, name);
		gamePanel.setSize(600, 600);
		gamePanel.setLocation(0,30);
		gamePanel.setBorder(border);
		getContentPane().add(gamePanel);
		shootingPanel.setVisible(false);
	}
	
	 class ListenNetwork extends Thread {
	        public void run() {
	            while (true) {
	                try {
	                    // Use readUTF to read messages
	                    String msg = dis.readUTF();
	                    String [] msgToken = msg.split("%"); 
	                    if(!msgToken[0].equals(name) && msgToken[1].equals("333"))
	                    	scorePanel.increaseYourGoal(); // 상대 골 넣었을 
	                    else if(!msgToken[0].equals(name) && msgToken[1].equals("444"))
	                    	scorePanel.setScoreZero(); // 상대의 공격 - Red Card
	                    else if(!msgToken[0].equals(name) && msgToken[1].equals("111"))
	                    	scorePanel.decreaseScore(); // 상대의 공격 - Yellow Card
	                    
	                } catch (IOException e) {
//	                    AppendText("dis.read() error");
	                    try {
	                        dos.close();
	                        dis.close();
	                        socket.close();
	                        break;
	                    } catch (Exception ee) {
	                        break;
	                    }
	                }
	            }
	        }
	    }
	 public void SendMessage(String msg) {
	        try {
	            // Use writeUTF to send messages
	            dos.writeUTF(name + "%333");
	            System.out.println("Send");
	        } catch (IOException e) {
//	            AppendText("dos.write() error");
	            try {
	                dos.close();
	                dis.close();
	                socket.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	                System.exit(0);
	            }
	        }
	    }
}