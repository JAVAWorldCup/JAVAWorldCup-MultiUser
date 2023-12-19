import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

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
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class WaitingRoomFrame extends JFrame {
	final static int ServerPort = 5019;   // 포트 번호
	private MyTableModel model;
	private int roomNumber;
	private String header[] = {"방 번호", "제목", "방장", "인원"};
	HashMap<Integer, Room> roomMap = new HashMap<Integer, Room>();
	
	DataInputStream is;
	DataOutputStream os;
	Socket socket;
	private Player user;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField inputChatting;
	private JTextArea chattingTextArea;
	private CreateRoomDialog createRoomDialog;
	private JTable roomTable;
	public WaitingRoomFrame(Player user) {
		this.user = user;
		setTitle("방");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 836, 490);
		contentPane = new BackgroundPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		inputChatting = new JTextField();
		inputChatting.setBounds(605, 390, 203, 21);
		contentPane.add(inputChatting);
		inputChatting.setColumns(10);
		
		inputChatting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = inputChatting.getText(); //메시지 서버에 보내기
				if(!s.contains("%")) {
					try {
						os.writeUTF("chatting%"+user.getName()+"%"+s);
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
		
		//사용자 이름
		JLabel userNameLabel = new JLabel(user.getName());
		userNameLabel.setBounds(605, 10, 185, 27);
		contentPane.add(userNameLabel);
		
		//방만들기 
		JButton createRoomBtn = new JButton("방만들기");
		createRoomBtn.setBounds(29, 12, 97, 23);
		contentPane.add(createRoomBtn);
		
		if(model==null) model=new MyTableModel(header,0); // 쉘 추가 및 삭제를 할 수 있도록 테이블의 모델을 만듦
		
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
	                    System.out.println("방 정보:" + roomTable.getValueAt(row, 0) + ", " + roomTable.getValueAt(row, 1) + ", "+ roomTable.getValueAt(row, 2) + ", " + roomTable.getValueAt(row, 3));

	                    if(roomTable.getValueAt(row, 3).equals("1/2")) { //현재 방에 자리가 있으면 방에 들어간다.
							String s = roomTable.getValueAt(row, 0) + "%" + user.getName() +"%"+ user.getCountry() + "%" + row;   //방에 들어간다고 메시지 서버에 보내기
							try {
								os.writeUTF("enterToRoom%"+s);
								 Room r = roomMap.get(Integer.parseInt((String) roomTable.getValueAt(row, 0)));
				                    int userNum = 1;
				                    if(r.roomState[0]) {
				                    	r.roomState[1] = true;
				                    	userNum=2;
				                    }
				                    else {
				                    	r.roomState[0] = true;
				                    	userNum = 1;
				                    }
									RoomFrame gf = new RoomFrame(r, user, userNum); //게임 입장
									gf.setLocationRelativeTo(null);//프레임 화면 중앙에 오게 하기
									gf.setVisible(true);
									dispose();//창을 닫음
				                    // 소켓을 닫는 코드 추가
				                    try {
				                        if (is != null) is.close();
				                        if (os != null) os.close();
				                        if (s!=null) socket.close();
				                    } catch (IOException ex) {
				                        ex.printStackTrace();
				                    }
							} catch (IOException e1) {
								e1.printStackTrace();
							}
	                    }
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
		
		JScrollPane chattingScrollPane = new JScrollPane();
		chattingScrollPane.setBounds(605, 47, 203, 333);
		contentPane.add(chattingScrollPane);
		//대기실 채팅 목록
		chattingTextArea = new JTextArea();
		chattingScrollPane.setViewportView(chattingTextArea);
		chattingTextArea.setEditable(false);
	}
	public void socketCreate() throws IOException {
		InetAddress ip = InetAddress.getByName("localhost"); //도메인 네임 이용해서 ip주소 구하기
		socket = new Socket(ip, ServerPort);//소켓 생성
		is = new DataInputStream(socket.getInputStream());   //입력 스트림 객체 생성
		os = new DataOutputStream(socket.getOutputStream()); //출력 스트림 객체 생성
		os.writeUTF(user.getName());
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
				    		String temp[] = {msgToken[1], msgToken[2], msgToken[3], msgToken[5]};
				    		model.addRow(temp);
				    		Room r =new Room(Integer.parseInt(msgToken[1]), msgToken[2]);
				    		r.setPlayer(new Player(msgToken[3], msgToken[4]), 1);
				    		r.roomState[0] = true;
				    		roomMap.put(Integer.parseInt(msgToken[1]), r);
				        }
				        else if(msgToken[0].equals("roomListDownload")) { //메시지가 roomListDownload면 로그인 전 생성된 방 목록을 가져옴
				        	if(model==null) { 
				        		model=new MyTableModel(header,0);
				        	}
				        	String temp[] = {msgToken[1], msgToken[2], msgToken[3], msgToken[5]};//방 번호, 제목, 방장 이름, 인원
				    		model.addRow(temp);
				    		Room r =new Room(Integer.parseInt(msgToken[1]), msgToken[2]);
				    		r.setPlayer(new Player(msgToken[3], msgToken[4]), Integer.parseInt(msgToken[6])); //방장 이름, 방장 국가, 방장 위치로 룸 정보 설정
				    		r.roomState[Integer.parseInt(msgToken[6])-1] = true;
				    		r.setMasterNum(Integer.parseInt(msgToken[6]));
				    		roomMap.put(Integer.parseInt(msgToken[1]), r);
				        }
				        else if(msgToken[0].equals("enterToRoom")) {
				        	roomTable.setValueAt( "2/2", Integer.parseInt(msgToken[4]), 3); //토큰에서 행정보를 가져와 테이블의 인원 정보를 바꿈
				        }
				        else if(msgToken[0].equals("exitFromRoom")) { //메시지가 exitFromRoom이면 방에서 나온 유저가 있음
				            String exitRoomNumber = msgToken[1];
				            
				            int rowIndex = -1;
				            for (int i = 0; i < roomTable.getRowCount(); i++) {// 해당 방 번호를 가진 쉘의 위치 찾기
				                if (roomTable.getValueAt(i, 0).equals(exitRoomNumber)) {
				                    rowIndex = i;
				                    break;
				                }
				            }
				            if (rowIndex != -1) {// 방 번호를 가진 쉘을 찾았을 때의 처리
				            	System.out.println("방 번호 " + exitRoomNumber + "을 가진 행을 찾았습니다. 행 인덱스: " + rowIndex + "\n");
				                
				            	if(roomTable.getValueAt(rowIndex, 3).equals("1/2")){
				            		model.removeRow(rowIndex); //모든 인원이 나가면 방을 삭제한다.
				            		roomMap.remove(Integer.parseInt(exitRoomNumber));
				            	}
				            	else {
					            	roomTable.setValueAt(msgToken[3], rowIndex, 2); //새 방장 이름
					            	roomTable.setValueAt( "1/2", rowIndex, 3);
					            	Room newRoom = new Room(Integer.parseInt(exitRoomNumber), msgToken[2]);
					            	newRoom.setPlayer(new Player(msgToken[3], msgToken[4]), Integer.parseInt(msgToken[6]));
					            	newRoom.roomState[Integer.parseInt(msgToken[6])-1] = true;
					            	newRoom.setMasterNum(Integer.parseInt(msgToken[6]));
					            	roomMap.put(Integer.parseInt(exitRoomNumber),newRoom);
					            	
				            	}
				            } else { // 방 번호를 가진 쉘을 찾지 못했을 때 오류 처리
				                System.out.println("방 번호 " + exitRoomNumber + "을 가진 행을 찾지 못했습니다." + "\n");
				            }
				        }
				        else {
				        	System.out.println("잘못된 전송");
				        }
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
    public static class MyTableModel extends DefaultTableModel {
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
						
						int rm = roomNumber;
						String s =rm + "%" + title + "%" + user.getName() +"%"+ user.getCountry() +"%1/2%1";   //메시지 서버에 보내기, 방번호, 방제목, 방장이름, 방장국가, 방장위치
						try {
							os.writeUTF("createRoom%"+s);
							Room r = new Room(rm, title);
							r.roomState[0] = true;
							RoomFrame gf = new RoomFrame(r, user, 1); //게임 입장
							gf.setLocationRelativeTo(null);//프레임 화면 중앙에 오게 하기
							gf.setVisible(true);
							dispose();//dialog창을 닫음
							frame.dispose();
		                    // 소켓을 닫는 코드 추가
		                    try {
		                        if (is != null) is.close();
		                        if (os != null) os.close();
		                        if (s!=null) socket.close();
		                    } catch (IOException ex) {
		                        ex.printStackTrace();
		                    }
						} catch (IOException e1) {
							e1.printStackTrace();
						}
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
