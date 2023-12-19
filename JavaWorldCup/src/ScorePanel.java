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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScorePanel extends JPanel{
	private int score = 0;
	private int myGoal = 0;
	private int yourGoal = 0;
	private int level;
	
	private int index = 0; // 골 넣은걸 표시, 1로 바뀌면 넣은 것
	private int pause = 0; // 게임이 끝났는지 아닌지 확인해주기 1이면 중지
	
	private JLabel scoreLabel = new JLabel("Point: " + Integer.toString(score));
	private JLabel goalLabel = new JLabel(Integer.toString(myGoal) + " : " + Integer.toString(yourGoal));
	private ImageIcon backIcon = new ImageIcon("images/back.jpeg");
	private Image backImg = backIcon.getImage();
	
	private Clip clip;
	
	public ScorePanel() {
		setLayout(null);
		scoreLabel.setSize(200, 100);
		goalLabel.setSize(100,100);
		
		scoreLabel.setLocation(35, 50);
		goalLabel.setLocation(55, 170);
		
		scoreLabel.setFont(new Font("Abalone Smile", Font.BOLD, 40));
		goalLabel.setFont(new Font("Abalone Smile", Font.BOLD, 50));
		scoreLabel.setForeground(Color.WHITE);
		goalLabel.setForeground(Color.WHITE);
		
		add(scoreLabel);
		add(goalLabel);
		loadAudio("media/dribble.wav");
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backImg, 0, 0, this.getWidth(), this.getHeight(), null);
	}
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
	public void increaseScore() {
		if(pause == 0) {
			clip.setFramePosition(0);
			clip.start(); // 드리블 소리 들리게
			score+=1;
			scoreLabel.setText("Point: " + Integer.toString(score));
		}
	}
	public void decreaseScore() { // 아이템(yellow Card)을 얻었을 때: Point:5
		score-=1;
		if(score < 0)
			score = 0;
		scoreLabel.setText("Point: " + Integer.toString(score));
	}
	public void setScoreZero() { // 아이템(red Card)을 얻었을 때: Point:5
		if(pause == 0) {
			score=0;
			scoreLabel.setText("Point: " + Integer.toString(score));
		}
	}
	
	public void setScoreFive() { // 아이템(penalty kick)을 얻었을 때: Point:5
		if(pause == 0) {
			score=5;
			scoreLabel.setText("Point: " + Integer.toString(score));
		}
	}
	public int getScore() {return score;}
	
	public void setScore() {  // Point 0으로 초기화
		index = 0;
		scoreLabel.setFont(new Font("Abalone Smile", Font.BOLD, 40));
		scoreLabel.setLocation(30, 50);
		scoreLabel.setText("Point: " + Integer.toString(score));
	}
	
	public void increaseMyGoal() {
		if(pause == 0) { // 게임이 정지 되지 않았을 떄
			myGoal +=1; // 골 증가
			index = 1; // 골이 들어갔음을 알림
			goalLabel.setText(Integer.toString(myGoal) + " : " + Integer.toString(yourGoal));
		}
	}
	
	public void increaseYourGoal() {
		if(pause == 0) { // 게임이 정지 되지 않았을 떄
			yourGoal +=1; // 상대 골 증가
			goalLabel.setText(Integer.toString(myGoal) + " : " + Integer.toString(yourGoal));
		}
	}
	
	public void setShootingChance() {
		score = 0; // point 0으로 초기화
		scoreLabel.setFont(new Font("Abalone Smile", Font.BOLD, 25));
		scoreLabel.setText("Shooting Chance!");
		scoreLabel.setLocation(5, 50);
	}
	
	public int getIndex() {return index;} // 골이 들어갔는지 안들어 갔는지(index == 1이면 들어감)
	public void pauseGame() {pause = 1;} // 경기 일시 중지
	public void restartGame() { pause = 0;} // 경기 재개
	public int getGoalState() {
		if(myGoal > yourGoal)
			return 0; // 경기 승리
		else if(myGoal < yourGoal)
			return 1;  // 경기 패배
		else
			return 2;
	}
	public int getMyGoal() {return myGoal;}
}
