package mjolk.engine.core.managers;

import mjolk.engine.Launcher;
import mjolk.engine.graphics.rendering.renderer.ShadowRenderer;
import mjolk.engine.io.ILogic;
import mjolk.engine.io.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.logging.Logger;

public class EngineManager {

    private static final Logger LOGGER = Logger.getLogger(EngineManager.class.getName());

    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 120;

    private static int fps;
    private static float frameTime = 1.0f / FRAMERATE;

    private boolean isRunning;

    private WindowManager window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;
    private MouseInput mouseInput;

    private void init() throws Exception {
        LOGGER.info("EngineManager init called");
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        window = Launcher.getWindow();

        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();

        gameLogic.init();
        mouseInput.init();

        LOGGER.info("EngineManager init complete");
    }

    public void start() throws Exception {
        init();
        if (isRunning) return;
        run();
    }

    public void run() {
        this.isRunning = true;
        long lastTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            float delta = (now - lastTime) / (float) NANOSECOND;
            lastTime = now;

            GLFW.glfwPollEvents();
            input(delta);

            update(delta);
            render();

            GLFW.glfwSwapBuffers(window.getWindow());

            if (window.windowShouldClose()) {
                stop();
            }
        }


        cleanup();
    }

    public void stop() {
        if(!isRunning) return;
        isRunning = false;
    }

    public void input(float interval) {
        gameLogic.input(interval);
        mouseInput.input();
    }

    public void render() {
        gameLogic.render();
        window.update();
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup() {
        window.cleanUp();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
