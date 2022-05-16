package server.logic;

public class DatabaseAdapterImpl implements Server.DatabaseAdapter {
    private String dbUrl;
    public DatabaseAdapterImpl(String databaseUrl) {
        this.dbUrl = databaseUrl;
    }

    @Override
    public void connectDB() {

    }

    @Override
    public DBResponse handleQuery(Query query) {
        return null;
    }
}
