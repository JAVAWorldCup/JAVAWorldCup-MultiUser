import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class MultiStartFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userName;
	public MultiStartFrame(){
		
		String[] country={"한국", "브라질", "프랑스", "일본"};
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		contentPane = new BackgroundPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		JButton waitingRoomBtn = new JButton("로그인");
		waitingRoomBtn.setBounds(171, 160, 97, 23);
		contentPane.add(waitingRoomBtn);
		
		userName = new JTextField();
		userName.setBounds(153, 83, 133, 23);
		contentPane.add(userName);
		userName.setColumns(10);
		
		JComboBox comboBox = new JComboBox(country);
		comboBox.setBounds(171, 116, 97, 23);
		contentPane.add(comboBox);
		
		waitingRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = userName.getText();//텍스트필드의 내용을 가져온다.
				name = name.replaceAll("%", "");//%문자를 공백으로 바꿈, 이름 저장시 오류방지
				if(name.equals("")==false) {//공백이 아니면 게임 시작
					String myCountry = (String) comboBox.getSelectedItem();
					WaitingRoom wr = new WaitingRoom(new Player(name, myCountry));
					wr.setLocationRelativeTo(null);//프레임 화면 중앙에 오게 하기
					wr.setVisible(true);
					dispose();//dialog과 메뉴 프레임 창을 닫는다.
					//frame.dispose();
				}
			}
		});
	}
    // BackgroundPanel 클래스 정의
    class BackgroundPanel extends JPanel {
        private ImageIcon icon = new ImageIcon("images/startBackground.png");
        private Image img = icon.getImage();

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
