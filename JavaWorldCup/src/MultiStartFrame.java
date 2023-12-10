import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;

public class MultiStartFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userName;

	public MultiStartFrame(){
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton waitingRoomBtn = new JButton("로그인");
		waitingRoomBtn.setBounds(161, 148, 97, 23);
		contentPane.add(waitingRoomBtn);
		waitingRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = userName.getText();//텍스트필드의 내용을 가져온다.
				name = name.replaceAll(",", " ");//,문자를 공백으로 바꿈, 이름 저장시 오류방지
				if(name.equals("")==false) {//공백이 아니면 게임 시작
					WaitingRoom wr = new WaitingRoom(name);
					wr.setLocationRelativeTo(null);//프레임 화면 중앙에 오게 하기
					wr.setVisible(true);
					dispose();//dialog과 메뉴 프레임 창을 닫는다.
					//frame.dispose();
				}
			}
		});
		
		userName = new JTextField();
		userName.setBounds(154, 104, 116, 21);
		contentPane.add(userName);
		userName.setColumns(10);
	}
}
