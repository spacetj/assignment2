package com.Model.Exceptions;

/**
 * NotToBeColleaguesException gets thrown when trying to connect a young adult into a colleague relation
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NotToBeColleaguesException extends Exception {
    public NotToBeColleaguesException(String message) {
        super(message);
    }
}
