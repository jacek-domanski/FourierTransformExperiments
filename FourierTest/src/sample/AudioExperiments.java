package sample;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class AudioExperiments {
    public static void audioTry() throws IOException, UnsupportedAudioFileException {

        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                new File("C:/Java/FourierTransformExperiments/FourierTest/src/resources/Ring09.wav"));
        AudioFormat format = inputStream.getFormat();
        System.out.println(format.isBigEndian());
        System.out.println(inputStream.available());
        System.out.println(inputStream.getFrameLength());
        for (int i = 0; i < 32; i += 4) {
            byte[] buffer = new byte[4];inputStream.read(buffer);
            System.out.println(new BigInteger(buffer).longValue());
        }
    }
    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        audioTry();
    }
}
