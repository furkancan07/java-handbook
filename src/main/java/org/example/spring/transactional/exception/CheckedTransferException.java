package org.example.spring.transactional.exception;

public class CheckedTransferException extends Exception {

    public CheckedTransferException(String message) {
        super(message);
    }
}
