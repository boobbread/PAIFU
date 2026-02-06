package mjolk.engine.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class AudioLoader {

    public static int loadWAV(String fileName) throws Exception{
        AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
        AudioFormat audioFormat = ais.getFormat();

        if (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED){
            AudioFormat pcmFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    audioFormat.getSampleRate(),
                    16,
                    audioFormat.getChannels(),
                    audioFormat.getChannels() * 2,
                    audioFormat.getSampleRate(),
                    false
            );

            ais = AudioSystem.getAudioInputStream(pcmFormat, ais);
            audioFormat = pcmFormat;
        }

        byte[] audioBytes = ais.readAllBytes();
        ais.close();

        ByteBuffer buffer = memAlloc(audioBytes.length);
        buffer.put(audioBytes).flip();

        int alFormat = getOpenALFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
        int sampleRate = (int) audioFormat.getSampleRate();

        System.out.println("Channels: " + audioFormat.getChannels());

        int bufferId = alGenBuffers();
        alBufferData(bufferId, alFormat, buffer, sampleRate);

        memFree(buffer);
        return bufferId;
    }

    private static int getOpenALFormat(int channels, int bits) {
        if (channels == 1) {
            return bits == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16;
        } else if (channels == 2) {
            return bits == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16;
        }
        throw new IllegalArgumentException("Unsupported WAV format");
    }
}
