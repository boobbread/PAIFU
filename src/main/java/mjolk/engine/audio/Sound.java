package mjolk.engine.audio;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

public class Sound {

    int source;

    public Sound() {
        // just a dummy constructor
    }

    public void setPosition(Vector3f pos) {
        alSource3f(source, AL_POSITION, pos.x, pos.y, pos.z);
    }

    public void play() {
        if (!isPlaying()) alSourcePlay(source);
    }

    public void stop() {
        alSourceStop(source);
    }

    public boolean isPlaying() {
        return alGetSourcei(source, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public boolean isFinished() {
        return alGetSourcei(source, AL_SOURCE_STATE) == AL_STOPPED;
    }

    public void setGain(float gain) {
        alSourcef(source, AL_GAIN, gain);
    }

    void attachSource(int sourceId) {
        this.source = sourceId;
    }
}

