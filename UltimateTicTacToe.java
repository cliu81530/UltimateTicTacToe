package TicTacToe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UltimateTicTacToe extends JFrame implements KeyboardHandler.MoveHandler {
    private static final int BOARD_SIZE = 3;
    
    // 游戏状态
    private char[][][] gameBoard;
    private char[][] bigBoard;
    private char currentPlayer = 'X';
    private int activeBoard = -1;
    private boolean gameEnded = false;
    private char winner = ' ';
    private List<String> moveHistory;
    
    // UI组件
    private JButton[][][] cellButtons;
    private JLabel statusLabel;
    private JTextArea moveHistoryArea;
    private JButton testButton, restartButton, keyboardHelpButton,saveButton, loadButton;
    
    // 键盘处理器
    private KeyboardHandler keyboardHandler;
    
    public UltimateTicTacToe() {
        initializeGame();
        setupSimpleUI();
        setupKeyListener();
        printGameState();
        
        // 显示键盘控制说明
        KeyboardHandler.printKeyboardLayout();
    }
    
    private void initializeGame() {
        gameBoard = new char[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE * BOARD_SIZE];
        bigBoard = new char[BOARD_SIZE][BOARD_SIZE];
        cellButtons = new JButton[BOARD_SIZE][BOARD_SIZE][BOARD_SIZE * BOARD_SIZE];
        moveHistory = new ArrayList<>();
        
        // Initialize all positions to empty
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                bigBoard[i][j] = ' ';
                for (int k = 0; k < BOARD_SIZE * BOARD_SIZE; k++) {
                    gameBoard[i][j][k] = ' ';
                }
            }
        }
        
        // Initialize cell buttons
        keyboardHandler = new KeyboardHandler(this);
    }
    
    private void setupSimpleUI() {
        setTitle("Ultimate Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 主面板：9个小棋盘
        JPanel mainPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.DARK_GRAY);
        
        // 创建9个小棋盘
        for (int bigRow = 0; bigRow < BOARD_SIZE; bigRow++) {
            for (int bigCol = 0; bigCol < BOARD_SIZE; bigCol++) {
                JPanel smallBoard = createSmallBoard(bigRow, bigCol);
                mainPanel.add(smallBoard);
            }
        }
        
        // 右侧信息面板
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(300, 600));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 状态显示和按钮
        JPanel topPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        statusLabel = new JLabel("Player X's turn - Can play anywhere", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        testButton = new JButton("Logic Tests");
        testButton.addActionListener(e -> runLogicTests());

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        keyboardHelpButton = new JButton("Keyboard Controls");
        keyboardHelpButton.addActionListener(e -> showKeyboardHelp());

        saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> GameIO.saveGame(this, gameBoard, bigBoard,
                                                           currentPlayer, activeBoard,
                                                           gameEnded, winner, moveHistory));
        
        loadButton = new JButton("Load Game");
        loadButton.addActionListener(e-> {
            GameState loadedState = GameIO.loadGame(this);
            if (loadedState != null) {
                // 更新游戏状态
                gameBoard = loadedState.gameBoard;
                bigBoard = loadedState.bigBoard;
                currentPlayer = loadedState.currentPlayer;
                activeBoard = loadedState.activeBoard;
                gameEnded = loadedState.gameOver;
                winner = loadedState.winner;
                moveHistory = loadedState.movesHistory;
                // 更新按钮显示
                // 更新UI
                updateMoveHistory();
                updateUI();
                
                statusLabel.setText("Player " + currentPlayer + "'s turn - " +
                                    (activeBoard == -1 ? "Can play anywhere" : 
                                     "棋盘(" + (activeBoard/3 + 1) + "," + (activeBoard%3 + 1) + ")"));
            }
        });

        topPanel.add(statusLabel);
        topPanel.add(testButton);
        topPanel.add(restartButton);
        topPanel.add(keyboardHelpButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        
        // 移动历史
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(280, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Move History"));
        
        // // 调试信息区域
        // JTextArea debugArea = new JTextArea();
        // debugArea.setEditable(false);
        // debugArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        // debugArea.setText(getDebugInfo());
        // JScrollPane debugScroll = new JScrollPane(debugArea);
        // debugScroll.setPreferredSize(new Dimension(280, 200));
        // debugScroll.setBorder(BorderFactory.createTitledBorder("Debug Info"));
        
        infoPanel.add(topPanel, BorderLayout.NORTH);
        infoPanel.add(scrollPane, BorderLayout.CENTER);
        // infoPanel.add(debugScroll, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        updateUI();
    }
    

    private void setupKeyListener() {
        // 让窗口能够接收键盘焦点
        setFocusable(true);
        requestFocusInWindow();
        
        // 添加键盘监听器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("按键检测: " + KeyEvent.getKeyText(e.getKeyCode()) + " (代码: " + e.getKeyCode() + ")");
                keyboardHandler.handleKeyPress(e.getKeyCode(), gameBoard, bigBoard, 
                                               activeBoard, gameEnded);
            }
        });
        
        // 确保窗口获得焦点时能接收键盘事件
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                requestFocusInWindow();
            }
        });
        
        System.out.println("keyboard listener setup complete.");
    }
    
    private JPanel createSmallBoard(int bigRow, int bigCol) {
        JPanel smallBoard = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 2, 2));
        smallBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        smallBoard.setPreferredSize(new Dimension(120, 120));
        
        // 添加标题
        String title = "Board(" + (bigRow + 1) + "," + (bigCol + 1) + ")";
        smallBoard.setBorder(BorderFactory.createTitledBorder(title));
        
        // 创建9个按钮
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellIndex = row * BOARD_SIZE + col;
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(35, 35));
                button.setFont(new Font("Arial", Font.BOLD, 24)); // 大字体显示X和O
                
                // 设置按钮外观
                button.setBackground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                
                // 显示位置编号（调试用）
                button.setText(String.valueOf(cellIndex + 1));
                button.setForeground(Color.LIGHT_GRAY);
                
                final int fBigRow = bigRow;
                final int fBigCol = bigCol;
                final int fCellIndex = cellIndex;
                
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        makeMove(fBigRow, fBigCol, fCellIndex);
                    }
                });
                
                cellButtons[bigRow][bigCol][cellIndex] = button;
                smallBoard.add(button);
            }
        }
        
        return smallBoard;
    }
    
    /**
     * 处理玩家移动 - 实现KeyboardHandler.MoveHandler接口
     * 这个方法会被鼠标点击和键盘按键共同调用
     */
    @Override
    public void makeMove(int bigRow, int bigCol, int cellIndex) {
        System.out.println("\n=== 尝试移动 ===");
        System.out.println("位置: BigBoard(" + bigRow + "," + bigCol + ") Cell(" + cellIndex + ")");
        System.out.println("当前玩家: " + currentPlayer);
        System.out.println("激活棋盘: " + activeBoard);
        
        boolean isValid = GameLogic.isValidMove(gameBoard, bigBoard, bigRow, bigCol, 
                                                 cellIndex, activeBoard, gameEnded);
        
        System.out.println("移动是否有效: " + isValid);
        
        if (!isValid) {
            System.out.println("移动无效！");
            return;
        }
        
        // 执行移动
        gameBoard[bigRow][bigCol][cellIndex] = currentPlayer;
        updateCellButton(bigRow, bigCol, cellIndex);
        
        // 记录移动
        String move = GameLogic.formatMove(currentPlayer, bigRow, bigCol, cellIndex);
        moveHistory.add(move);
        updateMoveHistory();

        System.out.println("Move executed: " + move);

        // 检查小棋盘获胜
        char smallWinner = GameLogic.checkSmallBoardWinner(gameBoard[bigRow][bigCol]);
        System.out.println("Small board winner: " + (smallWinner == ' ' ? "None" : smallWinner));

        if (smallWinner != ' ') {
            bigBoard[bigRow][bigCol] = smallWinner;
            highlightWonBoard(bigRow, bigCol, smallWinner);
            System.out.println("Small board (" + bigRow + "," + bigCol + ") won by " + smallWinner);
        }
        
        // 检查大棋盘获胜
        char bigWinner = GameLogic.checkBigBoardWinner(bigBoard);
        System.out.println("Big board winner: " + (bigWinner == ' ' ? "None" : bigWinner));

        if (bigWinner != ' ') {
            winner = bigWinner;
            gameEnded = true;
            statusLabel.setText("🎉 Player " + winner + " wins the game! 🎉");
            System.out.println("🎉 Game over! Winner: " + winner + " 🎉");
        } else if (GameLogic.isBigBoardFull(bigBoard)) {
            gameEnded = true;
            statusLabel.setText("It's a tie!");
            System.out.println("Game over: It's a tie");
        }
        
        // 如果游戏没结束，切换玩家
        if (!gameEnded) {
            activeBoard = GameLogic.getNextActiveBoard(cellIndex, bigBoard);
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            
            String activeBoardText = (activeBoard == -1) ? "Anywhere" : 
                                   "Board(" + (activeBoard/3 + 1) + "," + (activeBoard%3 + 1) + ")";
            statusLabel.setText("Player " + currentPlayer + "'s turn - " + activeBoardText);

            System.out.println("Next turn: Player " + currentPlayer + ", Active board: " + activeBoard);
        }
        
        updateUI();
        printGameState();
        
        // 确保窗口保持键盘焦点
        requestFocusInWindow();
    }
    
    /**
     * 更新按钮显示 - 现在使用文本而不是图像
     */
    private void updateCellButton(int bigRow, int bigCol, int cellIndex) {
        JButton button = cellButtons[bigRow][bigCol][cellIndex];
        char player = gameBoard[bigRow][bigCol][cellIndex];
        
        if (player == 'X') {
            button.setText("X");
            button.setFont(new Font("Arial", Font.BOLD, 28));
            button.setForeground(Color.RED);
            button.setBackground(new Color(255, 200, 200));
        } else if (player == 'O') {
            button.setText("O");
            button.setFont(new Font("Arial", Font.BOLD, 28));
            button.setForeground(Color.BLUE);
            button.setBackground(new Color(200, 200, 255));
        }
        
        button.setEnabled(false);
        button.setBorder(BorderFactory.createLoweredBevelBorder());
    }
    
    /**
     * 高亮获胜的小棋盘
     */
    private void highlightWonBoard(int bigRow, int bigCol, char winner) {
        Color color = (winner == 'X') ? new Color(255, 150, 150) : new Color(150, 150, 255);
        
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            cellButtons[bigRow][bigCol][i].setBackground(color);
        }
    }
    
    /**
     * 更新UI状态（高亮可用位置）
     */
    private void updateUI() {
        for (int bigRow = 0; bigRow < BOARD_SIZE; bigRow++) {
            for (int bigCol = 0; bigCol < BOARD_SIZE; bigCol++) {
                boolean isBoardActive = (activeBoard == -1) || 
                                       (activeBoard == bigRow * BOARD_SIZE + bigCol);
                boolean isBoardWon = (bigBoard[bigRow][bigCol] != ' ');
                
                for (int cellIndex = 0; cellIndex < BOARD_SIZE * BOARD_SIZE; cellIndex++) {
                    JButton button = cellButtons[bigRow][bigCol][cellIndex];
                    
                    if (gameBoard[bigRow][bigCol][cellIndex] == ' ' && 
                        !isBoardWon && isBoardActive && !gameEnded) {
                        // 可以下棋的位置 - 高亮
                        button.setBackground(Color.YELLOW);
                        button.setEnabled(true);
                        button.setText(String.valueOf(cellIndex + 1));
                        button.setForeground(Color.GRAY);
                    } else if (gameBoard[bigRow][bigCol][cellIndex] == ' ' && !isBoardWon) {
                        // 不能下棋的位置 - 普通
                        button.setBackground(Color.WHITE);
                        button.setEnabled(false);
                        button.setText(String.valueOf(cellIndex + 1));
                        button.setForeground(Color.LIGHT_GRAY);
                    }
                }
            }
        }
    }
    
    /**
     * 重新开始游戏
     */
    private void restartGame() {
        // 重置游戏状态
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                bigBoard[i][j] = ' ';
                for (int k = 0; k < BOARD_SIZE * BOARD_SIZE; k++) {
                    gameBoard[i][j][k] = ' ';
                    JButton button = cellButtons[i][j][k];
                    button.setText(String.valueOf(k + 1));
                    button.setForeground(Color.LIGHT_GRAY);
                    button.setBackground(Color.WHITE);
                    button.setEnabled(true);
                    button.setBorder(BorderFactory.createRaisedBevelBorder());
                }
            }
        }
        
        currentPlayer = 'X';
        activeBoard = -1;
        gameEnded = false;
        winner = ' ';
        moveHistory.clear();
        
        statusLabel.setText("Player X's turn - Can play anywhere");
        updateMoveHistory();
        updateUI();
        
        System.out.println("游戏重新开始！");
        printGameState();
        
        // 确保窗口保持键盘焦点
        requestFocusInWindow();
    }
    
    /**
     * 显示键盘控制帮助
     */
    private void showKeyboardHelp() {
        String helpText = "Instructions:\n\n" +
                         "Use the numpad or regular number keys 1-9 to control:\n\n" +
                         "Board position mapping:\n" +
                         "┌─────┬─────┬─────┐\n" +
                         "│  7  │  8  │  9  │\n" +
                         "├─────┼─────┼─────┤\n" +
                         "│  4  │  5  │  6  │\n" +
                         "├─────┼─────┼─────┤\n" +
                         "│  1  │  2  │  3  │\n" +
                         "└─────┴─────┴─────┘\n\n" +
                         "Usage:\n" +
                         "• Press number keys 1-9 to make a move in the corresponding position\n" +
                         "• Keyboard layout corresponds to tic-tac-toe positions\n" +
                         "• Supports both numpad and regular number keys\n" +
                         "• Will automatically make a move in the currently active board\n\n" +
                         "Note: Make sure the game window is focused to use keyboard controls";

        JOptionPane.showMessageDialog(this, helpText, "Keyboard Controls",
                                      JOptionPane.INFORMATION_MESSAGE);
        
        // 显示对话框后重新获取焦点
        requestFocusInWindow();
    }
    
    private void updateMoveHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = moveHistory.size() - 1; i >= 0; i--) {
            sb.append((moveHistory.size() - i)).append(". ");
            sb.append(moveHistory.get(i)).append("\n");
        }
        moveHistoryArea.setText(sb.toString());
    }
    
    private void printGameState() {
        System.out.println("\n=== Current Game State ===");
        System.out.println("Current Player: " + currentPlayer);
        System.out.println("Active Board: " + activeBoard);
        System.out.println("Game Over: " + gameEnded);
        System.out.println("Winner: " + winner);

        System.out.println("\nBig Board State:");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(bigBoard[i][j] + " ");
            }
            System.out.println();
        }
    }
    

