package TicTacToe;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;


public class GameIO {

    public static boolean saveGame(JFrame parent, char[][][] gameBoard, char[][] bigBoard,
                                   char currentPlayer, int activeBoard, boolean gameEnded,
                                   char winner, java.util.List<String> moveHistory) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(fileChooser.getSelectedFile()))) {

                GameState state = new GameState();
                state.gameBoard = gameBoard;
                state.bigBoard = bigBoard;
                state.currentPlayer = currentPlayer;
                state.activeBoard = activeBoard;
                state.gameOver = gameEnded;
                state.winner = winner;
                state.movesHistory = new ArrayList<>(moveHistory);

                oos.writeObject(state);
                JOptionPane.showMessageDialog(parent, "Game saved successfully!");
                return true;

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error saving game: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Loads a game state from a file
     * @param parent The parent component for dialog display
     * @return The loaded GameState, or null if loading failed
     */
    public static GameState loadGame(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fileChooser.getSelectedFile()))) {

                GameState state = (GameState) ois.readObject();
                JOptionPane.showMessageDialog(parent, "Game loaded successfully!");
                return state;

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(parent, "Error loading game: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
}