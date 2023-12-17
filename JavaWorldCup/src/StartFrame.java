import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class StartFrame extends JFrame{
	// Component
	private JButton startBtn = new JButton("START");
	private JButton addWordsBtn = new JButton("WORDS");
	private JButton rankingBtn = new JButton("Ranking");
	private JLabel titleLabel = new JLabel("2022 Java World Cup");
	private JButton muteBtn = new JButton("Mute");
	private JButton audioBtn = new JButton("Audio");
	
	private String [] levelArray = {"Easy", "Normal", "Hard"};
	private JComboBox<String> levelCombo = new JComboBox<String>(levelArray);
	
	private int level;
	private int totalGoals=0; // 게임 하는 동안 넣은 골 수
	
	private GameFrame gf;
	private MainThread mt = new MainThread();
	private VersusFrame vf;
	private ResultFrame rf;
	
	private Clip clip; // audio 파일 재생
	
	public StartFrame() {
		super("2022 JAVA World Cup");
		setContentPane(new BackPanel());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		
		// 게임 제목
		titleLabel.setFont(new Font("Abalone Smile", Font.BOLD,75));
		titleLabel.setSize(800, 100);
		titleLabel.setLocation(50, 100);
		add(titleLabel);
		
		Font btnFont = new Font("Abalone Smile", Font.BOLD, 30);
		
		//게임 스타트 버튼
		startBtn.setFont(btnFont);
		startBtn.setSize(200, 50);
		startBtn.setLocation(300, 250);
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mt.start();
				clip.stop();
			}
		});
		add(startBtn);
		
		// 난이도 설정 
		levelCombo.setFont(new Font("Abalone Smile", Font.BOLD, 21));
		levelCombo.setSize(200,50);
		levelCombo.setLocation(300, 320);
		levelCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				 @SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				level = cb.getSelectedIndex();
			}
		});
		add(levelCombo);
		
		// 단어 추가 버튼
		addWordsBtn.setFont(btnFont);
		addWordsBtn.setSize(200, 50);
		addWordsBtn.setLocation(300, 390);
		addWordsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = JOptionPane.showInputDialog("Add word");
				writeWord(text);
			}
		});
		add(addWordsBtn);
		
		// 랭킹 버튼
		rankingBtn.setFont(btnFont);
		rankingBtn.setSize(200, 50);
		rankingBtn.setLocation(300, 460);
		rankingBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new RankingFrame();
			}
		});
		add(rankingBtn);
		
		// 음소거 버튼
		muteBtn.setFont(btnFont);
		muteBtn.setSize(100, 50);
		muteBtn.setLocation(550, 550);
		muteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clip.stop();
			}
		});
		add(muteBtn);
		
		// audio 재생 버튼
		audioBtn.setFont(btnFont);
		audioBtn.setSize(100, 50);
		audioBtn.setLocation(680, 550);
		audioBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clip.start();
			}
		});
		add(audioBtn);
		
		// 배경음악 재생
		loadAudio("media/main.wav");
		clip.start();
		
		setSize(800,630);
		setVisible(true);
	}

	// 배경음악 Clip에 초기화
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
	
	// 배경 이미지
	class BackPanel extends JPanel {
		private ImageIcon icon = new ImageIcon("images/mainImg2.jpg");
		private Image img = icon.getImage();
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
		}
	}
	
	// 전체 게임을 돌아가게 하는 Thread
	class MainThread extends Thread {
		private int round = 8;
		private boolean start = false; // 처음 게임을 시작하는지 아닌지 표기(true면 처음이 아님)
		@Override
		public void run() {
			while(true) {
				try {
					sleep(100);
					if(start == false) { // 처음 게임을 시작했을 때
//						vf = new VersusFrame(round); // 8강전, 4강전, 결승 대진표 표기
						setVisible(false);
						sleep(2000);
						gf = new GameFrame(round, level, "");
						vf.setVisible(false);
						start = true; // 게임을 시작했으니 true로 변경
					}
					if(gf.getGameState() == true) { // 경기 끝남
						if(gf.getGoalState()) { // 경기를 이겼을 때
							totalGoals += gf.getMyGoal(); // 해당 Stage에서 넣은 골 추가
//							rf = new ResultFrame(true); // 결과 화면
							gf.setVisible(false); // gameFrame 숨기기
							gf.stopAudio(); // gameFrame 배경음악 끄기
							sleep(2000); 
							rf.setVisible(false); // 결과 화면 숨기기
							rf.stopAudio(); // resultFrame 배경음악 끄기
							round /= 2; // 다음 라운드
							if(round == 1) { // 우승 했을 때
								String name = JOptionPane.showInputDialog("Congratulations! What's your name?"); // 이름 입력
								writeResult(name, totalGoals); // 이름과 총 골 수 저장
								new RankingFrame(); 
								gf.setVisible(false);
								interrupt(); // 스레드 종료
							}
							else {  // 4강전, 결승전일 경우
//								vf = new VersusFrame(round);
								sleep(2000);
								gf = new GameFrame(round, level, "");
								gf.setVisible(true);
								vf.setVisible(false);
							}
						}
						else { // 경기 졌을 때
//							rf = new ResultFrame(false);
							gf.setVisible(false);
							gf.stopAudio();
							sleep(2000);
							new RankingFrame();
							rf.setVisible(false);
							rf.stopAudio();
							interrupt(); // 스레드 종료
						}
					}
						
				}catch(InterruptedException e) {return;}
			}
		}
	}
	// 경기 결과를 적는 함수
	private void writeResult(String name, int totalGoals) {
		FileWriter fout = null;
		String goal = Integer.toString(totalGoals);
		try {
			if(level == 0) // 난이도: easy
				fout = new FileWriter("static/easy.txt",true);
			else if(level == 1) // 난이도: normal
				fout = new FileWriter("static/normal.txt",true);
			else if(level == 2) // 난이도: hard
				fout = new FileWriter("static/hard.txt",true);
			fout.write(name,0,name.length()); // 이름 저장
			fout.write("\r\n", 0, 2);
			fout.write(goal,0,goal.length()); // 총 골 수 저장
			fout.write("\r\n", 0, 2);
			fout.close();
		}catch(IOException e) {}
	}
	
	// 단어 추가
	private void writeWord(String text) {
		FileWriter fout = null;
		try {
			fout = new FileWriter("static/words.txt", true);
			fout.write(text,0,text.length());
			fout.write("\r\n", 0, 2);
			JOptionPane.showMessageDialog(null, text+" 저장되었습니다~", "Add word", JOptionPane.INFORMATION_MESSAGE);
			fout.close();
		}catch(IOException e) {}
	}
}
