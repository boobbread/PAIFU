package mjolk.engine.graphics.camera;

import org.joml.Vector3f;

public class Camera {

    private Vector3f position, rotation;
    private float zoom = 60f;
    public enum Perspective {
        NORMAL,
        ORTHOGRAPHIC
    }

    private Perspective perspective;

    /**
     * Instances a new camera
     */
    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        perspective = Perspective.NORMAL;
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.perspective = Perspective.NORMAL;
    }

    public Camera(Vector3f position, Vector3f rotation, Perspective perspective) {
        this.position = position;
        this.rotation = rotation;
        this.perspective = perspective;
    }

    public Camera(Perspective perspective) {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        this.perspective = perspective;
    }

    public void movePosition(float x, float y, float z) {
        if (z != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }

        if (x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }

        position.y += y;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void moveRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public Perspective getPerspective() {
        return perspective;
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }
}
