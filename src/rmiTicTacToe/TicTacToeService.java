package rmiTicTacToe;

import model.GameBoard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToeService extends Remote {
    // Rejestracja gracza
    int registerPlayer(String name) throws RemoteException;

    // Wykonanie ruchu
    boolean makeMove(int playerId, int row, int col) throws RemoteException;

    // Pobranie aktualnego stanu planszy
    GameBoard getGameBoard() throws RemoteException;

    // Sprawdzenie, czy to kolej gracza
    boolean isPlayerTurn(int playerId) throws RemoteException;

    // Resetowanie gry
    void resetGame() throws RemoteException;

    // Sprawdzenie czy gra się rozpoczęła (czy są dwaj gracze)
    boolean isGameReady() throws RemoteException;

    // Sprawdzenie statusu gry
    String getGameStatus() throws RemoteException;
}