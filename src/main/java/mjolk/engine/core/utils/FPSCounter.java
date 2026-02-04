package mjolk.engine.core.utils;

public class FPSCounter {
    private int frames = 0;
    private long lastTime = System.nanoTime();
    private int fps = 0;

    public void update() {
        frames++;
        long now = System.nanoTime();

        if (now - lastTime >= 1_000_000_000L) {
            fps = frames;
            frames = 0;
            lastTime = now;
        }
    }

    public int getFPS() {
        return fps;
    }
}

