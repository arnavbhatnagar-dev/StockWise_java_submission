package com.stockwise.dao;

import com.stockwise.model.Category;
import com.stockwise.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() throws SQLException {

        List<Category> categories = new ArrayList<>();

        String sql = """
                SELECT category_id, category_name, description
                FROM categories
                ORDER BY category_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                categories.add(
                        new Category(
                                result.getInt("category_id"),
                                result.getString("category_name"),
                                result.getString("description")
                        )
                );
            }
        }

        return categories;
    }

    public boolean categoryExists(int categoryId)
            throws SQLException {

        String sql =
                "SELECT 1 FROM categories WHERE category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, categoryId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}