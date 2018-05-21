package com.Model.Exceptions;

/**
 * NotToBeFriendsException gets thrown when trying to make an adult and a child friend or connect two children
 * with an age gap larger than 3.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NotToBeFriendsException extends Exception {
    public NotToBeFriendsException(String message) {
        super(message);
    }
}
