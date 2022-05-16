package server.logic;

import java.util.List;

public class SendRequest implements ClientConnection.Request {
    private InternetAdapterImpl.ClientDriver clientDriver;

    public SendRequest(InternetAdapterImpl.ClientDriver clientDriver) {
        this.clientDriver = clientDriver;
    }

    private Query createQuery() {
        return null;
    }
    private void ackClient() {
        InternetAdapterImpl.Frame ack = null;
        clientDriver.sendFrame(ack);
    }
    private void forwardMessageToSubscribers(List<DBResponse.Subscriber> subscribers) {

    }
    @Override
    public void execute(Server.DatabaseAdapter adapter) {
        var query = createQuery();
        var response = adapter.handleQuery(query);
        ackClient();
        forwardMessageToSubscribers(response.getSubscribers());
    }
}
