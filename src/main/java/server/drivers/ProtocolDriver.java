package server.drivers;

import server.logic.InternetAdapterImpl;


public class ProtocolDriver implements InternetAdapterImpl.ClientDriver {

    @Override
    public InternetAdapterImpl.Frame receiveFrame() {
        return null;
    }

    @Override
    public void sendFrame(InternetAdapterImpl.Frame frame) {

    }
}
