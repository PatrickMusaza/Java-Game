package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class WelcomeScreen extends JPanel {
    private List<String> carouselTexts;
    private int currentTextIndex = 0;
    private Timer carouselTimer;
    private JProgressBar progressBar;
    private Image backgroundImage;

    public WelcomeScreen() {
        loadTexts();
        loadBackground();
        setupUI();
        startCarousel();
    }

    private void loadBackground() {
        ImageIcon ii = new ImageIcon(getClass().getResource("/components/resources/images/background.jpg"));
        backgroundImage = ii.getImage();
    }

    private void loadTexts() {
        carouselTexts = new ArrayList<>();
        carouselTexts.add("AIRPLANE SHOOTER");
        carouselTexts.add("DEFEND THE SKIES");
        carouselTexts.add("DESTROY ENEMY ROCKETS");
        carouselTexts.add("COLLECT POWER-UPS");
        carouselTexts.add("BECOME THE BEST PILOT");
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Carousel panel with background and text display
        JPanel carouselPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw background
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Draw current text
                if (!carouselTexts.isEmpty()) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 36));

                    // Add text shadow for better visibility
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(getWidth() / 2 - 200, getHeight() / 3 - 30, 400, 60);

                    g.setColor(Color.WHITE);
                    FontMetrics fm = g.getFontMetrics();
                    String text = carouselTexts.get(currentTextIndex);
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = getHeight() / 3 + fm.getAscent() / 2;
                    g.drawString(text, x, y);
                }
            }
        };

        // Progress bar styling
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading Game...");
        progressBar.setForeground(new Color(0, 150, 0));
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        progressBar.setFont(new Font("Arial", Font.BOLD, 12));

        add(carouselPanel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        // Simulated loading with random increments
        Timer loadingTimer = new Timer(200, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int val = progressBar.getValue();
                if (val < 100) {
                    int increment = 1 + (int) (Math.random() * 3);
                    progressBar.setValue(Math.min(val + increment, 100));
                } else {
                    ((Timer) e.getSource()).stop();
                    AudioManager.playSound("loading_complete.wav");
                    Main.showHomeScreen();
                }
            }
        });
        loadingTimer.start();
    }

    private void startCarousel() {
        carouselTimer = new Timer(3000, e -> {
            currentTextIndex = (currentTextIndex + 1) % carouselTexts.size();
            repaint();
        });
        carouselTimer.start();
    }
}