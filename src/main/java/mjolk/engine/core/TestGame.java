package mjolk.engine.core;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.SpotLight;
import mjolk.engine.graphics.rendering.renderer.ShadowRenderer;
import mjolk.engine.io.ILogic;
import mjolk.engine.io.MouseInput;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.core.managers.WindowManager;
import mjolk.engine.graphics.rendering.renderer.GeometryRenderer;
import mjolk.engine.graphics.rendering.renderer.LightingRenderer;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.graphics.camera.Camera;
import mjolk.engine.graphics.material.Texture;
import mjolk.engine.graphics.mesh.Model;
import mjolk.engine.graphics.mesh.ObjectLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

import static mjolk.engine.core.maths.Constants.CAMERA_STEP;
import static mjolk.engine.core.maths.Constants.MOUSE_SENSITIVITY;
public class TestGame implements ILogic {

    private static final Logger LOGGER = Logger.getLogger(TestGame.class.getName());
    private final WindowManager window;
    private Scene scene;
    private ObjectLoader loader;

    private GeometryRenderer geometryRenderer;
    private LightingRenderer lightingRenderer;



    private ShadowRenderer shadowRenderer;

    Vector3f cameraInc;

    public TestGame() throws Exception {
        LOGGER.info("TestGame constructor called");
        window = Launcher.getWindow();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        LOGGER.info("TestGame init called");

        geometryRenderer = new GeometryRenderer(Launcher.getWindow().getWidth(), Launcher.getWindow().getHeight());
        shadowRenderer = new ShadowRenderer();
        lightingRenderer = new LightingRenderer();

        geometryRenderer.init();
        shadowRenderer.init();
        lightingRenderer.init();

        loader = new ObjectLoader();

        Camera camera = new Camera();
        camera.setPosition(0,0,5);

        scene = new Scene(camera);

        Model bunny_model = loader.loadOBJModel("models/bunny.obj");
        bunny_model.setTexture(new Texture(loader.loadTexture("textures/texture.jpg")), .02f);
        scene.addEntity(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1f, 0, 1), bunny_model));

        Model box_model = loader.loadOBJModel("models/box.obj");
        box_model.setTexture(new Texture(loader.loadTexture("textures/texture.jpg")), .02f);
        scene.addEntity(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1, 0, 1), box_model));

        // Point light
        Vector3f lightPosition = new Vector3f(0.1f, 1.9f, 1);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, 1f, 1f, 0.09f, 0.032f);
        scene.addLight(pointLight);

        DirectionLight directionLight = new DirectionLight(new Vector3f(1, 1, 1), new Vector3f(-0.3f, -1.0f, -0.2f), 1f);
//        scene.addLight(directionLight);

        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(1, 0, 0), new Vector3f(1.9f, 1.9f, 1), 1f, 1f, 0.09f, 0.032f), new Vector3f(-1, -1, 0), (float) Math.toRadians(30));
//        scene.addLight(spotLight);

        SpotLight spotLight2 = new SpotLight(new PointLight(new Vector3f(0, 0, 1), new Vector3f(0.1f, 1.9f, 1), 1f, 1f, 0.09f, 0.032f), new Vector3f(1, -1, 0), (float) Math.toRadians(30));
//        scene.addLight(spotLight2);

        LOGGER.info("TestGame init complete");
    }

    @Override
    public void input(float interval) {
        cameraInc.set(0,0,0);

        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        float moveSpeed = CAMERA_STEP * interval;
        scene.getCamera().movePosition(cameraInc.x * moveSpeed, cameraInc.y * moveSpeed, cameraInc.z * moveSpeed);

        if(mouseInput.isLeftButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            scene.getCamera().moveRotation(rotVec.x * MOUSE_SENSITIVITY * interval, rotVec.y * MOUSE_SENSITIVITY * interval, 0);
        }

        scene.update(interval);
    }

    @Override
    public void render() throws Exception {

        geometryRenderer.geometryPass(scene);
        shadowRenderer.render(scene);
        lightingRenderer.render(scene, geometryRenderer, shadowRenderer);

    }

    public ShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }

    public void setShadowRenderer(ShadowRenderer shadowRenderer) {
        this.shadowRenderer = shadowRenderer;
    }

    @Override
    public void cleanup() {
        loader.cleanup();

        geometryRenderer.cleanup();
        lightingRenderer.cleanup();
    }
}
