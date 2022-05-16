package server.logic;


public class ClientConnection {
    public interface Request {
        void execute(Server.DatabaseAdapter adapter);
    }
    private final InternetAdapterImpl.ClientDriver driver;
    ClientConnection(InternetAdapterImpl.ClientDriver driver) {
        this.driver = driver;
    }
    private Request decode(InternetAdapterImpl.Frame frame) {
        return null;
    }
    void handleConnection() {
        while (true) {
            var frame = driver.receiveFrame();
            var request = decode(frame);//validate first?

        }
    }
}
