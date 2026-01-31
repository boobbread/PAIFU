package mjolk.engine.core;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
import mjolk.engine.core.io.ILogic;
import mjolk.engine.core.io.MouseInput;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.SpotLight;
import mjolk.engine.core.rendering.RenderManager;
import mjolk.engine.core.managers.WindowManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static mjolk.engine.core.utils.Constants.CAMERA_STEP;
import static mjolk.engine.core.utils.Constants.MOUSE_SENSITIVITY;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private List<Entity> entities;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private DirectionLight directionLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;

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

        Model model = loader.loadOBJModel("models/church_2.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/colour.png")), .02f);

        entities = new ArrayList<>();
//        Random rnd = new Random();
//        for (int i = 0; i < 20; i++) {
//            float x = (rnd.nextFloat() * 5) - 2.5f;
//            float y = (rnd.nextFloat() * 5) - 2.5f;
//            float z = (rnd.nextFloat() * 5) - 2.5f;
//
//            entities.add(new Entity(1, new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), new Vector3f(x, y, z), model));
//        }

        entities.add(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1, 0, 1), model));
        camera.setPosition(0,0,5);

        // Point light
        Vector3f lightPosition = new Vector3f(1, 2, 1);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, 0f);

        // Spotlight
        Vector3f coneDirection = new Vector3f(0f, -1, 0);
        float cutoff = (float) Math.cos(Math.toRadians(30));
        SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(1,1.8f,1),
                1f, 0, 0, 0.5f), coneDirection, cutoff);

        // Directional light
        lightPosition = new Vector3f(0, 3, 0f);
        lightColour = new Vector3f(1, 1, 1);
        directionLight = new DirectionLight(lightColour, lightPosition, 0f);
        directionLight.setDirection(new Vector3f(0, 0, 0));

        pointLights = new PointLight[]{pointLight};
        spotLights = new SpotLight[]{spotLight};

        System.out.println("TestGame INIT");
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

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            pointLights[0].getPosition().x -= 0.05f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            pointLights[0].getPosition().x += 0.05f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            pointLights[0].getPosition().y += 0.05f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            pointLights[0].getPosition().y -= 0.05f;
        }

        float lightPos = spotLights[0].getPointLight().getPosition().z;
        if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
            spotLights[0].getPointLight().getPosition().y += 0.5f  * interval;
            System.out.println(spotLights[0].getPointLight().getPosition().y);
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
            spotLights[0].getPointLight().getPosition().y -= 0.5f  * interval;
            System.out.println(spotLights[0].getPointLight().getPosition().y);
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

        for (Entity e : entities) {
            renderer.processEntities(e);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, directionLight, pointLights, spotLights);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
