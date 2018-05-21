package com.Model.Exceptions;

/**
 * NoParentException is thrown when a child or young adult has no parent or only 1 parent.
 * Also gets thrown when trying to delete an adult with atleast 1 dependant.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class NoParentException extends Exception {
    public NoParentException(String message) {
        super(message);
    }
}
