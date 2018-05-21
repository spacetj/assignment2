package com.Model.Exceptions;

/**
 * NoAvailableException gets thrown when trying to make 2 adults a couple where one of them is already coupled.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NoAvailableException extends Exception{
    public NoAvailableException(String message) {
        super(message);
    }
}
