package TicTacToe;
import java.util.List;
import java.io.Serializable;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    public char[][][] gameBoard;
    public char[][] bigBoard;
    public int activeBoard;
    public char currentPlayer;
    public boolean gameOver;
    public char winner;
    public List<String> movesHistory;
    
    
    public GameState() {}

    public GameState(char[][][] board, char[][] bigBoard, int activeBoard, char currentPlayer, boolean gameOver, char winner, List<String> movesHistory) {
        this.gameBoard = board;
        this.bigBoard = bigBoard;
        this.currentPlayer = currentPlayer;
        this.gameOver = gameOver;
        this.winner = winner;
        this.movesHistory = movesHistory;
    }



}
