package mjolk.engine.core.entity.components;

import mjolk.engine.graphics.mesh.Model;

public class RenderComponent {
    public Model model;

    public RenderComponent(Model model) {
        this.model = model;
    }
}
