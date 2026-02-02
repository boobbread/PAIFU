package mjolk.engine.io;

public interface ILogic {

    void init() throws Exception;

    void input(float interval);

    void update(float interval, MouseInput mouseInput);

    void render();

    void cleanup();
}
