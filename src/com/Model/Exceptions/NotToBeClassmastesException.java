package com.Model.Exceptions;

/**
 * NotToBeClassmatesException is thrown when trying to make an Infant a classmate.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NotToBeClassmastesException extends Exception {
    public NotToBeClassmastesException(String message) {
        super(message);
    }
}
