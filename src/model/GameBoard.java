package model;

import java.io.Serializable;

public class GameBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int EMPTY = 0;
    public static final int PLAYER_X = 1;
    public static final int PLAYER_O = 2;

    private final int[][] board;
    private int currentPlayer;
    private boolean gameOver;
    private int winner;

    public GameBoard() {
        board = new int[3][3];
        currentPlayer = PLAYER_X; // X zawsze zaczyna
        gameOver = false;
        winner = EMPTY;
    }

    public boolean makeMove(int row, int col) {
        // Sprawdź czy ruch jest możliwy
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != EMPTY || gameOver) {
            return false;
        }

        // Wykonaj ruch
        board[row][col] = currentPlayer;

        // Sprawdź czy gra się zakończyła
        checkGameStatus();

        // Zmień gracza jeśli gra nie jest skończona
        if (!gameOver) {
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }

        return true;
    }

    private void checkGameStatus() {
        // Sprawdź wiersze
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                gameOver = true;
                winner = board[i][0];
                return;
            }
        }

        // Sprawdź kolumny
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                gameOver = true;
                winner = board[0][i];
                return;
            }
        }

        // Sprawdź przekątne
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            gameOver = true;
            winner = board[0][0];
            return;
        }

        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            gameOver = true;
            winner = board[0][2];
            return;
        }

        // Sprawdź remis (plansza pełna)
        boolean boardFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    boardFull = false;
                    break;
                }
            }
            if (!boardFull) break;
        }

        if (boardFull) {
            gameOver = true;
            winner = EMPTY; // Remis
        }
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
        currentPlayer = PLAYER_X;
        gameOver = false;
        winner = EMPTY;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public String getBoardAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  0 1 2\n");
        for (int i = 0; i < 3; i++) {
            sb.append(i).append(" ");
            for (int j = 0; j < 3; j++) {
                switch (board[i][j]) {
                    case EMPTY:
                        sb.append("- ");
                        break;
                    case PLAYER_X:
                        sb.append("X ");
                        break;
                    case PLAYER_O:
                        sb.append("O ");
                        break;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}