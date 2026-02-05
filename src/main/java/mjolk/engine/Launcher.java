package mjolk.engine;

import mjolk.engine.core.managers.EngineManager;
import mjolk.engine.core.TestGame;
import mjolk.engine.core.managers.WindowManager;
import mjolk.engine.core.maths.Constants;

import java.util.logging.Logger;

public class Launcher {
    private static final Logger LOGGER = Logger.getLogger(Launcher.class.getName());

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) throws Exception {

        window = new WindowManager(Constants.TITLE, 1920, 1080, false);

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
