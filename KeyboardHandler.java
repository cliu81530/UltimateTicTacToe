package TicTacToe;

import java.awt.event.KeyEvent;


public class KeyboardHandler {
    private static final int BOARD_SIZE = 3;


    public interface MoveHandler {
        void makeMove(int bigRow, int bigCol, int cellIndex);
    }

    private final MoveHandler moveHandler;

    public KeyboardHandler(MoveHandler moveHandler) {
        this.moveHandler = moveHandler;
    }

 
    public void handleKeyPress(int keyCode, char[][][] gameBoard, char[][] bigBoard,
                               int activeBoard, boolean gameEnded) {
        if (gameEnded) {
            System.out.println("Game has ended, no further moves allowed.");
            return;
        }

        // numpad mapping: map numpad 7-9, 4-6, 1-3 to the same positions
        int[] numpadMapping = {
            KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,  // 位置 0,1,2
            KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,  // 位置 3,4,5
            KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3   // 位置 6,7,8
        };
        
        // normal key mapping: map 1-9 to the same positions
        int[] numberMapping = {
            KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9,  // 位置 0,1,2
            KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6,  // 位置 3,4,5
            KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3   // 位置 6,7,8
        };

        // 查找按键对应的棋盘位置
        int targetCellIndex = -1;
        
        // 先检查数字键盘
        for (int i = 0; i < numpadMapping.length; i++) {
            if (keyCode == numpadMapping[i]) {
                targetCellIndex = i;
                System.out.println("Numpad key pressed: " + (i + 1) + " -> Position " + i);
                break;
            }
        }
        
        // 如果数字键盘没匹配，检查普通数字键
        if (targetCellIndex == -1) {
            for (int i = 0; i < numberMapping.length; i++) {
                if (keyCode == numberMapping[i]) {
                    targetCellIndex = i;
                    System.out.println("Number keyboard: " + getKeyName(keyCode) + " -> Position " + i);
                    break;
                }
            }
        }

        // 如果没有找到对应的位置，返回
        if (targetCellIndex == -1) {
            // 只有在按下数字相关的键时才提示
            if (isNumberKey(keyCode)) {
                System.out.println("Invalid key: " + getKeyName(keyCode));
            }
            return;
        }

        // 根据当前激活棋盘找到合适的移动位置
        if (activeBoard == -1) {
            // 任意棋盘都可以，找第一个可用的位置
            System.out.println("Any available board for position " + targetCellIndex);
            for (int bigRow = 0; bigRow < BOARD_SIZE; bigRow++) {
                for (int bigCol = 0; bigCol < BOARD_SIZE; bigCol++) {
                    if (bigBoard[bigRow][bigCol] == ' ' && 
                        gameBoard[bigRow][bigCol][targetCellIndex] == ' ') {
                        System.out.println("Found available position: BigBoard(" + bigRow + "," + bigCol + ") Cell(" + targetCellIndex + ")");
                        moveHandler.makeMove(bigRow, bigCol, targetCellIndex);
                        return;
                    }
                }
            }
            System.out.println("Position " + targetCellIndex + " is not available on any board.");
        } else {
            // Specific board activated
            int bigRow = activeBoard / BOARD_SIZE;
            int bigCol = activeBoard % BOARD_SIZE;

            System.out.println("Activated board mode: BigBoard(" + bigRow + "," + bigCol + ") Cell(" + targetCellIndex + ")");

            if (bigBoard[bigRow][bigCol] == ' ' &&
                gameBoard[bigRow][bigCol][targetCellIndex] == ' ') {
                System.out.println("Making move on activated board");
                moveHandler.makeMove(bigRow, bigCol, targetCellIndex);
            } else {
                if (bigBoard[bigRow][bigCol] != ' ') {
                    System.out.println("Activated board is over, no further moves allowed.");
                } else {
                    System.out.println("Target position is already occupied.");
                }
            }
        }
    }

    /**
     * 判断是否是数字相关的按键
     */
    private boolean isNumberKey(int keyCode) {
        return (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) ||
               (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9);
    }

    /**
     * 获取按键名称（用于调试）
     */
    private String getKeyName(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1: return "1";
            case KeyEvent.VK_2: return "2";
            case KeyEvent.VK_3: return "3";
            case KeyEvent.VK_4: return "4";
            case KeyEvent.VK_5: return "5";
            case KeyEvent.VK_6: return "6";
            case KeyEvent.VK_7: return "7";
            case KeyEvent.VK_8: return "8";
            case KeyEvent.VK_9: return "9";
            case KeyEvent.VK_NUMPAD1: return "Numpad1";
            case KeyEvent.VK_NUMPAD2: return "Numpad2";
            case KeyEvent.VK_NUMPAD3: return "Numpad3";
            case KeyEvent.VK_NUMPAD4: return "Numpad4";
            case KeyEvent.VK_NUMPAD5: return "Numpad5";
            case KeyEvent.VK_NUMPAD6: return "Numpad6";
            case KeyEvent.VK_NUMPAD7: return "Numpad7";
            case KeyEvent.VK_NUMPAD8: return "Numpad8";
            case KeyEvent.VK_NUMPAD9: return "Numpad9";
            default: return "Unknown(" + keyCode + ")";
        }
    }

    /**
     * Keyboard layout and controls explanation
     */
    public static void printKeyboardLayout() {
        System.out.println("=== Instruction ===");
        System.out.println("Use the numpad or regular number keys to control:");
        System.out.println();
        System.out.println("Board position mapping:");
        System.out.println("┌─────┬─────┬─────┐");
        System.out.println("│  7  │  8  │  9  │");
        System.out.println("├─────┼─────┼─────┤");
        System.out.println("│  4  │  5  │  6  │");
        System.out.println("├─────┼─────┼─────┤");
        System.out.println("│  1  │  2  │  3  │");
        System.out.println("└─────┴─────┴─────┘");
        System.out.println();
        System.out.println("- Numpad keys 1-9 correspond to board positions");
        System.out.println("- Regular number keys 1-9 can also be used");
        System.out.println("- Pressing a key will place a mark in the corresponding position on the active board");
        System.out.println("- If the active board is 'Any', the first available board will be selected");
        System.out.println("=====================");
    }
}