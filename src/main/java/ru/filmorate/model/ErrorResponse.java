package ru.filmorate.model;

/**
 * The type Error response
 */

public class ErrorResponse {
    private final String error;

    /**
     * Instantiates a new Error response
     * @param error the error
     */

    public ErrorResponse(String error) {
        this.error = error;
    }

    /**
     * Get error
     * @return the error
     */

    public String getError() {
        return error;
    }
}