package mjolk.engine.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.alDeleteBuffers;

public class SoundAsset {
    private int buffer;

    public SoundAsset(int buffer) {
        this.buffer = buffer;
    }

    public int getBuffer() {
        return buffer;
    }

    public void cleanup() {
        alDeleteBuffers(buffer);
    }
}
