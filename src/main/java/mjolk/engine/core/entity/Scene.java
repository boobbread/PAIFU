package mjolk.engine.core.entity;

import mjolk.engine.Launcher;
import mjolk.engine.audio.AudioSystem;
import mjolk.engine.audio.Sound;
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
    private AudioSystem audioSystem;

    private int pointLights;
    private int spotLights;
    private int directionLights;

    Vector3f centre;
    float radius;

    // Constructors
    public Scene(Camera camera, AudioSystem audioSystem) {
        this.camera = camera;
        this.entities = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.audioSystem = audioSystem;
    }

    // Methods
    public void update(float delta) {
        for (Entity e : entities) e.update();
        for (Light l : lights) l.update();

        audioSystem.updateListener(camera);
        audioSystem.update();
    }

    // Getters and setters
    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }

    public List<Entity> getEntities() { return entities; }
    public void addEntity(Entity e) { entities.add(e); }
    public void removeEntity(Entity e) { entities.remove(e); }

    public List<Light> getLights() { return lights; }
    public void addLight(Light l) {
        if (l instanceof PointLight && !(l instanceof SpotLight)) {
            if (pointLights < 64) {
                pointLights++;
            } else {
                return;
            }
        }

        if (l instanceof SpotLight) {
            if (spotLights < 64) {
                spotLights++;
            } else {
                return;
            }
        }

        if (l instanceof PointLight && !(l instanceof SpotLight)) {
            if (directionLights < 64) {
                directionLights++;
            } else {
                return;
            }
        }
        lights.add(l);
        if (l.castsShadows()) {
            if (l instanceof PointLight && !(l instanceof SpotLight)) {
                Vector4f frontRect = Launcher.getGame().getShadowRenderer().getAtlas().allocateTile();
                Vector4f backRect = Launcher.getGame().getShadowRenderer().getAtlas().allocateTile();
                ((PointLight) l).setFrontRect(frontRect);
                ((PointLight) l).setBackRect(backRect);

                System.out.println(frontRect);
                System.out.println(backRect);
            } else {
                System.out.println("rect for non-point lights called");
                Vector4f rect = Launcher.getGame().getShadowRenderer().getAtlas().allocateTile();
                l.setShadowRect(rect);
                System.out.println(rect);
            }
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
            if (l instanceof PointLight && !(l instanceof SpotLight)) {
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