// 在UltimateTicTacToe.java中替换runLogicTests()方法

/**
 * 运行Ultimate Tic Tac Toe大棋盘测试
 */
private void runLogicTests() {
    System.out.println("\n🧪 开始运行Ultimate Tic Tac Toe大棋盘测试...");
    
    int passed = 0;
    int total = 3;
    
    // 测试1: X获胜大棋盘 - 第一行
    System.out.println("\n=== 测试1: X获胜大棋盘（第一行）===");
    char[][] xWinBigBoard = {
        {'X', 'X', 'X'},  // X获胜第一行
        {'O', 'O', ' '},  
        {' ', ' ', ' '}   
    };
    
    char result1 = GameLogic.checkBigBoardWinner(xWinBigBoard);
    if (result1 == 'X') {
        System.out.println("✅ X获胜大棋盘测试通过");
        passed++;
    } else {
        System.out.println("❌ X获胜大棋盘测试失败，期望X，得到" + result1);
    }
    
    // 打印大棋盘状态
    System.out.println("大棋盘状态:");
    printBigBoard(xWinBigBoard);
    printBigBoardExplanation(xWinBigBoard, "X获胜了第一行的三个小棋盘");
    
    // 测试2: O获胜大棋盘 - 对角线
    System.out.println("\n=== 测试2: O获胜大棋盘（主对角线）===");
    char[][] oWinBigBoard = {
        {'O', 'X', 'X'},  
        {'X', 'O', ' '},  
        {' ', ' ', 'O'}   // O获胜主对角线
    };
    
    char result2 = GameLogic.checkBigBoardWinner(oWinBigBoard);
    if (result2 == 'O') {
        System.out.println("✅ O获胜大棋盘测试通过");
        passed++;
    } else {
        System.out.println("❌ O获胜大棋盘测试失败，期望O，得到" + result2);
    }
    
    // 打印大棋盘状态
    System.out.println("大棋盘状态:");
    printBigBoard(oWinBigBoard);
    printBigBoardExplanation(oWinBigBoard, "O获胜了主对角线的三个小棋盘");
    
    // 测试3: 大棋盘平局
    System.out.println("\n=== 测试3: 大棋盘平局 ===");
    char[][] tieBigBoard = {
        {'X', 'O', 'X'},  
        {'O', 'X', 'O'},  
        {'O', 'X', 'O'}   // 平局 - 大棋盘满了但没有获胜者
    };
    
    char result3 = GameLogic.checkBigBoardWinner(tieBigBoard);
    boolean isFull = GameLogic.isBigBoardFull(tieBigBoard);
    
    if (result3 == ' ' && isFull) {
        System.out.println("✅ Big board Tie test passed");
        passed++;
    } else {
        System.out.println("❌ Big board Tie test failed");
        System.out.println("   Winner check: " + (result3 == ' ' ? "Correct" : "Incorrect, got " + result3));
        System.out.println("   Full board check: " + (isFull ? "Correct" : "Incorrect"));
    }
    
    // 打印大棋盘状态
    System.out.println("Big board state:");
    printBigBoard(tieBigBoard);
    printBigBoardExplanation(tieBigBoard, "All small boards are filled, no winner");
    
    // 额外测试：演示完整的游戏场景
    demonstrateGameScenario();
    
    // 测试总结
    System.out.println("\n" + "=".repeat(50));
    System.out.println("🧪 Ultimate Tic Tac Toe Big Board passed");
    System.out.println("Pass: " + passed + "/" + total + " Passed");

    if (passed == total) {
        System.out.println("🎉 All tests passed!");
    } else {
        System.out.println("⚠️ " + (total - passed) + " tests failed");
    }
    System.out.println("=".repeat(50));
    
    // 显示结果对话框
    String message = "Ultimate Tic Tac Toe Big Board 测试结果:\n\n" +
                    "✅ X获胜测试（第一行）: " + (result1 == 'X' ? "通过" : "失败") + "\n" +
                    "✅ O获胜测试（对角线）: " + (result2 == 'O' ? "通过" : "失败") + "\n" +
                    "✅ 平局测试: " + (result3 == ' ' && isFull ? "通过" : "失败") + "\n\n" +
                    "总计: " + passed + "/" + total + " 通过\n\n" +
                    "详细信息请查看控制台输出。";
    
    JOptionPane.showMessageDialog(this, message, "大棋盘测试结果", JOptionPane.INFORMATION_MESSAGE);
    
    // 确保窗口保持键盘焦点
    requestFocusInWindow();
}

