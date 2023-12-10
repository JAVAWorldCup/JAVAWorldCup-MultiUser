import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class WaitingRoom extends JFrame {
	final static int ServerPort = 5008;   // 포트 번호
	private MyTableModel model;
	private int roomNumber;
	private String header[] = {"방 번호", "제목", "방장", "인원"};
	DataInputStream is;
	DataOutputStream os;
	private String userName;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField inputChatting;
	private JTextArea chattingTextArea;
	private CreateRoomDialog createRoomDialog;
	private JTable roomTable;
	public WaitingRoom(String userName) {
		this.userName = userName;
		setTitle("방");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 836, 490);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//대기실 채팅 입력
		JButton inputChattingBtn = new JButton("입력");
		inputChattingBtn.setBounds(751, 389, 57, 23);
		contentPane.add(inputChattingBtn);
		
		inputChatting = new JTextField();
		inputChatting.setBounds(605, 390, 147, 21);
		contentPane.add(inputChatting);
		inputChatting.setColumns(10);
		
		inputChatting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = inputChatting.getText(); //메시지 서버에 보내기
				if(!s.contains("%")) {
					try {
						os.writeUTF("chatting%"+userName+"%"+s);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				else System.out.println("%를 넣을 수 없습니다.");
				inputChatting.setText("");
			}
		});
		try {
			socketCreate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//대기실 채팅 목록
		chattingTextArea = new JTextArea();
		chattingTextArea.setBounds(605, 47, 203, 332);
		chattingTextArea.setEditable(false); //수정 불가
		contentPane.add(chattingTextArea);
		
		//사용자 이름
		JLabel userNameLabel = new JLabel(userName);
		userNameLabel.setBounds(605, 10, 185, 27);
		contentPane.add(userNameLabel);
		
		//방만들기 
		JButton createRoomBtn = new JButton("방만들기");
		createRoomBtn.setBounds(29, 12, 97, 23);
		contentPane.add(createRoomBtn);
		
		if(model==null) model=new MyTableModel(header,0); // 쉘 추가 및 삭제를 할 수 있도록 테이블의 모델을 만듦
		//String temp1[] = {"123", "제목1aaaa", "aaa", "1/2"};
		//model.addRow(temp1);
		
		JScrollPane roomScrollPane = new JScrollPane();
		roomScrollPane.setBounds(29, 47, 550, 365);
		contentPane.add(roomScrollPane);
		
		roomTable = new JTable(model);
		roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //한 셀만 선택가능
		roomTable.getColumnModel().getColumn(0).setPreferredWidth(15);
		roomTable.getColumnModel().getColumn(1).setPreferredWidth(300);
		roomTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		roomTable.getColumnModel().getColumn(3).setPreferredWidth(15);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		roomTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); //열의 내용을 가운데 정렬시킴
		roomTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		roomTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		roomTable.setRowHeight(30);
		
		roomTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블클릭 감지
                    int row = roomTable.rowAtPoint(e.getPoint());
                    int col = roomTable.columnAtPoint(e.getPoint());
                    if(row >= 0 && col >=0) {
	                    System.out.println("방 정보:" + row + ", " + col + " "+ roomTable.getValueAt(row, col));
                    }
                }
            }
        });
		
		roomScrollPane.setViewportView(roomTable);
		
		createRoomDialog = new CreateRoomDialog(this);//방을 만드는 dialog
		createRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createRoomDialog.setLocationRelativeTo(null); //dialog 화면 중앙에 위치
				createRoomDialog.setVisible(true); //dialog화면에 보이게하기
			}
		});
		
		JButton showRankingBtn = new JButton("랭킹");
		showRankingBtn.setBounds(138, 12, 97, 23);
		contentPane.add(showRankingBtn);
	}
	public void socketCreate() throws IOException {
		InetAddress ip = InetAddress.getByName("localhost"); //도메인 네임 이용해서 ip주소 구하기
		Socket s = new Socket(ip, ServerPort);//소켓 생성
		is = new DataInputStream(s.getInputStream());   //입력 스트림 객체 생성
		os = new DataOutputStream(s.getOutputStream()); //출력 스트림 객체 생성
		os.writeUTF(userName);
		Thread waitingRoomMessageThread = new Thread(new Runnable() {//대기방 메시지를 받는 스레드 생성
			@Override                                    
			public void run() {
				while (true) {//서버로부터 메시지를 받음
					try {
						String msg = is.readUTF();      //서버에서 메시지를 받기를 기다림
				        String[] msgToken = msg.split("%");// 문자열을 '%'를 기준으로 자르기
				        if(msgToken[0].equals("firstRoomNumber"))
				        	roomNumber = Integer.parseInt(msgToken[1]);
				        else if(msgToken[0].equals("chatting")) //메시지가 chatting이면 TextArea에 출력
				        	chattingTextArea.append(msgToken[1]+" : " +msgToken[2] + "\n");
				        else if(msgToken[0].equals("createRoom")) {//메시지가 createRoom면 방을 만듬
				        	roomNumber++;
				    		String temp[] = {msgToken[1], msgToken[2], msgToken[3], msgToken[4]};
				    		model.addRow(temp);
				        }
				        else if(msgToken[0].equals("roomListDownload")) { //메시지가 roomListDownload면 로그인 전 생성된 방 목록을 가져옴
				        	if(model==null) { 
				        		model=new MyTableModel(header,0);
				        	}
				        	String temp[] = {msgToken[1], msgToken[2], msgToken[3], msgToken[4]};
				    		model.addRow(temp);
				        }
				        else System.out.println("잘못받음");
				        	
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
		});
		waitingRoomMessageThread.start();
	}
    // DefaultTableModel을 상속한 사용자 정의 모델 클래스
    private static class MyTableModel extends DefaultTableModel {
        public MyTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }
        // isCellEditable 메서드를 오버라이드해 셀을 편집 불가능으로 만듦
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
	
	class CreateRoomDialog extends JDialog{//방을 만들기 위한 dialog
		private JTextField inputTitle = new JTextField(10);
		private JButton okBtn = new JButton("Ok");
		private JButton cancelBtn = new JButton("Cancel");
		public CreateRoomDialog(JFrame frame) {
			super(frame, "방 만들기");
			setLayout(new FlowLayout());
			add(inputTitle);
			add(okBtn);
			add(cancelBtn);
			setSize(300,100);
			
			okBtn.addActionListener(new ActionListener() {//확인 버튼이 눌리면 실행
				public void actionPerformed(ActionEvent e) {
					String title = inputTitle.getText();//텍스트필드의 내용을 가져온다.
					if(title.equals("")==false && !title.contains("%")) {//공백이 아니고 %가 안들어있으면 시작, %로 토큰을 나누기 때문에 들어가면 안됨
						inputTitle.setText("");
						
						String s =roomNumber + "%" + title + "%" + userName + "%1/2";   //메시지 서버에 보내기
						try {
							os.writeUTF("createRoom%"+s);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						dispose();//dialog창을 닫음
					}
				}
			});
			cancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();//dialog창을 닫음
				}
			});
		}
	}
}
