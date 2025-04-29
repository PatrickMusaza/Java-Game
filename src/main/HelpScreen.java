package main;

import javax.swing.*;
import java.awt.*;

public class HelpScreen extends JPanel {
    public HelpScreen() {
        setLayout(new BorderLayout());

        JTextArea helpText = new JTextArea();
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(new Font("Arial", Font.PLAIN, 16));
        helpText.setBackground(new Color(240, 240, 240));

        String text = "HOW TO PLAY:\n\n" +
                "1. Use arrow keys or mouse to move your airplane\n" +
                "2. Press SPACE or ENTER to shoot, or click mouse button\n" +
                "3. Avoid enemy rockets and shoot them to earn points\n" +
                "4. Collect yellow bonus items to activate double rockets for 10 seconds\n" +
                "5. You have 3 lives - each hit by enemy costs one life\n" +
                "6. Press 'P' to pause the game\n\n" +
                "LEVELS:\n" +
                "- Easy: Slow enemies, fewer at a time\n" +
                "- Medium: Faster enemies, more at a time\n" +
                "- Hard: Very fast enemies, many at a time\n\n" +
                "SCORING:\n" +
                "- Easy: 10 points per hit\n" +
                "- Medium: 20 points per hit\n" +
                "- Hard: 30 points per hit";

        helpText.setText(text);

        JScrollPane scrollPane = new JScrollPane(helpText);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> Main.showHomeScreen());
        backButton.setFont(new Font("Arial", Font.BOLD, 16));

        add(scrollPane, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}