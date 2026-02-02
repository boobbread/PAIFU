package mjolk.engine.graphics.lighting;

import mjolk.engine.graphics.lighting.shadow.ShadowMap;
import mjolk.engine.graphics.material.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Light {

    protected boolean castsShadows = true;

    private Vector3f colour;
    private float intensity;

    public Light(Vector3f colour, float intensity) {
        this.colour = colour;
        this.intensity = intensity;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void update() {
        // Space for future
    }

    public boolean castsShadows() {
        return castsShadows;
    }

    public abstract ShadowMap getShadowMap();

    public abstract Matrix4f getViewProjectionMatrix();
}