/**
 * 打印3x3大棋盘的辅助方法
 */
private void printBigBoard(char[][] bigBoard) {
    System.out.println("┌─────────┬─────────┬─────────┐");
    for (int row = 0; row < 3; row++) {
        System.out.print("│");
        for (int col = 0; col < 3; col++) {
            char cell = bigBoard[row][col];
            String cellStr;
            if (cell == ' ') {
                cellStr = "  空   ";
            } else {
                cellStr = "  " + cell + "   ";
            }
            System.out.print(cellStr + "│");
        }
        System.out.println();
        if (row < 2) {
            System.out.println("├─────────┼─────────┼─────────┤");
        }
    }
    System.out.println("└─────────┴─────────┴─────────┘");
}

/**
 * 解释大棋盘状态
 */
private void printBigBoardExplanation(char[][] bigBoard, String explanation) {
    System.out.println("\n说明: " + explanation);
    System.out.println("大棋盘位置编号:");
    System.out.println("┌─────────┬─────────┬─────────┐");
    System.out.println("│ 棋盘(1,1) │ 棋盘(1,2) │ 棋盘(1,3) │");
    System.out.println("├─────────┼─────────┼─────────┤");
    System.out.println("│ 棋盘(2,1) │ 棋盘(2,2) │ 棋盘(2,3) │");
    System.out.println("├─────────┼─────────┼─────────┤");
    System.out.println("│ 棋盘(3,1) │ 棋盘(3,2) │ 棋盘(3,3) │");
    System.out.println("└─────────┴─────────┴─────────┘");
    
    // 显示每个位置的获胜者
    System.out.println("\n各小棋盘获胜者:");
    for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 3; col++) {
            char winner = bigBoard[row][col];
            String status = (winner == ' ') ? "未决定" : "获胜者: " + winner;
            System.out.println("棋盘(" + (row+1) + "," + (col+1) + "): " + status);
        }
    }
}

