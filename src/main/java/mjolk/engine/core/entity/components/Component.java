package mjolk.engine.core.entity.components;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;

/**
 * <h2>Abstract Class Component</h2>
 * <p> A component in this context is a behaviour modifier that can be added to an entity
 * to enable certain features, for example movement, gravity and collision</p>
 *
 * <strong>You can, and <i>should</i>, extend this class to add game-specific features</strong>

 * @author mjolkster
 */
public abstract class Component {
    protected Entity entity;

    /**
     * Used by the entity to which the component is attached in order
     * to enable entity-specific changes.
     * @param entity The entity in question.
     */
    public abstract void setEntity(Entity entity);

    /**
     * Used to provide the behaviour given by the component eg. update transformation
     * @param deltaTime The time between frames, used for ensuring updates happen consistently
     *                  on any machine.
     * @param scene The scene that holds the entity the component is attached to.
     */
    public abstract void update(float deltaTime, Scene scene);
}

