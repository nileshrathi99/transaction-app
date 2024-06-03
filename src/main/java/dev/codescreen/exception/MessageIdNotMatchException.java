package dev.codescreen.exception;

public class MessageIdNotMatchException extends RuntimeException{

    public MessageIdNotMatchException(String msg){
        super(msg);
    }
}
