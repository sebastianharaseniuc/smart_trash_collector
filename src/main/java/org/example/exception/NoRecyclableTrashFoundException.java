package org.example.exception;

public class NoRecyclableTrashFoundException extends Exception {
    public NoRecyclableTrashFoundException(String message)
    {
        super(message);
    }
}
