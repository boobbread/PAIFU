package mjolk.engine.core.entity.components;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;

public abstract class Component {
    protected Entity entity;
    public abstract void setEntity(Entity entity);
    public abstract void update(float deltaTime, Scene scene);
}

