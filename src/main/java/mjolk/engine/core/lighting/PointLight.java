package mjolk.engine.core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PointLight extends Light {

    private Vector3f position;
    private float constant, linear, exponent;

    public PointLight(Vector3f colour, Vector3f position, float intensity, float constant, float linear, float exponent) {
        super(colour, intensity);
        this.position = position;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        this(colour, position, intensity, 1, 0, 0);
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

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Matrix4f[] getViewProjectionMatrices() {
        Vector3f pos = new Vector3f(getPosition());
        float near = 0.1f;
        float far = 50f;
        float fov = (float) Math.toRadians(90);
        float aspect = 1f;

        Matrix4f proj = new Matrix4f().perspective(fov, aspect, near, far);

        Vector3f[] targets = new Vector3f[] {
                new Vector3f(1, 0, 0),   // +X
                new Vector3f(-1, 0, 0),  // -X
                new Vector3f(0, 1, 0),   // +Y
                new Vector3f(0, -1, 0),  // -Y
                new Vector3f(0, 0, 1),   // +Z
                new Vector3f(0, 0, -1)   // -Z
        };

        Vector3f[] ups = new Vector3f[] {
                new Vector3f(0, -1, 0), // +X
                new Vector3f(0, -1, 0), // -X
                new Vector3f(0, 0, 1),  // +Y
                new Vector3f(0, 0, -1), // -Y
                new Vector3f(0, -1, 0), // +Z
                new Vector3f(0, -1, 0)  // -Z
        };

        Matrix4f[] matrices = new Matrix4f[6];

        for (int i = 0; i < 6; i++) {
            Matrix4f view = new Matrix4f().lookAt(pos, new Vector3f(pos).add(targets[i]), ups[i]);
            matrices[i] = new Matrix4f(proj).mul(view);
        }

        return matrices;
    }
}
