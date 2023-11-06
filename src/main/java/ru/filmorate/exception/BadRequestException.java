package ru.filmorate.exception;

/**
 * The type Bad request exception
 */

public class BadRequestException extends RuntimeException {
    private final String parameter;

    /**
     * Instantiates a new Bad request exception
     * @param parameter the parameter
     */

    public BadRequestException(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Get parameter
     * @return the parameter
     */

    public String getParameter() {
        return parameter;
    }
}