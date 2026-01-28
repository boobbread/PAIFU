package mjolk.engine.core.rendering;

import mjolk.engine.core.entity.Camera;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.SpotLight;

public interface IRenderer<T> {

    void init() throws Exception;

    void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight);

    void bind(Model model);

    void unbind();

    void prepare(T t, Camera camera);

    void cleanup();

}
