package com.stockwise.exception;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(int categoryId) {
        super("Category with ID " + categoryId + " was not found.");
    }
}
