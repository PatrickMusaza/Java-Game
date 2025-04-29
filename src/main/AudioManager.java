package main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static Clip backgroundMusic;
    private static Map<String, AudioInputStream> soundStreams = new HashMap<>();
    private static float volume = 0.7f;

    static {
        // Register all sound files as resources
        try {
            soundStreams.put("shoot.wav", getAudioInputStream("/sounds/shoot.wav"));
            soundStreams.put("explosion.wav", getAudioInputStream("/sounds/explosion.wav"));
            soundStreams.put("bonus.wav", getAudioInputStream("/sounds/bonus.wav"));
            soundStreams.put("gameover.wav", getAudioInputStream("/sounds/gameover.wav"));
            soundStreams.put("loading_complete.wav", getAudioInputStream("/sounds/loading_complete.wav"));
            soundStreams.put("bonus_collect.wav", getAudioInputStream("/sounds/bonus_collect.wav"));
            soundStreams.put("life_lost.wav", getAudioInputStream("/sounds/life_lost.wav"));

            // Load and configure background music
            AudioInputStream bgMusicStream = getAudioInputStream("/sounds/background.wav");
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(bgMusicStream);
            setVolume(volume); // Apply volume to background music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Helper method to load an audio file from the classpath
    private static AudioInputStream getAudioInputStream(String path) throws IOException, UnsupportedAudioFileException {
        InputStream resourceStream = AudioManager.class.getResourceAsStream(path);
        if (resourceStream == null) {
            throw new IOException("Audio resource not found: " + path);
        }
        return AudioSystem.getAudioInputStream(resourceStream);
    }

    // Play a sound effect from the sound map
    public static void playSound(String soundName) {
        if (!soundStreams.containsKey(soundName))
            return;

        new Thread(() -> {
            try {
                AudioInputStream audioInputStream = soundStreams.get(soundName);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                setClipVolume(clip, volume);
                clip.start();
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
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
