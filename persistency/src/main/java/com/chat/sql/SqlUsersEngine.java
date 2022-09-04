package com.chat.sql;

import com.chat.database.UsersEngine;

import java.sql.*;

public class SqlUsersEngine implements UsersEngine {
    private final String url;

    public SqlUsersEngine(String path) {
        this.url = "jdbc:sqlite:" + path;
    }
    @Override
    public void addUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement userInsert = connection.prepareStatement("insert into users" +
                    "(username, password) " +
                    "values (?, ?)");
            userInsert.setString(1, username);
            userInsert.setString(2, password);
            userInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
