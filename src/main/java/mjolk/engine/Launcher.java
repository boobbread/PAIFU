package mjolk.engine;

import mjolk.engine.core.managers.EngineManager;
import mjolk.engine.core.TestGame;
import mjolk.engine.core.managers.WindowManager;
import mjolk.engine.core.utils.Constants;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) throws Exception {
        System.out.println("Launcher called");

        window = new WindowManager(Constants.TITLE, 1600, 900, false);

        game = new TestGame();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static void setWindow(WindowManager window) {
        Launcher.window = window;
    }

    public static TestGame getGame() {
        return game;
    }

}
