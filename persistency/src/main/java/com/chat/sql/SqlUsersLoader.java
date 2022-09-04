package com.chat.sql;

import com.chat.database.UsersLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlUsersLoader implements UsersLoader {
    private final String url;

    public SqlUsersLoader(String path) {
        this.url = "jdbc:sqlite:" + path;
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
