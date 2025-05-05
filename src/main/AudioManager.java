package main;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static Clip backgroundMusic;
    private static final Map<String, Clip> soundClips = new HashMap<>();
    private static float volume = 0.7f;

    static {
        loadAllSounds();
    }

    private static void loadAllSounds() {
        new Thread(() -> {
            // Sound effects
            loadSoundAsResource("fixed_shoot.wav");
            loadSoundAsResource("explosion.wav");
            loadSoundAsResource("fixed_bonus.wav");
            loadSoundAsResource("fixed_gameover.wav");
            loadSoundAsResource("fixed_loading_complete.wav");
            loadSoundAsResource("fixed_life_lost.wav");
            loadSoundAsResource("fixed_background.wav");

            // Background music
            loadBackgroundMusic();
            System.out.println("Audio loading complete");
        }).start();
    }

    private static void loadSoundAsResource(String filename) {
        try {
            URL soundUrl = AudioManager.class.getResource("/components/resources/sounds/" + filename);
            if (soundUrl == null) {
                System.err.println("Resource not found: " + filename);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundClips.put(filename, clip);
            System.out.println("Successfully loaded: " + filename);
        } catch (Exception e) {
            System.err.println("Failed to load " + filename + ":");
            e.printStackTrace();
        }
    }

    private static void loadBackgroundMusic() {
        try {
            URL musicUrl = AudioManager.class.getResource("/components/resources/sounds/fixed_background.wav");
            if (musicUrl == null) {
                System.err.println("Background music not found");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicUrl);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            setClipVolume(backgroundMusic, volume);
            System.out.println("Loaded background music");
        } catch (Exception e) {
            System.err.println("Failed to load background music:");
            e.printStackTrace();
        }
    }

    public static void playSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip == null) {
            System.err.println("Sound not available: " + soundName);
            return;
        }

        // Stop and rewind if already playing
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    // Set the volume of a given clip
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

    // Play the background music in a loop
    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Pause the background music
    public static void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    // Resume the background music
    public static void resumeBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Set the volume of the background music
    public static void setVolume(float newVolume) {
        volume = Math.max(0f, Math.min(1f, newVolume)); // Clamp between 0 and 1
        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            setClipVolume(backgroundMusic, volume);
        }
    }
}
