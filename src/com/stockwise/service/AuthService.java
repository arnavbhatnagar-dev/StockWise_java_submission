package com.stockwise.service;

import com.stockwise.dao.UserDAO;
import com.stockwise.exception.InvalidCredentialsException;
import com.stockwise.model.User;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password)
            throws SQLException, InvalidCredentialsException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new InvalidCredentialsException();
        }
        User user = userDAO.authenticate(username.trim(), password);
        if (user == null) throw new InvalidCredentialsException();
        return user;
    }
}
