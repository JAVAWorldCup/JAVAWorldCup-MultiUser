import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreServer {
	private final int port;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ScoreServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("TimerServer started on port " + port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private DataOutputStream output;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        public void run() {
            try (DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {
                String inputLine;
                while ((inputLine = input.readUTF()) != null) {
                    for (ClientHandler client : clients) {
                        if (client != this) {
                            client.send(inputLine);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                try {
                    clients.remove(this);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send(String message) {
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                System.out.println("Error sending message to client: " + e.getMessage());
                clients.remove(this);
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 5001; // Use your desired port
        try {
            new ScoreServer(port).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
