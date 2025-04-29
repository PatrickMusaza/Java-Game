package main;

public class Level {
    private String name;
    private int enemySpeed;
    private int spawnDelay;
    private int maxEnemies;
    private int scorePerHit;

    public Level(String name, int enemySpeed, int spawnDelay, int maxEnemies, int scorePerHit) {
        this.name = name;
        this.enemySpeed = enemySpeed;
        this.spawnDelay = spawnDelay;
        this.maxEnemies = maxEnemies;
        this.scorePerHit = scorePerHit;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getEnemySpeed() {
        return enemySpeed;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public int getScorePerHit() {
        return scorePerHit;
    }

    // Factory methods for predefined levels
    public static Level getEasyLevel() {
        return new Level("Easy", 2, 2000, 5, 10);
    }

    public static Level getMediumLevel() {
        return new Level("Medium", 4, 1500, 8, 20);
    }

    public static Level getHardLevel() {
        return new Level("Hard", 6, 1000, 12, 30);
    }
}