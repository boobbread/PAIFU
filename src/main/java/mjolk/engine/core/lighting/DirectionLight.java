package mjolk.engine.core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DirectionLight extends Light {

    private Vector3f colour, direction;
    private float intensity;

    public DirectionLight(Vector3f colour, Vector3f direction, float intensity) {
        this.intensity = intensity;
        this.direction = direction;
        this.colour = colour;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Matrix4f getProjectionMatrix() {
        Vector3f lightPos = new Vector3f(-direction.x, -direction.y, -direction.z).mul(10f);
        Vector3f target = new Vector3f(0,0,0);
        Vector3f up = Math.abs(direction.y) > 0.99f ?  new Vector3f(0,0,-1) : new Vector3f(0,1,0);

        Matrix4f lightView = new Matrix4f().lookAt(lightPos, target, up);

        float left = -10f, right = 10f, bottom = -10f, top = 10f, near = 0.01f, far = 50f;
        Matrix4f lightProj = new Matrix4f().ortho(left, right, bottom, top, near, far);

        return new Matrix4f(lightProj).mul(lightView);
    }
}
