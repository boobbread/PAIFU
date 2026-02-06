package mjolk.engine.core.entity;

import mjolk.engine.Launcher;
import mjolk.engine.audio.AudioSystem;
import mjolk.engine.core.entity.components.Component;
import mjolk.engine.core.entity.components.RenderableComponent;
import mjolk.engine.core.entity.components.TransformComponent;
import mjolk.engine.core.utils.Pair;
import mjolk.engine.graphics.camera.Camera;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.Light;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import mjolk.engine.graphics.mesh.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    // Fields
    private Camera camera;
    public Map<Entity, Pair<Matrix4f, Model>> renderQueue;
    public Map<Integer, Entity> entities;
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
        this.lights = new ArrayList<>();
        this.audioSystem = audioSystem;
        this.entities = new HashMap<>();
        this.renderQueue = new HashMap<>();
    }

    // Methods
    public void update(float delta) {
        for (Light l : lights) l.update();

        for (Entity e : entities.values()) {
            if (e.hasComponent(RenderableComponent.class)) {
                e.getComponent(RenderableComponent.class).update(delta, this);
            }

            if (e.hasComponent(TransformComponent.class)) {
                e.getComponent(TransformComponent.class).update(delta, this);
            }
        }

        audioSystem.updateListener(camera);
        audioSystem.update();
    }

    // Getters and setters
    public Camera getCamera() { return camera; }
    public void setCamera(Camera camera) { this.camera = camera; }
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
            } else {
                Vector4f rect = Launcher.getGame().getShadowRenderer().getAtlas().allocateTile();
                l.setShadowRect(rect);
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
