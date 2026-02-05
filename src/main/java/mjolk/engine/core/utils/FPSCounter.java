package mjolk.engine.core.utils;

public class FPSCounter {
    private int frames = 0;
    private long lastTime = System.nanoTime();
    private int fps = 0;

    public void update() {
        frames++;
        long now = System.nanoTime();

        // Calculate frame time in nanoseconds
        long frameTimeNano = now - lastTime;

        // Convert to milliseconds (1 ms = 1,000,000 ns)
        fps = (int) (frameTimeNano / 1_000_000.0f); // as float milliseconds

        // Alternative: store as nanoseconds if you need more precision
        // frameTimeNanos = frameTimeNano;

        lastTime = now;
    }

    public int getFPS() {
        return fps;
    }
}

