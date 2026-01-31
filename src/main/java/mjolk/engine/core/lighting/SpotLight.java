package mjolk.engine.core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SpotLight extends Light{

    private PointLight pointLight;

    private Vector3f coneDirection;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoff) {
        this.cutoff = cutoff;
        this.coneDirection = coneDirection;
        this.pointLight = pointLight;
    }

    public SpotLight(SpotLight spotLight) {
        this.pointLight = spotLight.getPointLight();
        this.coneDirection = spotLight.getConeDirection();
        this.cutoff = spotLight.getCutoff();
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public Matrix4f getProjectionMatrix() {
        float fovy = 2.0f * (float) Math.acos(cutoff);

        Vector3f up = Math.abs(coneDirection.y) > 0.99f ? new Vector3f(0, 0, -1) : new Vector3f(0, 1, 0);

        Matrix4f lightViewMatrix = new Matrix4f().lookAt(pointLight.getPosition(), new Vector3f(pointLight.getPosition()).add(new Vector3f(coneDirection)), up);
        Matrix4f lightProjectionMatrix = new Matrix4f().perspective(fovy, 1f, 0.01f, 100.0f);

        return new Matrix4f(lightProjectionMatrix).mul(lightViewMatrix);
    }
}
