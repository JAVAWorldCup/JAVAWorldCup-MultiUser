import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

	static ArrayList<ServerThread> threadList = new ArrayList<>();
	static ArrayList<String> roomList = new ArrayList<>();
	//static String clientName = "unknown";
	static int clientCount = 0;
	static int roomNumber = 100;

	public static void main(String[] args) throws IOException {
		try {
		ServerSocket ssocket = new ServerSocket(5008);
		System.out.println("server start");
		System.out.println("서버 연결");
		Socket s;
		while (!ssocket.isClosed()) {
			s = ssocket.accept();
			DataInputStream is = new DataInputStream(s.getInputStream());    //입력 스트림 객체 생성
			DataOutputStream os = new DataOutputStream(s.getOutputStream()); //출력 스트림 객체 생성
			
			ServerThread thread = new ServerThread(s, is, os); //소켓, 이름, 스트림 객체 보냄
			threadList.add(thread); //리스트 추가
			
			thread.start();
			clientCount++;
		}
		}catch(IOException e) {
            System.out.println("서버 에러 "  + e.toString()); 
        }
	}
}

class ServerThread extends Thread {
	Scanner scn = new Scanner(System.in);
	private String name;
	final DataInputStream is;
	final DataOutputStream os;
	Socket s;
	boolean active;

	public ServerThread(Socket s, DataInputStream is, DataOutputStream os) {
		this.is = is;
		this.os = os;
		try {
			this.name = is.readUTF(); //클라이언트로부터 이름을 받음
			System.out.println("클라이언트 접속: " + name);
			this.os.writeUTF("firstRoomNumber%"+Server.roomNumber);
			for(String msg : Server.roomList) {
				System.out.println("보냄:"+msg);
				this.os.writeUTF(msg);
			}
		}catch (IOException e) {
            e.printStackTrace();
        }
		this.s = s;
		this.active = true;
	}

	@Override
	public void run() {
		String message;
		while (s.isConnected()) {
			try {
				message = is.readUTF();
				System.out.println(message);
				
				String msgTokens[] = message.split("%",2);
				if(msgTokens[0].equals("createRoom")) {
					Server.roomNumber++;
					Server.roomList.add("roomListDownload%"+msgTokens[1]);
				}
				
				for (ServerThread t : Server.threadList) {
					t.os.writeUTF(message);
				}
			} 
			catch (IOException e) {
				Server.threadList.remove(this); // 클라이언트가 종료되면 EOFException이 발생하므로, 해당 클라이언트를 리스트에서 제거
                this.active = false;
				System.out.println("클라이언트" + name + "종료됨");
				e.printStackTrace();
				break;
			}
		}
		try {
			this.is.close();
			this.os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}