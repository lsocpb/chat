package serverChat;

import rmiChat.ChatService;
import rmiChat.ChatServiceImpl;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
    public static void main(String[] args) {
        try {
            // Automatyczne określenie adresu IP
            String ip = InetAddress.getLocalHost().getHostAddress();

            // Ustawienie właściwości systemowych
            System.setProperty("java.rmi.server.hostname", ip);

            // Utworzenie instancji serwisu
            ChatService service = new ChatServiceImpl();

            // Tworzenie rejestru RMI
            Registry registry = LocateRegistry.createRegistry(1099);

            // Rejestracja serwisu w rejestrze
            registry.rebind("ChatService", service);

            System.out.println("Serwer RMI został uruchomiony.");
            System.out.println("Adres IP serwera: " + ip);

        } catch (Exception e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}