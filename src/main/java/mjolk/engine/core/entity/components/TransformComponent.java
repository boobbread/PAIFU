package mjolk.engine.core.entity.components;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;
import org.joml.Vector3f;

public class TransformComponent extends Component {
    public Vector3f pos;
    public Vector3f rotation;
    public float scale;

    public TransformComponent(Vector3f pos, Vector3f rotation, float scale) {
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
    }

    @Override
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float deltaTime, Scene scene) {
        if (entity.hasComponent(MoveableComponent.class)) {
            MoveableComponent moveableComponent = entity.getComponent(MoveableComponent.class);
            System.out.println(moveableComponent.positionVelocity);
            pos.add(moveableComponent.positionVelocity.mul(deltaTime));
            rotation.add(moveableComponent.angularVelocity.mul(deltaTime));
            scale += moveableComponent.scaleVelocity * deltaTime;
        }
    }
}