/**
 * 演示一个完整的游戏场景
 */
private void demonstrateGameScenario() {
    System.out.println("\n" + "=".repeat(60));
    System.out.println("🎮 演示完整游戏场景");
    System.out.println("=".repeat(60));
    
    // 创建一个更复杂的游戏状态
    char[][] gameScenario = {
        {'X', 'O', ' '},  // 第一行：X, O, 空
        {' ', 'X', 'O'},  // 第二行：空, X, O  
        {'O', ' ', 'X'}   // 第三行：O, 空, X
    };
    
    System.out.println("当前游戏状态:");
    printBigBoard(gameScenario);
    
    char winner = GameLogic.checkBigBoardWinner(gameScenario);
    boolean isFull = GameLogic.isBigBoardFull(gameScenario);
    
    System.out.println("\n游戏分析:");
    System.out.println("- 大棋盘获胜者: " + (winner == ' ' ? "暂无" : winner));
    System.out.println("- 大棋盘是否满了: " + (isFull ? "是" : "否"));
    System.out.println("- 游戏状态: " + getGameStatus(winner, isFull));
    
    // 分析获胜可能性
    analyzeWinningPossibilities(gameScenario);
}

/**
 * 获取游戏状态描述
 */
private String getGameStatus(char winner, boolean isFull) {
    if (winner != ' ') {
        return "游戏结束 - " + winner + " 获胜！";
    } else if (isFull) {
        return "游戏结束 - 平局！";
    } else {
        return "游戏继续中";
    }
}

