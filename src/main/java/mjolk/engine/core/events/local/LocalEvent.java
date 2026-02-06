package mjolk.engine.core.events.local;

import mjolk.engine.core.entity.Entity;

public abstract class LocalEvent {

    protected Entity entity;
    public abstract void setEntity(Entity entity);

}
