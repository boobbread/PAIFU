package mjolk.engine.core.entity;

import org.joml.Vector3f;

public class Entity {

    private Model model;
    private Vector3f pos, rotation;
    private float scale;

    /**
     * @param scale The scale the model is rendered in (optional - defaults to one)
     * @param rotation The rotation of the model (optional - defaults to none)
     * @param pos The world-relative position of the model (optional - defaults to (0, 0, 0))
     * @param model The model rendered for the entity (non-optional)
     */
    public Entity(float scale, Vector3f rotation, Vector3f pos, Model model) {
        this.scale = scale;
        this.rotation = rotation;
        this.pos = pos;
        this.model = model;
    }

    /**
     * Default scale, position and rotation
     * @param model The model rendered for the entity
     * @see #Entity(float, Vector3f, Vector3f, Model)
     */
    public Entity(Model model) {
        this(1, new Vector3f(0,0,0), new Vector3f(0,0,0), model);
    }

    public void update() {
        // Space for future
    }

    public void incPos(float x, float y, float z) {
        this.pos.x += x;
        this.pos.y += y;
        this.pos.z += z;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }


    public void incRotation(float x, float y, float z) {
        this.rotation.x += x;
        this.rotation.y += y;
        this.rotation.z += z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
