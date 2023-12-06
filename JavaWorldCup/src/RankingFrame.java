import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class RankingFrame extends JFrame{
	public RankingFrame() {
		super("2022 JAVA World Cup");
		
		add(createTabbedPane());
		setSize(800,630);
		setVisible(true);
	}
	
	private JTabbedPane createTabbedPane() {
		JTabbedPane pane = new JTabbedPane();
		pane.addTab("EASY", new RankingPanel(0)); // easy 랭킹 페이지
		pane.addTab("NORMAL", new RankingPanel(1)); // normal 랭킹 페이지
		pane.addTab("HARD", new RankingPanel(2)); // hard 랭킹 페이지
		
		return pane;
	}
	
	class RankingPanel extends JPanel{
		private ImageIcon backIcon = new ImageIcon("images/ranking.jpg"); // 배경 이미지
		private Image backImg = backIcon.getImage();
		private ImageIcon trophyIcon = new ImageIcon("images/trophy.png"); // 중앙 트로피 이미지
		private Image trophyImg = trophyIcon.getImage();
		private int level; // Easy:0, normal: 1, Hard:2
		private RankingList rl;
		
		public RankingPanel(int level) { 
			this.level = level;
			rl = new RankingList(level);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Font numberFont = new Font("Abalone Smile", Font.BOLD,60);
			g.drawImage(backImg, 0, 0, this.getWidth(), this.getHeight(), null);
			g.drawImage(trophyImg, 330,30, 120,120,null);
			
			g.setFont(numberFont);
			g.setColor(Color.ORANGE);
			for(int i=0; i<5; i++) {
				g.drawString(Integer.toString(i+1), 100, 200+i*80);
				g.drawString(rl.getName(i), 350, 200+i*80);
				g.drawString(rl.getGoal(i), 650, 200+i*80);
			}
				
		}
	}
}
