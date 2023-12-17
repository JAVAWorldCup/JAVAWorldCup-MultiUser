import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class SampleGameFrame extends JFrame {
	final static int ServerPort = 5019;   // 포트 번호
	DataInputStream is;
	DataOutputStream os;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Room room; //방 점보
	private int userNum; //사용자 번호
	
	private JLabel player1Name;
	private JLabel player2Name;
	private JLabel player1Country;
	private JLabel player2Country;
	private JLabel player1Image;
	private JLabel player2Image;
	private JLabel player1ReadyLabel;
	private JLabel player2ReadyLabel;
	private JButton startAndReadyBtn;
	private HashMap<String, String> countryImgMap = new HashMap<String, String>(){{
		put ("-", "./images/blankCountry.png");
		put ("Korea", "./images/korea.png");
		put ("Brazil", "./images/brazil.png");
		put ("France", "./images/newFrance.png");
		put ("Japan", "./images/japan.png");
	}};
	public SampleGameFrame(Room room, Player newPlayer, int userNum) {
		this.room = room;
		this.userNum = userNum;
		room.setPlayer(newPlayer, userNum);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 736, 410);
		contentPane = new BackgroundPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel player1Panel = new JPanel();
		player1Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		player1Panel.setBackground(new Color(192, 192, 192));
		player1Panel.setBounds(82, 97, 211, 235);
		contentPane.add(player1Panel);
		player1Panel.setLayout(null);
		
		player1Name = new JLabel(room.getPlayer(1).getName());
		player1Name.setForeground(new Color(255, 255, 255));
		player1Name.setHorizontalAlignment(SwingConstants.CENTER);
		player1Name.setBounds(26, 10, 163, 30);
		player1Panel.add(player1Name);
		
		player1Country = new JLabel(room.getPlayer(1).getCountry());
		player1Country.setForeground(new Color(255, 255, 255));
		player1Country.setHorizontalAlignment(SwingConstants.CENTER);
		player1Country.setBounds(26, 44, 163, 30);
		player1Panel.add(player1Country);
		
		player1Image = new JLabel(new ImageIcon(countryImgMap.get(room.getPlayer(1).getCountry())));
		player1Image.setBounds(6, 74, 200, 120);
		player1Panel.add(player1Image);
		
		player1ReadyLabel = new JLabel("");
		player1ReadyLabel.setBounds(80, 210, 57, 15);
		player1Panel.add(player1ReadyLabel);
		
		JPanel player2Panel = new JPanel();
		player2Panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		player2Panel.setBackground(new Color(192, 192, 192));
		player2Panel.setBounds(434, 97, 211, 235);
		contentPane.add(player2Panel);
		player2Panel.setLayout(null);
		
		player2Name = new JLabel(room.getPlayer(2).getName());
		player2Name.setForeground(new Color(255, 255, 255));
		player2Name.setHorizontalAlignment(SwingConstants.CENTER);
		player2Name.setBounds(26, 10, 163, 30);
		player2Panel.add(player2Name);
		
		player2Country = new JLabel(room.getPlayer(2).getCountry());
		player2Country.setForeground(new Color(255, 255, 255));
		player2Country.setHorizontalAlignment(SwingConstants.CENTER);
		player2Country.setBounds(26, 44, 163, 30);
		player2Panel.add(player2Country);
		
		player2Image = new JLabel(new ImageIcon(countryImgMap.get(room.getPlayer(2).getCountry())));
		player2Image.setBounds(6, 74, 200, 120);
		player2Panel.add(player2Image);
		
		player2ReadyLabel = new JLabel("");
		player2ReadyLabel.setBounds(80, 210, 57, 15);
		player2Panel.add(player2ReadyLabel);
		
		JLabel roomTitle = new JLabel(room.getTitle());
		roomTitle.setForeground(new Color(255, 255, 255));
		roomTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitle.setBounds(268, 39, 178, 27);
		contentPane.add(roomTitle);
		
		JButton exitBtn = new JButton("나가기");
		exitBtn.setBounds(592, 12, 97, 23);
		contentPane.add(exitBtn);
		
		String btnText;
		if(room.getMasterNum()==userNum)
			btnText = "게임시작";
		else
			btnText = "준비";
		startAndReadyBtn = new JButton(btnText);
		startAndReadyBtn.setBounds(483, 12, 97, 23);
		contentPane.add(startAndReadyBtn);
		
		startAndReadyBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(startAndReadyBtn.getText().equals("게임시작")) { //게임 시작
					if(player1ReadyLabel.getText().equals("준비완료")||player2ReadyLabel.getText().equals("준비완료")) {
						try {
							os.writeUTF("gameStart%"+room.getRoomNumber());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				else if(startAndReadyBtn.getText().equals("준비")) { //준비
					startAndReadyBtn.setText("준비완료");
					try {
						os.writeUTF("ready%"+room.getRoomNumber()+"%"+userNum);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else if(startAndReadyBtn.getText().equals("준비완료")) { //준비 취소
					startAndReadyBtn.setText("준비");
					try {
						os.writeUTF("readyCancel%"+room.getRoomNumber()+"%"+userNum);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		exitBtn.addActionListener(new ActionListener(){ //나가기 버튼
			public void actionPerformed(ActionEvent e) {
				try {
					os.writeUTF("exitFromRoom%"+room.getRoomNumber()+"%"+room.getTitle()+"%"+room.getPlayer(3-userNum).getName() + "%" + room.getPlayer(3-userNum).getCountry() + "%1/2%" + +(3-userNum)); //나간다는 메시지 전송, 방 번호, 방 제목, 새로운 방장이름, 새로운 방장 국가, 방장 번호
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				WaitingRoom wr = new WaitingRoom(room.getPlayer(userNum));//대기실 화면 생성
				wr.setLocationRelativeTo(null);//프레임 화면 중앙에 오게 하기
				wr.setVisible(true);
				dispose();
			}
		});
		
		try {
			socketCreate();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		addWindowListener(new WindowAdapter() { //윈도우 종료 x키 누를 시 행동 정의
            @Override
            public void windowClosing(WindowEvent e) { //나가기 버튼이랑 똑같은 메세지 전송, 대기실 화면으로 이동은 안함
            	try {
					os.writeUTF("exitFromRoom%"+room.getRoomNumber()+"%"+room.getTitle()+"%"+room.getPlayer(3-userNum).getName() + "%" + room.getPlayer(3-userNum).getCountry() + "%1/2%" + +(3-userNum)); //나간다는 메시지 전송, 방 번호, 방 제목, 새로운 방장이름, 새로운 방장 국가, 방장 번호
				} catch (IOException e1) {
					e1.printStackTrace();
				}finally {
					System.exit(0);
				}
            }
        });
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //윈도우 종료 x키 누르면 닫지말고 addWindowListener에 정의된대로 실행
	}
	public void socketCreate() throws IOException {
		InetAddress ip = InetAddress.getByName("localhost"); //도메인 네임 이용해서 ip주소 구하기
		Socket socket = new Socket(ip, ServerPort);//소켓 생성
		is = new DataInputStream(socket.getInputStream());   //입력 스트림 객체 생성
		os = new DataOutputStream(socket.getOutputStream()); //출력 스트림 객체 생성
		os.writeUTF(room.getPlayer(userNum).getName());
		Thread gameRoomMessageThread = new Thread(new Runnable() {//대기방 메시지를 받는 스레드 생성
			@Override                                    
			public void run() {
				while (true) {//서버로부터 메시지를 받음
					try {
						String msg = is.readUTF();      //서버에서 메시지를 받기를 기다림
						//System.out.println(msg);
				        String[] msgToken = msg.split("%");// 문자열을 '%'를 기준으로 자르기
				        //System.out.println(msgToken[0]);
				        //System.out.println(msgToken[1]);
				        //System.out.println(room.getRoomNumber());
				        int temp = 3 - userNum; //현재 유저가 1번이면 상대 플레이어는 2번, 2번이면 1번 
				        if(msgToken[0].equals("enterToRoom") && Integer.parseInt(msgToken[1])==room.getRoomNumber()) { //방에 들어왔다는 메시지가 나오면 갱신한다.
				        	//System.out.println("1번");
				        	room.roomState[temp-1]=true;
				        	Player newPlayer = new Player(msgToken[2], msgToken[3]);
				        	room.setPlayer(newPlayer, temp);
				        	if(temp==1) {
				        		 player1Name.setText(newPlayer.getName());
				        		 player1Country.setText(newPlayer.getCountry());
				        		 player1Image.setIcon(new ImageIcon(countryImgMap.get(newPlayer.getCountry())));
				        	}
				        	else if(temp == 2) {
				        		player2Name.setText(newPlayer.getName());
				        		player2Country.setText(newPlayer.getCountry());
				        		player2Image.setIcon(new ImageIcon(countryImgMap.get(newPlayer.getCountry())));
				        	}
				        }
				        else if(msgToken[0].equals("exitFromRoom") && Integer.parseInt(msgToken[1])==room.getRoomNumber()) {
				        	room.roomState[temp-1]=true;
				        	startAndReadyBtn.setText("게임시작");
				        	player1ReadyLabel.setText("");
				        	player2ReadyLabel.setText("");
				        	Player newPlayer = new Player("접속 대기 중", "-");
				        	room.setPlayer(newPlayer, temp);
				        	if(temp==1) {
				        		 player1Name.setText(newPlayer.getName());
				        		 player1Country.setText(newPlayer.getCountry());
				        		 player1Image.setIcon(new ImageIcon(countryImgMap.get(newPlayer.getCountry())));
				        	}
				        	else if(temp == 2) {
				        		player2Name.setText(newPlayer.getName());
				        		player2Country.setText(newPlayer.getCountry());
				        		player2Image.setIcon(new ImageIcon(countryImgMap.get(newPlayer.getCountry())));
				        	}
				        }
				        else if(msgToken[0].equals("ready") && Integer.parseInt(msgToken[1])==room.getRoomNumber()) {
				        	if(msgToken[2].equals("1"))
				        		player1ReadyLabel.setText("준비완료");
				        	else
				        		player2ReadyLabel.setText("준비완료");
				        }
				        else if(msgToken[0].equals("readyCancel") && Integer.parseInt(msgToken[1])==room.getRoomNumber()) {
				        	if(msgToken[2].equals("1"))
				        		player1ReadyLabel.setText("");
				        	else
				        		player2ReadyLabel.setText("");
				        }
				        else if(msgToken[0].equals("gameStart") && Integer.parseInt(msgToken[1])==room.getRoomNumber()) {
//							new GameFrame(8, 1, room.getPlayer(userNum).getName());
				        	MainThread mt;
				        	if(room.getMasterNum()==userNum)
				        		mt = new MainThread(room.getPlayer(1).getName(), room.getPlayer(1).getCountry(), room.getPlayer(2).getName(), room.getPlayer(2).getCountry());
				    		else
				    			mt = new MainThread(room.getPlayer(2).getName(), room.getPlayer(2).getCountry(), room.getPlayer(1).getName(), room.getPlayer(1).getCountry());
				        	mt.start();
							dispose();
				        }
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
		});
		gameRoomMessageThread.start();
	}
    // BackgroundPanel 클래스 정의
    class BackgroundPanel extends JPanel {
        private ImageIcon icon = new ImageIcon("images/grassBackground.png");
        private Image img = icon.getImage();

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
