package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JPanel {
    private JButton startButton, resumeButton, levelButton, helpButton, scoreButton, exitButton;

    public HomeScreen() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Create buttons
        startButton = createStyledButton("START");
        resumeButton = createStyledButton("RESUME");
        levelButton = createStyledButton("LEVEL");
        helpButton = createStyledButton("HELP");
        scoreButton = createStyledButton("SCORE");
        exitButton = createStyledButton("EXIT");

        // Initially disable resume button
        resumeButton.setEnabled(false);

        // Add action listeners
        startButton.addActionListener(e -> Main.startNewGame());
        resumeButton.addActionListener(e -> Main.resumeGame());
        levelButton.addActionListener(e -> Main.showLevelSelection());
        helpButton.addActionListener(e -> Main.showHelpScreen());
        scoreButton.addActionListener(e -> Main.showScoreScreen());
        exitButton.addActionListener(e -> System.exit(0));

        // Add components
        add(startButton, gbc);
        add(resumeButton, gbc);
        add(levelButton, gbc);
        add(helpButton, gbc);
        add(scoreButton, gbc);
        add(exitButton, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    public void enableResume(boolean enable) {
        resumeButton.setEnabled(enable);
    }
}