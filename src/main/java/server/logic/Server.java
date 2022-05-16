package server.logic;

public class Server {
    public interface DatabaseAdapter {
//        void createDB();
        void connectDB();
//        void disconnectDB();
        DBResponse handleQuery(Query query);
    }
    public interface InternetAdapter {
        void runConnection(DatabaseAdapter dbAdapter); //ok?
    }
    public static void runServer(DatabaseAdapter dbAdapter, InternetAdapter iAdapter) {
        dbAdapter.connectDB();
        iAdapter.runConnection(dbAdapter);
    }
}
