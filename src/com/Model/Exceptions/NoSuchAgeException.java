package com.Model.Exceptions;

/**
 * NoSuchAgeException is thrown when the users age is greater than 150 or lower than 0.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NoSuchAgeException extends Exception {
    public NoSuchAgeException(String message) {
        super(message);
    }
}
