package mjolk.engine.core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PointLight extends Light {

    private Vector3f colour, position;
    private float intensity, constant, linear, exponent;

    Vector3f[] targets = {
            new Vector3f(1,0,0),   // +X
            new Vector3f(-1,0,0),  // -X
            new Vector3f(0,1,0),   // +Y
            new Vector3f(0,-1,0),  // -Y
            new Vector3f(0,0,1),   // +Z
            new Vector3f(0,0,-1)   // -Z
    };

    Vector3f[] ups = {
            new Vector3f(0,-1,0),
            new Vector3f(0,-1,0),
            new Vector3f(0,0,1),
            new Vector3f(0,0,-1),
            new Vector3f(0,-1,0),
            new Vector3f(0,-1,0)
    };

    public PointLight(Vector3f colour, Vector3f position, float intensity, float constant, float linear, float exponent) {
        this.colour = colour;
        this.position = position;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        this(colour, position, intensity, 1, 0, 0);
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Matrix4f[] getProjectionMatrices() {
        float near = 0.1f;
        float far = 50f;

        Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(90), 1f, near, far);

        Matrix4f[] lightViewProjMatrices = new Matrix4f[6];
        for (int i = 0; i < 6; i++) {
            Matrix4f view = new Matrix4f().lookAt(position, new Vector3f(position).add(targets[i]), new Vector3f(position).add(ups[i]));
            lightViewProjMatrices[i] = new Matrix4f(proj).mul(view);
        }

        return lightViewProjMatrices;
    }
}
