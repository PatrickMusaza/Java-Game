package main;

import javax.swing.*;
import java.awt.*;

public class Projectile {
    private int x, y;
    private Image image;
    private int speed = 10;
    private boolean isEnemy;

    public Projectile(int x, int y, boolean isEnemy) {
        this.x = x;
        this.y = y;
        this.isEnemy = isEnemy;
        loadImage();
    }

    private void loadImage() {
        String imagePath = isEnemy ? "/components/resources/images/enemy.png"
                : "/components/resources/images/bullet.png";
        ImageIcon ii = new ImageIcon(getClass().getResource(imagePath));
        image = ii.getImage();
    }

    public void update() {
        if (isEnemy) {
            y += speed;
        } else {
            y -= speed;
        }
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
}