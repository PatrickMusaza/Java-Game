package main;

import javax.swing.*;
import java.awt.*;

public class Enemy {
    private int x, y;
    private Image image;
    private int speed;
    private boolean isBonus;

    public Enemy(int x, int y, int speed) {
        this(x, y, speed, false);
    }

    public Enemy(int x, int y, int speed, boolean isBonus) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isBonus = isBonus;
        loadImage();
    }

    private void loadImage() {
        String imagePath = isBonus ? "/components/resources/images/bonus.png"
                : "/components/resources/images/enemy.png";
        ImageIcon ii = new ImageIcon(getClass().getResource(imagePath));
        image = ii.getImage();
    }

    public void update() {
        y += speed;
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, getWidth(), getHeight());
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }

    // Getters
    public int getY() {
        return y;
    }

    public boolean isBonus() {
        return isBonus;
    }
}