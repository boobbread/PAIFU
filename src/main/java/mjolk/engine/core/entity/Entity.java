package mjolk.engine.core.entity;

import mjolk.engine.core.entity.components.Component;
import mjolk.engine.graphics.mesh.Model;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

    public int id;

    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity() {
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        component.setEntity(this);
    }

    public <T extends Component> void removeComponent(T component) {
        components.remove(component.getClass(), component);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

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
