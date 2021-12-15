package resources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.*;

public class SoundEffectPlayer {



    public static void sound(String a) {
        File file = new File(a);

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clip.start();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
    private static AudioInputStream background;
    private  static Clip clip;
    public static void backgroundsound(String a) {
        File file = new File(a);
        if(clip != null){clip.close();}


        try {
            background = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();

            clip.open(background);
            clip.loop(100);
            clip.start();


        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
