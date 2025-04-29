package main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String SCORE_FILE = "scores.dat";
    private static Map<String, Integer> highScores = new TreeMap<>(Collections.reverseOrder());

    static {
        loadScores();
    }

    private static void loadScores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORE_FILE))) {
            highScores = (Map<String, Integer>) ois.readObject();
        } catch (FileNotFoundException e) {
            // First run, no scores yet
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScore(int score) {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        highScores.put(date, score);
        saveScores();
    }

    public static JPanel getScorePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea scoreArea = new JTextArea();
        scoreArea.setEditable(false);
        scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        StringBuilder sb = new StringBuilder();
        sb.append("DATE/TIME                SCORE\n");
        sb.append("--------------------------------\n");

        highScores.entrySet().stream()
                .limit(10) // Show top 10 scores
                .forEach(entry -> sb.append(String.format("%-20s %5d\n", entry.getKey(), entry.getValue())));

        scoreArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(scoreArea);

        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> Main.showHomeScreen());

        JButton resetButton = new JButton("Reset Scores");
        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(panel,
                    "Are you sure you want to reset all scores?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                highScores.clear();
                saveScores();
                panel.removeAll();
                panel.add(getScorePanel(), BorderLayout.CENTER);
                panel.revalidate();
                panel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(backButton);
        buttonPanel.add(resetButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}