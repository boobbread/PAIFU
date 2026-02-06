package mjolk.engine.core.entity.components;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.graphics.mesh.Model;
import mjolk.engine.core.utils.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderableComponent extends Component {
    public Matrix4f modelMatrix = new Matrix4f();
    public Model model;
    public RenderableComponent(Model model) {
        this.model = model;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void update(float deltaTime, Scene scene) {
        if (!scene.renderQueue.containsKey(entity) && entity.hasComponent(TransformComponent.class)) {
            updateModelMatrix();
            scene.renderQueue.put(entity, new Pair<>(modelMatrix, model));
        }
    }

    public void updateModelMatrix() {
        TransformComponent transform = entity.getComponent(TransformComponent.class);

        modelMatrix.identity().translate(transform.pos).
                rotateX((float) Math.toRadians(transform.rotation.x)).
                rotateY((float) Math.toRadians(transform.rotation.y)).
                rotateZ((float) Math.toRadians(transform.rotation.z)).
                scale(transform.scale);

    }
}
