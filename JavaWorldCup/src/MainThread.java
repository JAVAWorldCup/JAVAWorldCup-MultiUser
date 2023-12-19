import javax.swing.JOptionPane;

class MainThread extends Thread {
	private GameFrame gf;
	private VersusFrame vf;
	private ResultFrame rf;
	
	private Room room;
	private int userNum;
	private String name;
	private String country;
	private String opponentName;
	private String opponentCountry;
	
	private int myScore=0;
	private int opponentScore=0;
	
	private int round = 8;
	private int level = 3;
	private boolean start = false; // 처음 게임을 시작하는지 아닌지 표기(true면 처음이 아님)
	
	public MainThread(Room room, int userNum, String name, String country, String opponentName, String opponentCountry) {
		this.room = room;
		this.userNum = userNum;
		this.name = name;
		this.country = country;
		this.opponentName = opponentName;
		this.opponentCountry = opponentCountry;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(100);
				if(start == false) { // 처음 게임을 시작했을 때
					vf = new VersusFrame(round, name, country, opponentName, opponentCountry); // 8강전, 4강전, 결승 대진표 표기
//					setVisible(false);
					sleep(7000);
					gf = new GameFrame(round, level, name);
					vf.setVisible(false);
					start = true; // 게임을 시작했으니 true로 변경
				}
				if(gf.getGameState() == true) { // 경기 끝남
					if(gf.getGoalState() == 0) { // 승
						myScore++;
						System.out.println("Score me: " + myScore + " opponent: "+ opponentScore);
						round /= 2; // 다음 라운드
						rf = new ResultFrame(0, name); // 승
						gf.setVisible(false); // gameFrame 숨기기
						gf.stopAudio(); // gameFrame 배경음악 끄기
						sleep(3000); 
					}
					else if(gf.getGoalState() == 1){ // 패배
						opponentScore++;
						System.out.println("Score me: " + myScore + " opponent: "+ opponentScore);
						round /= 2; // 다음 라운드
						rf = new ResultFrame(1, name);
						gf.setVisible(false); // gameFrame 숨기기
						gf.stopAudio(); // gameFrame 배경음악 끄기
						sleep(3000); 
					}
					else {
						rf = new ResultFrame(2, name);
						gf.setVisible(false); // gameFrame 숨기기
						gf.stopAudio(); // gameFrame 배경음악 끄기
						sleep(3000);
					}
					if(myScore == 2) {
						rf.setVisible(false); // 결과 화면 숨기기
						rf.stopAudio(); // resultFrame 배경음악 끄기
						RoomFrame roomFrame = new RoomFrame(room, room.getPlayer(userNum), userNum);
						roomFrame.setVisible(true);
						JOptionPane.showMessageDialog(roomFrame, "You Win!!", name + ": Result", 1); // 이름 입력
						interrupt(); // 스레드 종료
					}
					else if(opponentScore == 2) {
						rf.setVisible(false); // 결과 화면 숨기기
						rf.stopAudio(); // resultFrame 배경음악 끄기
						RoomFrame roomFrame = new RoomFrame(room, room.getPlayer(userNum), userNum);
						roomFrame.setVisible(true);
						JOptionPane.showMessageDialog(roomFrame, "You Lose..", name + ": Result", 1); // 이름 입력
						interrupt(); // 스레드 종료
					}
					else {
						rf.setVisible(false); // 결과 화면 숨기기
						rf.stopAudio(); // resultFrame 배경음악 끄기
						vf = new VersusFrame(round, name, country, opponentName, opponentCountry);
						sleep(3000);
						gf = new GameFrame(round, level, name);
						gf.setVisible(true);
						vf.setVisible(false);
					}
				}
					
			}catch(InterruptedException e) {return;}
		}
	}
}