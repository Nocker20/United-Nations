package resources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;

public class SoundEffectPlayer {

    public static void sound(String a) {
        File file = new File(a);
        System.out.println(file.exists()); // true

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
