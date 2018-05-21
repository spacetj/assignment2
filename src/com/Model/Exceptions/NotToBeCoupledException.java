package com.Model.Exceptions;

/**
 * NotToBeCoupledException gets thrown when trying to make a couple when a user is not an adult.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NotToBeCoupledException extends Exception {
    public NotToBeCoupledException(String message) {
        super(message);
    }
}
