package mjolk.engine.core;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
import mjolk.engine.core.io.ILogic;
import mjolk.engine.core.io.MouseInput;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.managers.RenderManager;
import mjolk.engine.core.managers.WindowManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static mjolk.engine.core.utils.Constants.CAMERA_STEP;
import static mjolk.engine.core.utils.Constants.MOUSE_SENSITIVITY;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private DirectionLight directionLight;
    private PointLight pointLight;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90f;
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadOBJModel("models/bunny.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/colour.png")), .02f);

        entity = new Entity(1, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), model);
        camera.setPosition(0,0,5);

        float lightIntensity = 2.0f;

        Vector3f lightPosition = new Vector3f(5, 0, 3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity);

        lightPosition = new Vector3f(0, 0, 10f);
        lightColour = new Vector3f(1, 1, 1);
        directionLight = new DirectionLight(lightColour, lightPosition, lightIntensity);
    }

    @Override
    public void input() {
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

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            pointLight.getPosition().x -= 0.05f;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            pointLight.getPosition().x += 0.05f;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            pointLight.getPosition().y += 0.05f;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            pointLight.getPosition().y -= 0.05f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        float moveSpeed = CAMERA_STEP * interval;
        camera.movePosition(cameraInc.x * moveSpeed, cameraInc.y * moveSpeed, cameraInc.z * moveSpeed);

        if(mouseInput.isLeftButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY * interval, rotVec.y * MOUSE_SENSITIVITY * interval, 0);
        }

        // entity.incRotation(0.0f, 0.25f, 0.0f);

        lightAngle += 0.15f;

        directionLight.setIntensity(1);
        directionLight.getColour().x = 1;
        directionLight.getColour().y = 1;
        directionLight.getColour().z = 1;

        double angRad = Math.toRadians(lightAngle);
        directionLight.getDirection().z = (float) Math.sin(angRad);
        directionLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render() {
        renderer.render(entity, camera, directionLight, pointLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
