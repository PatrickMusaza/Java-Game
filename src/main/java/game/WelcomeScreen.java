package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class WelcomeScreen extends JPanel {
    private List<Image> carouselImages;
    private int currentImageIndex = 0;
    private Timer carouselTimer;
    private JProgressBar progressBar;
    private Image backgroundImage;

    public WelcomeScreen() {
        loadImages();
        loadBackground();
        setupUI();
        startCarousel();
    }

    private void loadBackground() {
        ImageIcon ii = new ImageIcon(getClass().getResource("/images/welcome_bg.jpg"));
        backgroundImage = ii.getImage();
    }

    private void loadImages() {
        carouselImages = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            ImageIcon ii = new ImageIcon(getClass().getResource("/images/welcome_" + i + ".png"));
            carouselImages.add(ii.getImage());
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Carousel panel with background and image centering
        JPanel carouselPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                if (!carouselImages.isEmpty()) {
                    Image img = carouselImages.get(currentImageIndex);
                    int imgWidth = img.getWidth(this);
                    int imgHeight = img.getHeight(this);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = (getHeight() - imgHeight) / 3;
                    g.drawImage(img, x, y, this);
                }
            }
        };

        // Progress bar styling
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 150, 0));
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        progressBar.setFont(new Font("Arial", Font.BOLD, 12));

        add(carouselPanel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        // Simulated random loading
        Timer loadingTimer = new Timer(50, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int val = progressBar.getValue();
                if (val < 100) {
                    int increment = 1 + (int)(Math.random() * 3); // Random increment between 1-3
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
            currentImageIndex = (currentImageIndex + 1) % carouselImages.size();
            repaint();
        });
        carouselTimer.start();
    }
}
