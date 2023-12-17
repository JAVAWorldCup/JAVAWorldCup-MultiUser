import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VersusFrame extends JFrame {
	private ImageIcon koreaIcon = new ImageIcon("images/korea.jpeg"); // 한국 국기
	private Image koreaImg = koreaIcon.getImage();
	private ImageIcon backGroundIcon = new ImageIcon("images/versus.jpg"); // 배경 이미지
	private Image backGroundImg = backGroundIcon.getImage();
	
	private QuterFinalPanel qfp = new QuterFinalPanel();
	private SemiFinalPanel sfp = new SemiFinalPanel();
	private FinalPanel fp = new FinalPanel();
	
	public VersusFrame(int index) {
		super("2022 JAVA World Cup");
		if(index == 8) 
			setContentPane(qfp);
		else if(index == 4)
			setContentPane(sfp);
		else
			setContentPane(fp);
		setSize(800,630);
		setVisible(true);
	}
	// 8강전
	class QuterFinalPanel extends JPanel{
		private ImageIcon japanIcon = new ImageIcon("images/japan.jpeg");
		private Image japanImg = japanIcon.getImage();
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			// 이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(koreaImg, 30, 90, 300, 200, null);
			g.drawImage(japanImg, 470, 310, 300, 200, null);
			
			// 국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString("Republic of Korea", 40, 330);
			g.drawString("Japan", 570, 550);
			
			// 8강전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("QUTER-FINAL", 380,150);
		}
	}
	// 4강전
	class SemiFinalPanel extends JPanel{
		private ImageIcon franceIcon = new ImageIcon("images/france.png");
		private Image franceImg = franceIcon.getImage();
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(koreaImg, 30, 90, 300, 200, null);
			g.drawImage(franceImg, 470, 310, 300, 200, null);
			
			//국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString("Republic of Korea", 40, 330);
			g.drawString("France", 570, 550);
			
			// 4강전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("SEMI-FINAL", 420,150);
		}
		
	}
	//결승전
	class FinalPanel extends JPanel{
		private ImageIcon brazilIcon = new ImageIcon("images/brazil.jpeg");
		private Image brazilImg = brazilIcon.getImage();
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//이미지 그리기
			g.drawImage(backGroundImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(koreaImg, 30, 90, 300, 200, null);
			g.drawImage(brazilImg, 470, 310, 300, 200, null);
			
			//국가 이름 그리기
			g.setColor(Color.WHITE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 35));
			g.drawString("Republic of Korea", 40, 330);
			g.drawString("Brazil", 570, 550);
			
			// 결승전 그리기
			g.setColor(Color.ORANGE);
			g.setFont(new Font("Abalone Smile", Font.PLAIN, 70));
			g.drawString("FINAL", 520,150);
		}
		
	}
}
