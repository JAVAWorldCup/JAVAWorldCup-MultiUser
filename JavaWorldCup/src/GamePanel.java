import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel {
	private JTextField inputField = new JTextField(20);
	private JLabel [] wordLabels = new JLabel[5];
	private JLabel redCardLabel;
	private JLabel yellowCardLabel;
	private JLabel penaltyLabel;
	
	private WordList wordList = new WordList(); // 검정색 단어(일반 단어)
	private final int WORDNUM = 5; // 단어 개수
	private final int ALLWORDNUM = 8; // 전체 단어 개수(아이템 포함)
	private Vector<Point> wordVector = new Vector<Point>(); // 일반 단어 위치
	private Vector<Point> playerWHVector = new Vector<Point>(); // 손흥민 width, height
	private Vector<Point> playerXYVector = new Vector<Point>(); // 손흥민 x,y
	public GroundPanel groundPanel = new GroundPanel();
	private ScorePanel sp;
	
	private int round; // 단계
	private int level; // 난이도
	
	public GamePanel(ScorePanel scorePanel, int round, int level) {
		sp = scorePanel;
		this.round = round;
		this.level = level;
		setLayout(null); // 배치관리자 제거
		groundPanel.setSize(600, 530);
		groundPanel.setLocation(0,0);
		add(groundPanel);
		
		// 하단 부 단어 입력 Panel
		JPanel inputPanel = new JPanel();
		inputPanel.setBackground(Color.DARK_GRAY);
		inputPanel.add(inputField);
		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField t = (JTextField)e.getSource();
				// red Card(빨간 글씨) 입력 했을 경우
				if(t.getText().equals(redCardLabel.getText())) {
					sp.setScoreZero(); // point 0으로 만들기
					String word = wordList.getBadWord();
					redCardLabel.setText(word);
					Point p = wordVector.get(5);
					p.y = 5;
					t.setText("");
				// yellow Card(노란 글씨) 입력 했을 경우
				} else if (t.getText().equals(yellowCardLabel.getText())) {
					sp.decreaseScore(); // point 1 깎기
					String word = wordList.getBadWord();
					yellowCardLabel.setText(word);
					Point p = wordVector.get(6);
					p.y = 5;
					t.setText("");
				// penalty(초록 글씨) 입력 했을 경우
				} else if (t.getText().equals(penaltyLabel.getText())) {
					sp.setScoreFive(); // point+=5
					String word = wordList.getGoodWord();
					penaltyLabel.setText(word);
					Point p = wordVector.get(7);
					p.y = 5;
					t.setText("");
				}
				// 일반(검정 글씨) 입력 했을 경우
				else {
					for(int i=0; i<WORDNUM; i++) {
						if(t.getText().equals(wordLabels[i].getText())) {
							sp.increaseScore(); // point++
							String word = wordList.getWord();
							wordLabels[i].setText(word);
							Point p = wordVector.get(i);
							p.y = 5;
							//지우기
							t.setText("");
						}
					}
				}
			}
		});
		inputPanel.setSize(600, 50);
		inputPanel.setLocation(0, 530);
		add(inputPanel);
	}
	
	class GroundPanel extends JPanel{
		public WordThread wt = new WordThread();
		// 8강전 배경 이미지
		private ImageIcon backGroundIconA = new ImageIcon("images/soccerFieldA.jpg");
		private Image backGroundImgA = backGroundIconA.getImage();
		// 4강전 배경 이미지
		private ImageIcon backGroundIconB = new ImageIcon("images/soccerFieldB.jpg");
		private Image backGroundImgB = backGroundIconB.getImage();
		// 결승 배경 이미지
		private ImageIcon backGroundIconC = new ImageIcon("images/soccerFieldC.jpg");
		private Image backGroundImgC = backGroundIconC.getImage();
		
		// 손흥민 왼쪽 이미지
		private ImageIcon playerLeftIcon = new ImageIcon("images/playerLeft.png");
		private Image playerLeftImg = playerLeftIcon.getImage();
		// 손흥민 왼쪽 이미지
		private ImageIcon playerRightIcon = new ImageIcon("images/playerRight.png");
		private Image playerRightImg = playerRightIcon.getImage();
		
		public GroundPanel() {
			//일반(검정 글씨) 초기화
			for(int i =0; i<WORDNUM; i++) {
				String word = wordList.getWord();
				wordLabels[i] = new JLabel(word);
				wordLabels[i].setFont(new Font("Abalone Smile", Font.PLAIN, 20));
				add(wordLabels[i]);
			}
			//yellow card(노란 글씨) 초기화
			String word = wordList.getBadWord();
			yellowCardLabel = new JLabel(word);
			yellowCardLabel.setFont(new Font("Abalone Smile", Font.PLAIN, 20));
			yellowCardLabel.setForeground(Color.YELLOW);
			add(yellowCardLabel);
			//red card(빨간 글씨) 초기화
			word = wordList.getBadWord();
			redCardLabel = new JLabel(word);
			redCardLabel.setFont(new Font("Abalone Smile", Font.PLAIN, 20));
			redCardLabel.setForeground(Color.RED);
			add(redCardLabel);
			//penalty(초록 글씨) 초기화
			word = wordList.getGoodWord();
			penaltyLabel = new JLabel(word);
			penaltyLabel.setFont(new Font("Abalone Smile", Font.PLAIN, 20));
			penaltyLabel.setForeground(Color.GREEN);
			add(penaltyLabel);
			
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					makeWord();
					makePlayer();
					wt.start();
					GroundPanel.this.removeComponentListener(this); // 한번만 부르게 하기
				}
			});
			
		}
		
		class WordThread extends Thread {
			private boolean flag = false;
			public void stopFlag() {flag = true;}
			synchronized public void resumeThread() {
				flag = false;
				this.notify();
			}
			synchronized public void waitFlag() {
				try {
					this.wait();
				} catch (InterruptedException e) {}
			}
			@Override
			public void run() {
				while(true) {
					try {
						sleep(500);
						if(flag == true)
							waitFlag();
						changePosition();
						repaint();
					} catch(InterruptedException e) {return;}
				}
			}
		}
		// 단어 위치 지정
		private void makeWord() {
			for(int i=0; i<ALLWORDNUM; i++) {
				int x = (int)(Math.random()*450);
				int y = (int)(Math.random()*100) + 10;
				
				wordVector.add(new Point(x,y));
			}
		}
		
		//손흥민 입력 point에 따라 움직일 위치 지정
		private void makePlayer() {
			playerWHVector.add(new Point(100, 150));
			playerXYVector.add(new Point(0, 380));
			
			playerWHVector.add(new Point(90, 130));
			playerXYVector.add(new Point(480, 370));
			
			playerWHVector.add(new Point(80, 110));
			playerXYVector.add(new Point(100, 360));
			
			playerWHVector.add(new Point(70, 90));
			playerXYVector.add(new Point(380, 340));
			
			playerWHVector.add(new Point(60, 70));
			playerXYVector.add(new Point(200, 320));
		}
		
		// 단어 바뀌는 위치
		private void changePosition() {
			for(int i=0; i<wordVector.size()-1; i++) {
				Point p = wordVector.get(i);
				int direction = Math.random() > 0.5 ? 1 : -1; // 왼쪽으로 갈지 오른쪽으로 갈지
				p.x += direction * 10;
				if(p.x < 0)
					p.x = 0;
				else if(p.x > 450)
					p.x = 450;
				p.y+=(10 + level*10); // 떨어지는 속도 조절
				if(p.y > 500) 
					p.y = 0;
			}
			// 아이템(Penalty kick)의 움직임
			Point p = wordVector.get(7);
			int direction = Math.random() > 0.5 ? 1 : -1; // 왼쪽으로 갈지 오른쪽으로 갈지
			p.x += direction * 170;
			if(p.x < 0)
				p.x = 0;
			else if(p.x > 450)
				p.x = 450;
			p.y+=(150 + level*50); // 떨어지는 속도 조절(penalty kick)
			if(p.y > 500) 
				p.y = 0;
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(round == 8)
				g.drawImage(backGroundImgA, 0, 0, this.getWidth(), this.getHeight(), null);
			else if(round == 4)
				g.drawImage(backGroundImgB, 0, 0, this.getWidth(), this.getHeight(), null);
			else if(round == 2)
				g.drawImage(backGroundImgC, 0, 0, this.getWidth(), this.getHeight(), null);
			// 손흥민 선수 움직임(짝수면 오른쪽 이미지)
			if(sp.getScore() % 2 == 0)
				g.drawImage(playerLeftImg, playerXYVector.get(sp.getScore()).x,playerXYVector.get(sp.getScore()).y,playerWHVector.get(sp.getScore()).x, playerWHVector.get(sp.getScore()).y, this);
			else  // 손흥민 선수 움직임(홀수면 왼쪽 이미지) 
				g.drawImage(playerRightImg, playerXYVector.get(sp.getScore()).x,playerXYVector.get(sp.getScore()).y,playerWHVector.get(sp.getScore()).x, playerWHVector.get(sp.getScore()).y, this);
			for(int i=0; i<WORDNUM; i++) { // 일반 단어 위치 지정
				wordLabels[i].setLocation(wordVector.get(i).x, wordVector.get(i).y);	
			}
			// 아이템 위치 지정
			yellowCardLabel.setLocation(wordVector.get(5).x, wordVector.get(5).y);
			redCardLabel.setLocation(wordVector.get(6).x, wordVector.get(6).y);
			penaltyLabel.setLocation(wordVector.get(7).x, wordVector.get(7).y);
		}
	}
}
