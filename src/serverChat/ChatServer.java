package serverChat;

import rmiChat.ChatService;
import rmiChat.ChatServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "172.20.10.3");

            ChatService service = new ChatServiceImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("ChatService", service);

            System.out.println("Serwer RMI został uruchomiony.");
            System.out.println("Adres IP serwera: " + System.getProperty("java.rmi.server.hostname"));

        } catch (Exception e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}