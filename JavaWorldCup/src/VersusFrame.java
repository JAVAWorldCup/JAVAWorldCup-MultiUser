import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VersusFrame extends JFrame {
	private ImageIcon myIcon;
	private Image myImg;
	private ImageIcon opponentIcon;
	private Image opponentImg;
	private ImageIcon backGroundIcon = new ImageIcon("images/versus.jpg"); // 배경 이미지
	private Image backGroundImg = backGroundIcon.getImage();
	
	private QuterFinalPanel qfp;
	private SemiFinalPanel sfp;
	private FinalPanel fp;
	
	private String name;
	private String opponentName;
	
	public VersusFrame(int index, String name, String country, String opponentName, String opponentCountry) {
		super(name + ": 2022 JAVA World Cup");
		
		this.name = name;
		this.opponentName = opponentName;
		System.out.println(country + opponentCountry);
		setImage(country, true);
		setImage(opponentCountry, false);
		
		qfp = new QuterFinalPanel();
		sfp = new SemiFinalPanel();
		fp = new FinalPanel();
		
		if(index == 8) 
			setContentPane(qfp);
		else if(index == 4)
			setContentPane(sfp);
		else
			setContentPane(fp);
		setSize(800,630);
		setVisible(true);
	}
	
	private void setImage(String country, boolean index) {
		System.out.println("versusPanel:" + country);
		if(index) {
			if(country.equals("Korea")) {
				myIcon = new ImageIcon("images/korea.jpeg");
				myImg = myIcon.getImage();
			}
			else if(country.equals("France")) {
				myIcon = new ImageIcon("images/france.png");
				myImg = myIcon.getImage();
			}
			else if(country.equals("Brazil")) {
				myIcon = new ImageIcon("images/brazil.png");
				myImg = myIcon.getImage();
			}
			else {
				myIcon = new ImageIcon("images/japan.jpeg");
				myImg = myIcon.getImage();
			}
		}
		else {
			if(country.equals("Korea")) {
				opponentIcon = new ImageIcon("images/korea.jpeg");
				opponentImg = opponentIcon.getImage();
			}
			else if(country.equals("France")) {
				opponentIcon = new ImageIcon("images/france.png");
				opponentImg = opponentIcon.getImage();
			}
			else if(country.equals("Brazil")) {
				opponentIcon = new ImageIcon("images/brazil.png");
				opponentImg = opponentIcon.getImage();
			}
			else {
				opponentIcon = new ImageIcon("images/japan.jpeg");
				opponentImg = opponentIcon.getImage();
			}
		}
		
	}
	// 8강전
	class QuterFinalPanel extends JPanel{
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// 이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(myImg, 30, 90, 300, 200, null);
			g.drawImage(opponentImg, 470, 310, 300, 200, null);
			
			// 국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString(name, 40, 330);
			g.drawString(opponentName, 570, 550);
			
			// 8강전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("QUTER-FINAL", 380,150);
		}
	}
	// 4강전
	class SemiFinalPanel extends JPanel{
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(myImg, 30, 90, 300, 200, null);
			g.drawImage(opponentImg, 470, 310, 300, 200, null);
			
			//국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString(name, 40, 330);
			g.drawString(opponentName, 570, 550);
			
			// 4강전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("SEMI-FINAL", 420,150);
		}
		
	}
	//결승전
	class FinalPanel extends JPanel{
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(myImg, 30, 90, 300, 200, null);
			g.drawImage(opponentImg, 470, 310, 300, 200, null);
			
			//국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString(name, 40, 330);
			g.drawString(opponentName, 570, 550);
			
			// 결승전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("FINAL", 520,150);
		}
		
	}
}
