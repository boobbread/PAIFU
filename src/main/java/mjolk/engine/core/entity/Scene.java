package mjolk.engine.core.entity;

import mjolk.engine.Launcher;
import mjolk.engine.graphics.camera.Camera;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.Light;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    // Fields
    private Camera camera;
    private List<Entity> entities;
    private List<Light> lights;

    Vector3f centre;
    float radius;

    // Constructors
    public Scene(Camera camera) {
        this.camera = camera;
        this.entities = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    // Methods
    public void update(float delta) {
        for (Entity e : entities) e.update();
        for (Light l : lights) l.update();
    }

    // Getters and setters
    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public List<Entity> getEntities() { return entities; }
    public void addEntity(Entity e) { entities.add(e); }
    public void removeEntity(Entity e) { entities.remove(e); }

    public List<Light> getLights() { return lights; }
    public void addLight(Light l) {
        lights.add(l);
        if (l.castsShadows()) {
            Vector4f rect = Launcher.getGame().getShadowRenderer().getAtlas().allocateTile();
            l.setShadowRect(rect);
        }
    }
    public void removeLight(Light l) { lights.remove(l); }

    public List<DirectionLight> getDirectionalLights() {
        List<DirectionLight> dls = new ArrayList<>();
        for (Light l : lights) {
            if (l instanceof DirectionLight) {
                dls.add((DirectionLight) l);
            }
        }
        return dls;
    }

    public List<PointLight> getPointLights() {
        List<PointLight> pts = new ArrayList<>();
        for (Light l : lights) {
            if (l instanceof PointLight) {
                pts.add((PointLight) l);
            }
        }
        return pts;
    }

    public List<SpotLight> getSpotLights() {
        List<SpotLight> spots = new ArrayList<>();
        for (Light l : lights) {
            if (l instanceof SpotLight) {
                spots.add((SpotLight) l);
            }
        }
        return spots;
    }
}
