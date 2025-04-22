package game;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static Clip backgroundMusic;
    private static Map<String, File> soundFiles = new HashMap<>();
    private static float volume = 0.7f;

    static {
        // Register all sound files
        soundFiles.put("shoot.wav", new File("resources/sounds/shoot.wav"));
        soundFiles.put("explosion.wav", new File("resources/sounds/explosion.wav"));
        soundFiles.put("bonus.wav", new File("resources/sounds/bonus.wav"));
        soundFiles.put("gameover.wav", new File("resources/sounds/gameover.wav"));
        soundFiles.put("loading_complete.wav", new File("resources/sounds/loading_complete.wav"));
        soundFiles.put("bonus_collect.wav", new File("resources/sounds/bonus_collect.wav"));
        soundFiles.put("life_lost.wav", new File("resources/sounds/life_lost.wav"));

        // Load and configure background music
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                new File("resources/sounds/background.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            setVolume(volume); // Apply volume to background music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void playSound(String soundName) {
        if (!soundFiles.containsKey(soundName)) return;

        new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFiles.get(soundName));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                setClipVolume(clip, volume);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void setClipVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float gain = (float) (20f * Math.log10(Math.max(0.0001, volume))); // avoid log10(0)
            gain = Math.max(min, Math.min(max, gain));
            gainControl.setValue(gain);
        }
    }

    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public static void resumeBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void setVolume(float newVolume) {
        volume = Math.max(0f, Math.min(1f, newVolume)); // Clamp between 0 and 1
        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            setClipVolume(backgroundMusic, volume);
        }
    }
}
