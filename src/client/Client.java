package client;

import model.Product;
import rmi.ProductService;
import rmi.ProductService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            System.setProperty("java.security.policy", "client.policy");

//            if (System.getSecurityManager() == null) {
//                System.setSecurityManager(new SecurityManager());
//            }

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            ProductService service = (ProductService) registry.lookup("ProductService");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n===== MENU =====");
                System.out.println("1. Pobierz listę produktów");
                System.out.println("2. Wyszukaj produkt po nazwie");
                System.out.println("0. Wyjście");
                System.out.print("Wybierz opcję: ");

                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        List<Product> produkty = service.getProducts();
                        System.out.println("\nLista produktów:");
                        for (Product p : produkty) {
                            System.out.println(p);
                        }
                        break;
                    case 2:
                        System.out.print("Podaj nazwę produktu: ");
                        String nazwa = scanner.nextLine();
                        Product produkt = service.findProductByName(nazwa);
                        if (produkt != null) {
                            System.out.println("\nZnaleziony produkt: " + produkt);
                        } else {
                            System.out.println("\nNie znaleziono produktu o nazwie: " + nazwa);
                        }
                        break;
                    case 0:
                        System.out.println("Kończenie pracy...");
                        return;
                    default:
                        System.out.println("Nieprawidłowa opcja!");
                }
            }

        } catch (Exception e) {
            System.err.println("Błąd klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}