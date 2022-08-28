package com.chat.server.domain;

public interface UsersEngine {
    interface UsersReader {
        void readUser(String username, String password);
    }
    void addUser(String username, String password);
    void readUsers(UsersEngine.UsersReader reader);
}
