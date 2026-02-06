package mjolk.engine.io;

public interface ILogic {

    void init() throws Exception;

    void input(float interval) throws Exception;

    void update(float interval, MouseInput mouseInput);

    void render() throws Exception;

    void cleanup();
}
