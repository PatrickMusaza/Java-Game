package main;

import javax.swing.*;
import java.awt.*;

public class Player {
    private int x, y;
    private Image image;
    private int speed = 5;
    private boolean movingLeft, movingRight, movingUp, movingDown;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        ImageIcon ii = new ImageIcon(getClass().getResource("/components/resources/images/airplane.png"));
        image = ii.getImage();
    }

    public void update() {
        if (movingLeft && x > 0) {
            x -= speed;
        }
        if (movingRight && x < Constants.SCREEN_WIDTH - getWidth()) {
            x += speed;
        }
        if (movingUp && y > 0) {
            y -= speed;
        }
        if (movingDown && y < Constants.SCREEN_HEIGHT - getHeight()) {
            y += speed;
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

    // Getters and setters remain the same
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }
}