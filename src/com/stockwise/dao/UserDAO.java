package com.stockwise.dao;

import com.stockwise.enums.Role;
import com.stockwise.model.User;
import com.stockwise.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        String sql = """
                SELECT user_id, username, full_name, role
                FROM users
                WHERE username = ? AND password = ? AND active = TRUE
                """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                return new User(result.getInt("user_id"), result.getString("username"),
                        result.getString("full_name"), Role.valueOf(result.getString("role")));
            }
        }
    }
}
