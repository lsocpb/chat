package server;

import rmi.ProductService;
import rmi.ProductServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.codebase", "file://" + System.getProperty("user.dir") + "/");
            System.setProperty("java.rmi.server.hostname", "localhost");
            System.setProperty("java.security.policy", "server.policy");

//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager());
//            }

            ProductService service = new ProductServiceImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("ProductService", service);

            System.out.println("Serwer RMI został uruchomiony.");

        } catch (Exception e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
