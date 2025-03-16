package serverTicTacToe;

import rmiTicTacToe.TicTacToeService;
import rmiTicTacToe.TicTacToeServiceImpl;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TicTacToeServer {
    public static void main(String[] args) {
        try {
            // Automatyczne określenie adresu IP
            String ip = InetAddress.getLocalHost().getHostAddress();

            // Ustawienie właściwości systemowych
            System.setProperty("java.rmi.server.hostname", ip);

            // Utworzenie instancji serwisu
            TicTacToeService service = new TicTacToeServiceImpl();

            // Tworzenie rejestru RMI
            Registry registry = LocateRegistry.createRegistry(1099);

            // Rejestracja serwisu w rejestrze
            registry.rebind("TicTacToeService", service);

            System.out.println("Serwer RMI gry 'Kółko i krzyżyk' został uruchomiony.");
            System.out.println("Adres IP serwera: " + ip);

        } catch (Exception e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}