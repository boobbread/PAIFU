package mjolk.engine.audio;

import mjolk.engine.graphics.camera.Camera;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

public class AudioSystem {


    private final List<Sound> activeSounds = new ArrayList<>();
    private final Deque<Integer> freeSources = new ArrayDeque<>();

    private static final int MAX_SOURCES = 128;

    public AudioSystem() {
        for (int i = 0; i < MAX_SOURCES; i++) {
            freeSources.add(alGenSources());
        }
    }

    public Sound play(SoundAsset asset,
                      Vector3f position,
                      float gain,
                      float ref,
                      float max,
                      float rollOff) {

        if (freeSources.isEmpty()) {
            return null;
        }

        int source = freeSources.pop();

        Sound sound = new Sound();
        sound.attachSource(source);

        alSourcei(source, AL_BUFFER, asset.getBuffer());
        alSourcef(source, AL_GAIN, gain);
        alSourcef(source, AL_REFERENCE_DISTANCE, ref);
        alSourcef(source, AL_MAX_DISTANCE, max);
        alSourcef(source, AL_ROLLOFF_FACTOR, rollOff);
        alSource3f(source, AL_POSITION, position.x, position.y, position.z);

        alSourcePlay(source);

        activeSounds.add(sound);
        return sound;
    }

    public void updateListener(Camera cam) {
        alListener3f(AL_POSITION,
                cam.getPosition().x,
                cam.getPosition().y,
                cam.getPosition().z
        );

        Vector3f f = cam.getForward();
        Vector3f u = cam.getUp();

        alListenerfv(AL_ORIENTATION, new float[]{
                f.x, f.y, f.z,
                u.x, u.y, u.z
        });
    }

    public void update() {
        for (int i = activeSounds.size() - 1; i >= 0; i--) {
            Sound s = activeSounds.get(i);

            if (s.isFinished()) {
                alSourceStop(s.source);
                alSourcei(s.source, AL_BUFFER, 0);

                freeSources.push(s.source);
                activeSounds.remove(i);
            }
        }
    }


    public void cleanup() {
        for (Sound s : activeSounds) {
            alDeleteSources(s.source);
        }
        for (int src : freeSources) {
            alDeleteSources(src);
        }
    }

}

