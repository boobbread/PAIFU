package mjolk.engine.core.entity;

import mjolk.engine.core.entity.components.Component;
import mjolk.engine.graphics.mesh.Model;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>An entity is nothing more than a holder of components that can be referenced to </p>
 */
public class Entity {

    public int id;

    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * A dummy constructor used to instantiate different entities. Other than that
     * it does <b>literally</b> nothing at all.
     */
    public Entity() {
    }

    /**
     * Attaches a component to an entity. For example:
     * <p><code> entity.addComponent(new ExampleComponent())</code></p>
     * @param component A new instance of the desired component.
     * @param <T> <b>Must extend the {@link mjolk.engine.core.entity.components Component} class.</b>
     */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        component.setEntity(this);
    }

    /**
     * Retrieves the component of that type in order to use that specific components methods, for example:
     * <p><code>entity.getComponent(ExampleComponent.class)</code></p>
     * @param type The type of the desired component.
     * @param <T> <b>Must extend the {@link mjolk.engine.core.entity.components Component} class.</b>
     * @return The component of that type present in the entity.
     */
    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

    /**
     * Test if the entity contains the component of a given type.
     * @param type The type of the desired component.
     * @return <code>True</code> if the entity has the component, <code>False</code> if not.
     * @param <T> <b>Must extend the {@link mjolk.engine.core.entity.components Component} class.</b>
     */
    public <T extends Component> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    public List<String> getComponentNames() {
        return components.keySet()
                .stream()
                .map(Class::getSimpleName)
                .toList();
    }

}
