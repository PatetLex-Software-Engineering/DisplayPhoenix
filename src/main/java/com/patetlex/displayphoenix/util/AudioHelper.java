package com.patetlex.displayphoenix.util;

<<<<<<< HEAD
=======
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
import java.io.IOException;
import java.io.InputStream;

public class AudioHelper {

<<<<<<< HEAD
/*    public static void playSound(String identifier) {
=======
    public static void playSound(String identifier) {
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
        if (!(identifier.endsWith(".wav")))
            identifier += ".wav";
        playFromResource("sounds/" + identifier);
    }

    private static void playFromResource(String path) {
        InputStream musicStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        try {
            AudioStream audioStream = new AudioStream(musicStream);
            AudioPlayer.player.start(audioStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
    }*/
=======
    }
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
}
