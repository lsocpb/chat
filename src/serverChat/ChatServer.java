package serverChat;

import modelChat.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 12345;
    private List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony na porcie " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void broadcast(Message message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.err.println("Błąd przy tworzeniu strumieni: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Message firstMessage = (Message) inputStream.readObject();
                username = firstMessage.getSender();

                Message serverMessage = new Message("Server", "Użytkownik " + username + " dołączył do czatu");
                broadcast(serverMessage, this);

                while (true) {
                    Message message = (Message) inputStream.readObject();
                    System.out.println("Otrzymano: " + message);
                    broadcast(message, this);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Użytkownik rozłączony: " + username);
            } finally {
                clients.remove(this);

                Message serverMessage = new Message("Server", "Użytkownik " + username + " opuścił czat");
                broadcast(serverMessage, this);

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(Message message) {
            try {
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                System.err.println("Błąd przy wysyłaniu wiadomości: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}