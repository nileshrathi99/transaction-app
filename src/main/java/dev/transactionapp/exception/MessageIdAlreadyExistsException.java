package dev.transactionapp.exception;

public class MessageIdAlreadyExistsException extends RuntimeException{

    public MessageIdAlreadyExistsException(String msg){
        super(msg);
    }
}
