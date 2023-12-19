import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ResultFrame extends JFrame{
	private MyPanel mp = new MyPanel();
	private JLabel winLabel = new JLabel("W  I  N");
	private JLabel loseLabel = new JLabel("L  O  S  E");
	private JLabel drawLabel = new JLabel("D R A W");
	private int result;
	
	private Clip clip;
	
	public ResultFrame(int result, String name) {
		super(name + ": 2022 JAVA World Cup");
		this.result = result;
		setContentPane(mp);
		setLayout(null);
		winLabel.setFont(new Font("Abalone Smile", Font.BOLD,120));
		loseLabel.setFont(new Font("Abalone Smile", Font.BOLD,100));
		drawLabel.setFont(new Font("Abalone Smile", Font.BOLD,100));
		winLabel.setForeground(Color.ORANGE);
		loseLabel.setForeground(Color.GRAY);
		drawLabel.setForeground(Color.gray);
		winLabel.setSize(400, 200);
		loseLabel.setSize(400, 200);
		drawLabel.setSize(400, 200);
		winLabel.setLocation(220,100);
		loseLabel.setLocation(200,100);
		drawLabel.setLocation(200,100);
		if(result == 0) { // win
			add(winLabel);
			loadAudio("media/applaud.wav");
			clip.start();
		}
		else if(result == 1){// lose
			loadAudio("media/lose.wav");
			add(loseLabel);
			clip.start();
		}
		else {
			loadAudio("media/lose.wav");
			add(drawLabel);
			clip.start();
		}
		
		setSize(800,630);
		setVisible(true);
	}
	// audio 종료
	public void stopAudio() { clip.stop();}
	// Clip 초기화
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
	
	class MyPanel extends JPanel{
		private ImageIcon backIcon = new ImageIcon("images/ranking.jpg");
		private Image backImg = backIcon.getImage();
		
		private ImageIcon winIcon1 = new ImageIcon("images/winImg1.png");
		private Image winImg1 = winIcon1.getImage();
		private ImageIcon winIcon2 = new ImageIcon("images/winImg2.png");
		private Image winImg2 = winIcon2.getImage();
		private ImageIcon winIcon3 = new ImageIcon("images/winImg3.png");
		private Image winImg3 = winIcon3.getImage();
		
		private ImageIcon loseIcon1 = new ImageIcon("images/loseImg1.png");
		private Image loseImg1 = loseIcon1.getImage();
		private ImageIcon loseIcon2 = new ImageIcon("images/loseImg2.png");
		private Image loseImg2 = loseIcon2.getImage();
		private ImageIcon loseIcon3 = new ImageIcon("images/loseImg3.png");
		private Image loseImg3 = loseIcon3.getImage();
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backImg, 0, 0, this.getWidth(), this.getHeight(), null);
			if(result == 0) {
				g.drawImage(winImg1, 80, 400, 100, 200, null);
				g.drawImage(winImg2, 300, 350, 150, 200, null);
				g.drawImage(winImg3, 550, 400, 100, 200, null);
			}
			else {
				g.drawImage(loseImg1, 80, 400, 100, 200, null);
				g.drawImage(loseImg2, 330, 370, 150, 200, null);
				g.drawImage(loseImg3, 600, 400, 100, 200, null);
			}
		}
	}
}
