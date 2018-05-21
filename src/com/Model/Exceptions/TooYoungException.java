package com.Model.Exceptions;

/**
 * TooYoungException gets thrown when trying to make friend with young child.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class TooYoungException extends Exception {
    public TooYoungException(String message) {
        super(message);
    }
}
