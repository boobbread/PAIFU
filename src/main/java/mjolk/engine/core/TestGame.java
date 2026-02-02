package mjolk.engine.core;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
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

import static mjolk.engine.core.maths.Constants.CAMERA_STEP;
import static mjolk.engine.core.maths.Constants.MOUSE_SENSITIVITY;
public class TestGame implements ILogic {
    private final WindowManager window;
    private Scene scene;
    private ObjectLoader loader;

    private GeometryRenderer geometryRenderer;
    private LightingRenderer lightingRenderer;

    Vector3f cameraInc;

    public TestGame() throws Exception {
        System.out.println("TestGame constructor called");
        window = Launcher.getWindow();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        System.out.println("TestGame init called");

        geometryRenderer = new GeometryRenderer(Launcher.getWindow().getWidth(), Launcher.getWindow().getHeight());
        lightingRenderer = new LightingRenderer();

        geometryRenderer.init();
        lightingRenderer.init();

        loader = new ObjectLoader();

        Camera camera = new Camera();
        camera.setPosition(0,0,5);

        scene = new Scene(camera);

        Model bunny_model = loader.loadOBJModel("models/bunny.obj");
        bunny_model.setTexture(new Texture(loader.loadTexture("textures/texture.jpg")), .02f);
        scene.addEntity(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1, 0, 1), bunny_model));

        Model box_model = loader.loadOBJModel("models/box.obj");
        box_model.setTexture(new Texture(loader.loadTexture("textures/texture.jpg")), .02f);
        scene.addEntity(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1, 0, 1), box_model));

        // Point light
        Vector3f lightPosition = new Vector3f(-1f, 1.9f, 1);
        Vector3f lightColour = new Vector3f(1, 0, 0);
        PointLight pointLight = new PointLight(lightColour, lightPosition, 1f, 1f, 0.09f, 0.032f);
        scene.addLight(pointLight);

        System.out.println("TestGame init complete");
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
    public void render() {

        geometryRenderer.geometryPass(scene);
        lightingRenderer.render(scene, geometryRenderer);

    }

    @Override
    public void cleanup() {
        loader.cleanup();

        geometryRenderer.cleanup();
        lightingRenderer.cleanup();
    }
}
