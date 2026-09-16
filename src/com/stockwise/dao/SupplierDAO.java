package com.stockwise.dao;

import com.stockwise.model.Supplier;
import com.stockwise.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public void addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (supplier_name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getSupplierName()); statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail()); statement.setString(4, supplier.getAddress()); statement.executeUpdate();
        }
    }
    public List<Supplier> getAllSuppliers() throws SQLException {
        String sql = "SELECT supplier_id, supplier_name, phone, email, address FROM suppliers WHERE active = TRUE ORDER BY supplier_name";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            while (result.next()) suppliers.add(new Supplier(result.getInt("supplier_id"), result.getString("supplier_name"),
                    result.getString("phone"), result.getString("email"), result.getString("address")));
        }
        return suppliers;
    }
    public boolean supplierExists(int supplierId) throws SQLException {
        String sql = "SELECT 1 FROM suppliers WHERE supplier_id = ? AND active = TRUE";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, supplierId); try (ResultSet result = statement.executeQuery()) { return result.next(); }
        }
    }
}
