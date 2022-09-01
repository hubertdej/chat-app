package com.chat.server.sql;

import com.chat.server.database.common.UsersEngine;
import com.chat.server.database.common.UsersLoader;

import java.sql.*;

public class SqlUsersManager implements UsersEngine, UsersLoader {
    private final String url;

    SqlUsersManager(String path) {
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

    @Override
    public void readUsers(UsersLoader.UsersReader reader) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement usersSelect = connection.prepareStatement("select * from users");
            var users = usersSelect.executeQuery();
            while (users.next()) {
                String username = users.getString("username");
                String password = users.getString("password");
                reader.readUser(username, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
