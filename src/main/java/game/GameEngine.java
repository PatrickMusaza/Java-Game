package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEngine extends JPanel implements Runnable {
    private Player player;
    private List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private List<Projectile> projectiles = new CopyOnWriteArrayList<>();
    private Thread gameThread;
    private volatile boolean running = false;
    private boolean paused = false;
    private Level currentLevel;
    private int score = 0;
    private int lives = 3;
    private boolean doubleRocketActive = false;
    private long doubleRocketEndTime = 0;
    private Random random = new Random();
    private Image backgroundImage;
    private long lastEnemySpawnTime = 0;

    public GameEngine(Level level) {
        this.currentLevel = level;
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        loadBackground();
        setupKeyListeners();

        player = new Player(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT - 100);
    }

    private void loadBackground() {
        ImageIcon ii = new ImageIcon(getClass().getResource("/images/background.jpg"));
        backgroundImage = ii.getImage();
    }

    private void setupKeyListeners() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                player.setX(e.getX() - player.getWidth() / 2);
                player.setY(e.getY() - player.getHeight() / 2);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    fireProjectile();
                }
            }
        });

        setFocusable(true);
        requestFocus();
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.setMovingLeft(true);
            case KeyEvent.VK_RIGHT -> player.setMovingRight(true);
            case KeyEvent.VK_UP -> player.setMovingUp(true);
            case KeyEvent.VK_DOWN -> player.setMovingDown(true);
            case KeyEvent.VK_SPACE, KeyEvent.VK_ENTER -> fireProjectile();
            case KeyEvent.VK_P -> togglePause();
        }
    }

    private void handleKeyRelease(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.setMovingLeft(false);
            case KeyEvent.VK_RIGHT -> player.setMovingRight(false);
            case KeyEvent.VK_UP -> player.setMovingUp(false);
            case KeyEvent.VK_DOWN -> player.setMovingDown(false);
        }
    }

    private void fireProjectile() {
        AudioManager.playSound("shoot.wav");
        int bulletX = player.getX() + player.getWidth() / 2 - 8;

        projectiles.add(new Projectile(bulletX, player.getY(), false));

        if (doubleRocketActive && System.currentTimeMillis() < doubleRocketEndTime) {
            projectiles.add(new Projectile(bulletX - 20, player.getY(), false));
            projectiles.add(new Projectile(bulletX + 20, player.getY(), false));
        }
    }

    public void startGame() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();
        } else if (paused) {
            paused = false;
            AudioManager.resumeBackgroundMusic();
        }
    }

    public void pauseGame() {
        paused = true;
        AudioManager.pauseBackgroundMusic();
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            AudioManager.pauseBackgroundMusic();
        } else {
            AudioManager.resumeBackgroundMusic();
        }
    }

    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1000000000.0 / 60.0;
        double delta = 0;

        while (running) {
            if (!paused) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerFrame;
                lastTime = now;

                while (delta >= 1) {
                    updateGame();
                    delta--;
                }

                repaint();
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemySpawnTime > currentLevel.getSpawnDelay()) {
            if (enemies.size() < currentLevel.getMaxEnemies()) {
                int x = random.nextInt(Constants.SCREEN_WIDTH - 50);
                int speed = currentLevel.getEnemySpeed() + random.nextInt(3);

                if (random.nextDouble() < 0.1) {
                    enemies.add(new Enemy(x, -30, 2, true)); // Bonus
                } else {
                    enemies.add(new Enemy(x, -50, speed));
                }

                lastEnemySpawnTime = currentTime;
            }
        }
    }

    private void updateGame() {
        player.update();
        spawnEnemies();

        for (Enemy enemy : enemies) {
            enemy.update();

            if (enemy.getY() > Constants.SCREEN_HEIGHT) {
                enemies.remove(enemy);
                if (!enemy.isBonus()) {
                    lives--;
                    if (lives <= 0) gameOver();
                }
            }

            if (player.getBounds().intersects(enemy.getBounds())) {
                handlePlayerEnemyCollision(enemy);
                continue;
            }

            for (Projectile projectile : projectiles) {
                if (projectile.getBounds().intersects(enemy.getBounds())) {
                    if (!enemy.isBonus()) {
                        score += currentLevel.getScorePerHit();
                    }
                    projectiles.remove(projectile);
                    enemies.remove(enemy);
                    AudioManager.playSound("explosion.wav");
                    break;
                }
            }
        }

        for (Projectile projectile : projectiles) {
            projectile.update();
            if (projectile.getY() < 0 || projectile.getY() > Constants.SCREEN_HEIGHT) {
                projectiles.remove(projectile);
            }
        }

        if (doubleRocketActive && System.currentTimeMillis() > doubleRocketEndTime) {
            doubleRocketActive = false;
        }
    }

    private void handlePlayerEnemyCollision(Enemy enemy) {
        if (enemy.isBonus()) {
            activateDoubleRocket();
            AudioManager.playSound("bonus_collect.wav");
        } else {
            lives--;
            AudioManager.playSound("life_lost.wav");
            if (lives <= 0) {
                gameOver();
            }
        }
        enemies.remove(enemy);
    }

    private void activateDoubleRocket() {
        doubleRocketActive = true;
        doubleRocketEndTime = System.currentTimeMillis() + 10000; // 10 seconds
        AudioManager.playSound("bonus.wav");
    }

    private void gameOver() {
        running = false;
        ScoreManager.saveScore(score);
        AudioManager.playSound("gameover.wav");
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score);
        Main.showHomeScreen();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw game elements
        player.draw(g);
        enemies.forEach(enemy -> enemy.draw(g));
        projectiles.forEach(projectile -> projectile.draw(g));

        // Draw HUD
        drawHUD(g);

        if (!running) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String loadingText = "Loading Game...";
            int textWidth = g.getFontMetrics().stringWidth(loadingText);
            g.drawString(loadingText, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

    private void drawHUD(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Lives: " + lives, 20, 60);

        if (doubleRocketActive) {
            long remaining = (doubleRocketEndTime - System.currentTimeMillis()) / 1000;
            g.drawString("Double Rocket: " + remaining + "s", 20, 90);
        }

        g.drawString("Level: " + currentLevel.getName(), Constants.SCREEN_WIDTH - 150, 30);
    }

    public boolean isPaused() {
        return paused;
    }

    public int getScore() {
        return score;
    }
}
