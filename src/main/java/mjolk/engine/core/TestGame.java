package mjolk.engine.core;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
import mjolk.engine.core.io.ILogic;
import mjolk.engine.core.io.MouseInput;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.SpotLight;
import mjolk.engine.core.lighting.deferred.GeometryRenderer;
import mjolk.engine.core.lighting.deferred.LightingRenderer;
import mjolk.engine.core.rendering.RenderManager;
import mjolk.engine.core.managers.WindowManager;
import mjolk.engine.core.rendering.Scene;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static mjolk.engine.core.utils.Constants.CAMERA_STEP;
import static mjolk.engine.core.utils.Constants.MOUSE_SENSITIVITY;
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

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final WindowManager window;
    private Scene scene;
    private ObjectLoader loader;

    private GeometryRenderer geometryRenderer;
    private LightingRenderer lightingRenderer;

    Vector3f cameraInc;

    public TestGame() throws Exception {
        System.out.println("TestGame constructor called");
        renderer = new RenderManager();
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
        renderer.init();
        loader = new ObjectLoader();

        Model model = loader.loadOBJModel("models/church_2.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/texture.jpg")), .02f);

        Camera camera = new Camera();
        camera.setPosition(0,0,5);

        scene = new Scene(camera);

        scene.addEntity(new Entity(1, new Vector3f(0, 180, 0), new Vector3f(1, 0, 1), model));

        // Directional light
        Vector3f lightPosition = new Vector3f(0, -3, 0f);
        Vector3f lightColour = new Vector3f(0, 0, 1);
        DirectionLight directionLight = new DirectionLight(lightColour, lightPosition, 1f);
        scene.addLight(directionLight);

        // Point light
        lightPosition = new Vector3f(-1f, 1.9f, 1);
        lightColour = new Vector3f(1, 0, 0);
        PointLight pointLight = new PointLight(lightColour, lightPosition, 1f, 1f, 0.09f, 0.032f);
        scene.addLight(pointLight);

        // Spotlight
//        Vector3f coneDirection = new Vector3f(0f, -1, 0);
//        float cutoff = (float) Math.cos(Math.toRadians(30));
//        SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(1,1.8f,1),
//                1f, 1f, 0.09f, 0.032f), coneDirection, cutoff);
//        scene.addLight(spotLight);

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

        for (Entity e : scene.getEntities()) {
            renderer.processEntities(e);
        }

        scene.update(interval);
    }

    @Override
    public void render() {
//        renderer.render(
//                scene.getCamera(),
//                scene.getDirectionalLight(),
//                scene.getPointLights().toArray(new PointLight[0]),
//                scene.getSpotLights().toArray(new SpotLight[0])
//        );

        geometryRenderer.geometryPass(scene);
        lightingRenderer.render(scene, geometryRenderer);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
