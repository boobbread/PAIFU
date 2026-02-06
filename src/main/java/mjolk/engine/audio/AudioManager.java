package mjolk.engine.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.AL10.AL_INVERSE_DISTANCE_CLAMPED;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;

public class AudioManager {

    private Map<String, SoundAsset> assets;
    private AudioSystem system;

    public AudioManager() {
        assets = new HashMap<>();

        init();

        system = new AudioSystem();
    }

    public void init() {
        long device = alcOpenDevice((ByteBuffer) null);
        if (device == 0) {
            throw new IllegalStateException("Failed to open OpenAL device");
        }

        ALCCapabilities alcCaps = ALC.createCapabilities(device);

        long context = alcCreateContext(device, (IntBuffer) null);
        if (context == 0) {
            throw new IllegalStateException("Failed to create OpenAL context");
        }

        alcMakeContextCurrent(context);

        AL.createCapabilities(alcCaps);

        alDistanceModel(AL_INVERSE_DISTANCE_CLAMPED);

    }

    public SoundAsset load(String filename) throws Exception {
        if (assets.containsKey(filename)) {
            return assets.get(filename);
        }

        int buffer = AudioLoader.loadWAV(filename);
        SoundAsset asset = new SoundAsset(buffer);
        assets.put(filename, asset);
        return asset;
    }

    public AudioSystem getSystem() {
        return system;
    }

    public void cleanup() {
        system.cleanup();

        for (SoundAsset asset : assets.values()) {
            asset.cleanup();
        }
        assets.clear();

        alcDestroyContext(alcGetCurrentContext());
        alcCloseDevice(alcGetContextsDevice(alcGetCurrentContext()));
    }

}
