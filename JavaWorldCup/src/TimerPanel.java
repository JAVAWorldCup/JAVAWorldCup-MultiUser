import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TimerPanel extends JPanel {
	private JLabel timer = new JLabel("0 Min");
	private JLabel state;
	private int count = 0;
	private int round;
	private int level;
	public TimerThread tgt = new TimerThread();
	private ScorePanel sp;
	private ImageIcon backIcon = new ImageIcon("images/back.jpeg");
	private Image backImg = backIcon.getImage();
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backImg, 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	public class TimerThread extends Thread {
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
					sleep(1000); // 0.8초동안 잠자기
					if(flag == true)
						waitFlag();
					count++; 
					timer.setText(Integer.toString(count) + " Min");
//					if(count == 45) { // 하프타임
//						state.setText("Half-time");
//					}
					// 난이도에 따라 상대 골 찬스 시간이 달라짐
//					if(level == 0) { //easy
//						if(count % 30 == 0)
//							opponentGoal();
//					}else if(level == 1) { //normal
//						if(count % 15 == 0)
//							opponentGoal();
//					}else if(level == 2) { // hard
//						if(count % 10 == 0)
//							opponentGoal();
//					}
					
					if(count == 90) {	// 0초 되면 종료
						state.setLocation(20, 170);
						state.setText("Game Over");
					}
				} 
				catch (InterruptedException e) {
					return;
				} 
			}
		}
	}
	
	public TimerPanel(ScorePanel scorePanel, int round, int level, String name) {
		sp = scorePanel;
		this.round = round;
		this.level = level;
		setLayout(null);
		
		timer.setFont(new Font("Abalone Smile", Font.BOLD, 40));
		timer.setForeground(Color.WHITE);
		timer.setLocation(50, 50);
		timer.setSize(200, 50);
		add(timer);
		
		state = new JLabel(name);
		state.setFont(new Font("Abalone Smile", Font.BOLD, 35));
		state.setForeground(Color.WHITE);
		state.setLocation(25, 170);
		state.setSize(200, 50);
		add(state);
		tgt.start();
	}
	private void opponentGoal() {
		int check = (int)(Math.random() *100)+1;
		// Stage에 따라 상대가 골 넣을 확률이 달라짐
		if(round == 8) { // 8강전 20%
			if(check % 5 == 0)
				sp.increaseYourGoal();
		}
		else if(round == 4) { // 4강전 33%
			if(check % 3 == 0)
				sp.increaseYourGoal();
		}
		else if(round == 2) { // 결승전 50%
			if(check % 2 == 0)
				sp.increaseYourGoal();
		}
	}
	public int getCount() {return count;}
}
