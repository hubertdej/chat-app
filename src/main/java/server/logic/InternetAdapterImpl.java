package server.logic;

import java.util.function.Supplier;

public class InternetAdapterImpl implements Server.InternetAdapter {
    public interface Frame {

    }
    public interface ClientDriver {
        Frame receiveFrame();
        void sendFrame(Frame frame);
    }
    private final Supplier<ClientDriver> factory;
    public InternetAdapterImpl(Supplier<ClientDriver> factory) {
        this.factory = factory;
    }
    @Override
    public void runConnection(Server.DatabaseAdapter dbAdapter) {
        connectToInternet();
        while (true) {
            ClientConnection clientConnection = new ClientConnection(accept());
            Thread clientThread = new Thread(clientConnection::handleConnection);
            clientThread.start();
        }
    }

    private void connectToInternet() {

    }

    private ClientDriver accept() {
        //change?
        return factory.get();
    }
}
