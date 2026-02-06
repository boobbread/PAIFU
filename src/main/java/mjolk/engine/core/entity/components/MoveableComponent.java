package mjolk.engine.core.entity.components;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;
import org.joml.Vector3f;

public class MoveableComponent extends Component {

    public Vector3f positionVelocity = new Vector3f(0, 0, 0);
    public Vector3f angularVelocity = new Vector3f(0, 0, 0);
    public float scaleVelocity = 0f;

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float deltaTime, Scene scene) {
    }
}
