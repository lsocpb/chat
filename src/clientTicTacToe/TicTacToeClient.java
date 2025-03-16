package clientTicTacToe;

import model.GameBoard;
import rmiTicTacToe.TicTacToeService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class TicTacToeClient {
    private TicTacToeService service;
    private int playerId;
    private String playerName;
    private Scanner scanner;

    public TicTacToeClient(String serverIP, String playerName) {
        this.playerName = playerName;
        this.scanner = new Scanner(System.in);

        try {
            // Połączenie z rejestrem RMI na podanym adresie IP
            Registry registry = LocateRegistry.getRegistry(serverIP, 1099);

            // Pobranie referencji do serwisu
            service = (TicTacToeService) registry.lookup("TicTacToeService");

            // Rejestracja gracza
            playerId = service.registerPlayer(playerName);

            if (playerId == -1) {
                System.out.println("Nie można dołączyć do gry - już jest 2 graczy.");
                System.exit(0);
            }

            System.out.println("Dołączono do gry jako gracz: " + playerName);
            System.out.println("Twoje ID: " + playerId);

        } catch (Exception e) {
            System.err.println("Błąd klienta: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void play() {
        try {
            while (true) {
                // Pobierz i wyświetl aktualny stan planszy
                GameBoard board = service.getGameBoard();
                System.out.println("\nAktualny stan planszy:");
                System.out.println(board.getBoardAsString());

                // Wyświetl status gry
                String status = service.getGameStatus();
                System.out.println(status);

                // Jeśli gra się zakończyła, zapytaj o nową grę
                if (board.isGameOver()) {
                    System.out.print("Czy chcesz zagrać ponownie? (t/n): ");
                    String answer = scanner.nextLine().trim().toLowerCase();
                    if (answer.equals("t")) {
                        service.resetGame();
                        continue;
                    } else {
                        System.out.println("Dziękujemy za grę!");
                        break;
                    }
                }

                // Sprawdź, czy to kolej gracza
                if (!service.isGameReady()) {
                    System.out.println("Oczekiwanie na drugiego gracza...");
                    Thread.sleep(3000);
                    continue;
                }

                if (!service.isPlayerTurn(playerId)) {
                    System.out.println("Oczekiwanie na ruch przeciwnika...");
                    Thread.sleep(2000);
                    continue;
                }

                // Pobierz ruch od gracza
                boolean validMove = false;
                while (!validMove) {
                    try {
                        System.out.println("Twój ruch!");
                        System.out.print("Podaj wiersz (0-2): ");
                        int row = Integer.parseInt(scanner.nextLine().trim());

                        System.out.print("Podaj kolumnę (0-2): ");
                        int col = Integer.parseInt(scanner.nextLine().trim());

                        // Wykonaj ruch
                        validMove = service.makeMove(playerId, row, col);

                        if (!validMove) {
                            System.out.println("Nieprawidłowy ruch. Spróbuj ponownie.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Nieprawidłowe dane wejściowe. Podaj liczbę od 0 do 2.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas gry: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Podaj adres IP serwera (naciśnij Enter dla localhost): ");
            String serverIP = scanner.nextLine().trim();
            if (serverIP.isEmpty()) {
                serverIP = "localhost";
            }

            System.out.print("Podaj swoje imię: ");
            String playerName = scanner.nextLine().trim();

            TicTacToeClient client = new TicTacToeClient(serverIP, playerName);
            client.play();

        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
        }
    }
}