import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ShootingPanel extends JPanel{
	private int barSize = 0;
	private int maxBarSize = 100;
	private GaugeLabel gaugeBar = new GaugeLabel();
	private ScorePanel sp;
	private JLabel goalLabel = new JLabel("GOAL!!!");
	private ImageIcon icon = new ImageIcon("images/shoot.jpg");
	private Image img = icon.getImage();
	private ImageIcon playerIcon = new ImageIcon("images/playerLeft.png");
	private Image playerImg = playerIcon.getImage();
	private int round =8;
	
	public ShootingPanel(ScorePanel scorePanel, int round) {
		sp = scorePanel;
		this.round = round;
		setLayout(null);
		gaugeBar.setBackground(Color.BLUE);
		gaugeBar.setOpaque(true);
		gaugeBar.setLocation(20, 50);
		gaugeBar.setSize(550,30);
		add(gaugeBar);
		
		goalLabel.setFont(new Font("Abalone Smile", Font.PLAIN, 100));
		goalLabel.setForeground(Color.ORANGE);
		goalLabel.setSize(300, 200);
		goalLabel.setLocation(150, 70);
		goalLabel.setVisible(false);
		add(goalLabel);
		
		this.setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 32)
					gaugeBar.fill();		
			}
		});
		setSize(350,200);
		setVisible(true);
		ConsumerThread th = new ConsumerThread(gaugeBar);
		th.start();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0,0,this.getWidth(),this.getHeight(),null);
		g.drawImage(playerImg, 50, 200, 150, 250, null);
	}
	
	class GaugeLabel extends JLabel {
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.GREEN);
			int width = (int)((double)(this.getWidth())/maxBarSize*barSize);
			if(width==0) return;
			g.fillRect(0, 0, width, this.getHeight());
		}
		synchronized public void fill() {
			if(barSize == maxBarSize) {
				try {
					goalLabel.setVisible(true); // GOAL 단어 출력
					wait(); // gaugeBar가 꽉참 -> 스레드 중지
				}catch(InterruptedException e) {return;}
			}
			if(round == 8) // 8강전
				barSize+=10;
			else if(round == 4) // 4강전
				barSize+=5;
			else // 결승
				barSize+=3;
			repaint();
			notify();
		}
		synchronized public void consume() {
			if(barSize == 0) {
				try {
					wait();
				} catch(InterruptedException e) { return; }
			}
			barSize--;
			repaint();
			notify();
		}
		public int getBarSize() {return barSize;}
	}
	class ConsumerThread extends Thread{
		private GaugeLabel gaugeBar;
		public ConsumerThread(GaugeLabel gaugeBar) {
			this.gaugeBar = gaugeBar;
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
					sleep(100);
					if(gaugeBar.getBarSize() > 100) { // gaugeBar 꽉차면
						sp.increaseMyGoal();// 골 증가
						goalLabel.setVisible(true);
						waitFlag();
					}
					gaugeBar.consume(); // 가만히 있으면 guage bar 줄어듬
				}catch(InterruptedException e) { return; }
			}
		}
	}


}