/**
 * 分析获胜可能性
 */
private void analyzeWinningPossibilities(char[][] bigBoard) {
    System.out.println("\n获胜分析:");
    
    // 检查X的获胜可能性
    System.out.println("X的获胜机会:");
    checkWinningChances(bigBoard, 'X');
    
    System.out.println("\nO的获胜机会:");
    checkWinningChances(bigBoard, 'O');
}

/**
 * 检查指定玩家的获胜机会
 */
private void checkWinningChances(char[][] bigBoard, char player) {
    boolean hasChance = false;
    
    // 检查行
    for (int row = 0; row < 3; row++) {
        int count = 0;
        int emptyCount = 0;
        for (int col = 0; col < 3; col++) {
            if (bigBoard[row][col] == player) count++;
            else if (bigBoard[row][col] == ' ') emptyCount++;
        }
        if (count == 2 && emptyCount == 1) {
            System.out.println("  - 可以通过获胜第" + (row+1) + "行来获胜");
            hasChance = true;
        }
    }
    
    // 检查列
    for (int col = 0; col < 3; col++) {
        int count = 0;
        int emptyCount = 0;
        for (int row = 0; row < 3; row++) {
            if (bigBoard[row][col] == player) count++;
            else if (bigBoard[row][col] == ' ') emptyCount++;
        }
        if (count == 2 && emptyCount == 1) {
            System.out.println("  - 可以通过获胜第" + (col+1) + "列来获胜");
            hasChance = true;
        }
    }
    
    // 检查主对角线
    int diagCount = 0;
    int diagEmpty = 0;
    for (int i = 0; i < 3; i++) {
        if (bigBoard[i][i] == player) diagCount++;
        else if (bigBoard[i][i] == ' ') diagEmpty++;
    }
    if (diagCount == 2 && diagEmpty == 1) {
        System.out.println("  - 可以通过获胜主对角线来获胜");
        hasChance = true;
    }
    
    // 检查反对角线
    int antiDiagCount = 0;
    int antiDiagEmpty = 0;
    for (int i = 0; i < 3; i++) {
        if (bigBoard[i][2-i] == player) antiDiagCount++;
        else if (bigBoard[i][2-i] == ' ') antiDiagEmpty++;
    }
    if (antiDiagCount == 2 && antiDiagEmpty == 1) {
        System.out.println("  - 可以通过获胜反对角线来获胜");
        hasChance = true;
    }
    
    if (!hasChance) {
        System.out.println("  - 暂时没有直接获胜机会");
    }
}    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("🎮 Boot Ultimate Tic Tac (Support Keyboard interaction)...");
            new UltimateTicTacToe().setVisible(true);
        });
    }
}