
public class Room {
	private int roomNumber;
	private Player[] players = {new Player("접속 대기 중", "-"), new Player("접속 대기 중", "-")};
	private String title;
	public boolean[] roomState = {false, false};
	private int masterNum = 1;
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public Player getPlayer(int num) {
		if(num==1)
			return players[0];
		else if(num==2)
			return players[1];
		return null;
	}
	public void setPlayer(Player player, int num) {
		if(num==1)
			this.players[0] = player;
		else if(num==2)
			this.players[1] = player;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getMasterNum() {
		return masterNum;
	}
	public void setMasterNum(int n) {
		this.masterNum = n;
	}
	public Room(int roomNumber, String title) {
		this.roomNumber = roomNumber;
		this.title = title;
	}
	
}
