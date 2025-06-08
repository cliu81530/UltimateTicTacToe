package TicTacToe;

public class GameLogic {
    private static final int boardSize = 3;

    public static char checkSmallBoardWinner(char[] Board) {
        // Check rows, columns, and diagonals for a winner
        for(int i = 0; i < boardSize; i++){
            if(Board[i * boardSize] != ' '&&
            Board[i*boardSize] == Board[i*boardSize +1]&&
            Board[i*boardSize+1] ==Board[i*boardSize+2]){
                return Board[i*boardSize];
            }
        }
        // Check columns
       
        for(int i = 0; i < boardSize; i++){
            System.out.println("check column: " + i + " with value: " + Board[i]); 
            if(Board[i] != ' ' &&
               Board[i] == Board[i + boardSize] &&
               Board[i] == Board[i + 2 * boardSize]) {
                return Board[i];
            }
        }

        // Check diagonals
        if(Board[0] != ' ' &&
           Board[0] == Board[4] &&
           Board[4] == Board[8]) {
            return Board[0];
        }
        if(Board[2] != ' ' &&
           Board[2] == Board[4] &&
           Board[4] == Board[6]) {
            return Board[2];
        }
        // If no winner found, return a space character
        return ' ';
    }

    public static char checkBigBoardWinner(char[][] bigBoard){
        // Check rows, columns, and diagonals for a winner
        for(int i = 0; i < boardSize; i++){
            //check rows
            if(bigBoard[i][0] != ' ' &&
               bigBoard[i][0] == bigBoard[i][1] &&
               bigBoard[i][1] == bigBoard[i][2]) {
                return bigBoard[i][0];
            }

        }
        // Check columns
        for(int i = 0; i < boardSize; i++){
            if(bigBoard[0][i] != ' ' &&
               bigBoard[0][i] == bigBoard[1][i] &&
               bigBoard[1][i] == bigBoard[2][i]) {
                return bigBoard[0][i];
            }
        }
        
        
        // Check diagonals
        if(bigBoard[0][0] != ' '&&
        bigBoard[0][0] == bigBoard[1][1] &&
        bigBoard[1][1] == bigBoard[2][2]){
            return bigBoard[0][0];
        }
        if(bigBoard[0][2] != ' '&&
        bigBoard[0][2] == bigBoard[1][1] &&
        bigBoard[1][1] == bigBoard[2][0]){
            return bigBoard[0][2];
        }
        return ' ';
    }

    public static boolean isBigBoardFull(char[][] bigBoard){
        for(int i = 0; i <boardSize; i++){
            for( int j = 0; j < boardSize;j++){
                if(bigBoard[i][j] == ' '){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidMove(char[][][] board, char[][] bigBoard,int bigRow, int bigCol, int cellIndex, int activeBoard, boolean gameOver) 
    {
        // Check if the game is over
        if(gameOver){
            return false; 
        }
        // Check if the cell is already occupied
        if(board[bigRow][bigCol][cellIndex] != ' '){
            return false; 
        }
        // Check if the big board cell is already over
        if(bigBoard[bigRow][bigCol] != ' '){return false;}
        // Check if the move is valid for the active board
        if (activeBoard != -1
            && (bigRow != activeBoard / 3 || bigCol != activeBoard % 3)) {
            return false;
        }
        return true;
    }

    public static int getNextActiveBoard(int cellIndex, char[][] bigBoard){
        int nextRow = cellIndex / boardSize;
        int nextCol = cellIndex % boardSize;
        // If the next active board cell is already occupied, return -1
        if(bigBoard[nextRow][nextCol] == ' '){
            return cellIndex; // Convert char to int
        }
        return -1; // No valid next active board
    }

    public static String formatMove(char player, int bigRow, int bigCol, int cellIndex){
        return String.format("Player %c placed in big board cell (%d, %d) at small board cell %d", player, bigRow +1, bigCol +1, cellIndex +1);
    }
}
