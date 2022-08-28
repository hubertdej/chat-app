package com.chat.server.database;

public interface UsersLoader {
    interface UsersReader {
        void readUser(String username, String password);
    }
    void readUsers(UsersReader reader);
}
