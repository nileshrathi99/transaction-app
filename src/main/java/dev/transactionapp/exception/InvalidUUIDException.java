package dev.transactionapp.exception;

public class InvalidUUIDException extends RuntimeException{

    public InvalidUUIDException(String msg){
        super(msg);
    }
}
