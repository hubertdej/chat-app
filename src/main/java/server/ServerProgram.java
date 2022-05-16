package server;

import server.drivers.ProtocolDriver;
import server.logic.DatabaseAdapterImpl;
import server.logic.InternetAdapterImpl;
import server.logic.Server;

class ServerProgram {
    static final String defaultDBUrl = "server.db";
    public static void main(String[] args) {
        Server.runServer(
                new DatabaseAdapterImpl(args.length <= 1 ? defaultDBUrl : args[1]),
                new InternetAdapterImpl(ProtocolDriver::new)
        );
    }
}
