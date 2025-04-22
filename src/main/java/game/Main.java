package game;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;
    private static WelcomeScreen welcomeScreen;
    private static HomeScreen homeScreen;
    private static GameEngine gameEngine;
    private static HelpScreen helpScreen;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Airplane Shooting Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            frame.setExtendedState(1);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);
            
            welcomeScreen = new WelcomeScreen();
            homeScreen = new HomeScreen();
            helpScreen = new HelpScreen();
            
            cardPanel.add(welcomeScreen, "Welcome");
            cardPanel.add(homeScreen, "Home");
            cardPanel.add(helpScreen, "Help");
            
            frame.add(cardPanel);
            frame.setVisible(true);
            
            // Play background music
            AudioManager.playBackgroundMusic();
        });
    }
    
    public static void showHomeScreen() {
        cardLayout.show(cardPanel, "Home");
        if (gameEngine != null) {
            homeScreen.enableResume(true);
        }
    }
    
    public static void startNewGame() {
        // Default to easy level
        startNewGame(Level.getEasyLevel());
    }
    
    public static void startNewGame(Level level) {
        if (gameEngine != null) {
            cardPanel.remove(gameEngine);
        }
        gameEngine = new GameEngine(level);
        cardPanel.add(gameEngine, "Game");
        cardLayout.show(cardPanel, "Game");
        gameEngine.startGame();
        homeScreen.enableResume(true);
    }
    
    public static void resumeGame() {
        if (gameEngine != null) {
            cardLayout.show(cardPanel, "Game");
            gameEngine.startGame();
        }
    }
    
    public static void showLevelSelection() {
        Object[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(frame,
            "Select Game Level",
            "Level Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice != JOptionPane.CLOSED_OPTION) {
            Level level;
            switch (choice) {
                case 0: level = Level.getEasyLevel(); break;
                case 1: level = Level.getMediumLevel(); break;
                case 2: level = Level.getHardLevel(); break;
                default: level = Level.getEasyLevel();
            }
            startNewGame(level);
        }
    }
    
    public static void showHelpScreen() {
        cardLayout.show(cardPanel, "Help");
    }
    
    public static void showScoreScreen() {
        JPanel scorePanel = ScoreManager.getScorePanel();
        cardPanel.add(scorePanel, "Scores");
        cardLayout.show(cardPanel, "Scores");
    }
}