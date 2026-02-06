package mjolk.engine.core.entity.components;

import org.joml.Vector3f;

public class TransformComponent {
    public final Vector3f pos = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    private float scale = 1.0f;
}
