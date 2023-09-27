package gui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Класс MusicPlayer представляет собой объект для воспроизведения музыки.
 */
public class MusicPlayer {
    private Clip musicClip;
    private FloatControl volumeControl;

    /**
     * Создает объект MusicPlayer для воспроизведения музыки из указанного файла.
     *
     * @param filePath Путь к аудиофайлу для воспроизведения.
     */
    public MusicPlayer(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
            musicClip = AudioSystem.getClip();
            musicClip.open(audioInputStream);

            if (musicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Начинает воспроизведение музыки.
     */
    public void play() {
        if (musicClip != null && !musicClip.isRunning()) {
            musicClip.start();
        }
    }

    /**
     * Приостанавливает воспроизведение музыки.
     */
    public void pause() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
        }
    }

    /**
     * Останавливает воспроизведение музыки и освобождает ресурсы.
     */
    public void stop() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
        }
    }

    /**
     * Устанавливает громкость воспроизведения музыки.
     *
     * @param volume Значение громкости (от -80.0 до 6.0206).
     */
    public void setVolume(float volume) {
        if (volumeControl != null) {
            volumeControl.setValue(volume);
        }
    }
}