package rmiTicTacToe;

import model.GameBoard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class TicTacToeServiceImpl extends UnicastRemoteObject implements TicTacToeService {
    private static final long serialVersionUID = 1L;

    private final GameBoard gameBoard;
    private final Map<Integer, String> players;
    private int playerXId;
    private int playerOId;
    private int nextPlayerId;

    public TicTacToeServiceImpl() throws RemoteException {
        super();
        gameBoard = new GameBoard();
        players = new HashMap<>();
        playerXId = -1;
        playerOId = -1;
        nextPlayerId = 1;
    }

    @Override
    public synchronized int registerPlayer(String name) throws RemoteException {
        // Maksymalnie dwóch graczy
        if (players.size() >= 2) {
            return -1;
        }

        int playerId = nextPlayerId++;
        players.put(playerId, name);

        // Przydziel X lub O
        if (playerXId == -1) {
            playerXId = playerId;
            System.out.println("Gracz " + name + " dołączył jako X (ID: " + playerId + ")");
        } else if (playerOId == -1) {
            playerOId = playerId;
            System.out.println("Gracz " + name + " dołączył jako O (ID: " + playerId + ")");
        }

        return playerId;
    }

    @Override
    public synchronized boolean makeMove(int playerId, int row, int col) throws RemoteException {
        // Sprawdź, czy gra jest gotowa
        if (!isGameReady()) {
            return false;
        }

        // Sprawdź, czy to ruch właściwego gracza
        if (!isPlayerTurn(playerId)) {
            return false;
        }

        // Wykonanie ruchu
        boolean moveMade = gameBoard.makeMove(row, col);

        if (moveMade) {
            System.out.println("Gracz " + players.get(playerId) + " wykonał ruch: [" + row + ", " + col + "]");
            System.out.println(gameBoard.getBoardAsString());

            if (gameBoard.isGameOver()) {
                int winner = gameBoard.getWinner();
                if (winner == GameBoard.PLAYER_X) {
                    System.out.println("Gracz X (" + players.get(playerXId) + ") wygrał!");
                } else if (winner == GameBoard.PLAYER_O) {
                    System.out.println("Gracz O (" + players.get(playerOId) + ") wygrał!");
                } else {
                    System.out.println("Remis!");
                }
            }
        }

        return moveMade;
    }

    @Override
    public synchronized GameBoard getGameBoard() throws RemoteException {
        return gameBoard;
    }

    @Override
    public synchronized boolean isPlayerTurn(int playerId) throws RemoteException {
        if (gameBoard.getCurrentPlayer() == GameBoard.PLAYER_X) {
            return playerId == playerXId;
        } else {
            return playerId == playerOId;
        }
    }

    @Override
    public synchronized void resetGame() throws RemoteException {
        gameBoard.resetBoard();
        System.out.println("Gra została zresetowana");
    }

    @Override
    public synchronized boolean isGameReady() throws RemoteException {
        return playerXId != -1 && playerOId != -1;
    }

    @Override
    public synchronized String getGameStatus() throws RemoteException {
        if (!isGameReady()) {
            return "Oczekiwanie na drugiego gracza...";
        }

        if (gameBoard.isGameOver()) {
            int winner = gameBoard.getWinner();
            if (winner == GameBoard.PLAYER_X) {
                return "Gracz X (" + players.get(playerXId) + ") wygrał!";
            } else if (winner == GameBoard.PLAYER_O) {
                return "Gracz O (" + players.get(playerOId) + ") wygrał!";
            } else {
                return "Remis!";
            }
        } else {
            String currentPlayerName = gameBoard.getCurrentPlayer() == GameBoard.PLAYER_X
                ? players.get(playerXId)
                : players.get(playerOId);
            return "Ruch gracza " + (gameBoard.getCurrentPlayer() == GameBoard.PLAYER_X ? "X" : "O")
                + " (" + currentPlayerName + ")";
        }
    }
}