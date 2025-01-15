/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author Al Badr
 */
public class GameSession {
     private String player1;
    private String player2;
    private char[] board = new char[9]; // Tic Tac Toe board
    private boolean isPlayer1Turn = true;

    public GameSession(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        for (int i = 0; i < board.length; i++) {
            board[i] = ' ';
        }
    }

    public String getCurrentPlayer() {
        return isPlayer1Turn ? player1 : player2;
    }

    public String getOpponent(String player) {
        return player.equals(player1) ? player2 : player1;
    }

    public boolean makeMove(String player, int position) {
        if (position < 0 || position >= 9 || board[position] != ' ') {
            return false;
        }
        board[position] = player.equals(player1) ? 'X' : 'O';
        isPlayer1Turn = !isPlayer1Turn;
        return true;
    }

    public boolean checkWin() {
        int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };
        for (int[] pos : winPositions) {
            if (board[pos[0]] != ' ' && board[pos[0]] == board[pos[1]] && board[pos[1]] == board[pos[2]]) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        for (char c : board) {
            if (c == ' ') return false;
        }
        return true;
    }

    public String getBoard() {
        return new String(board);
    }
}
    

