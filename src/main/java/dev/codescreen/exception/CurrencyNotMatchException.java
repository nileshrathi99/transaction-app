package dev.codescreen.exception;

public class CurrencyNotMatchException extends RuntimeException{

    public CurrencyNotMatchException(String msg){
        super(msg);
    }
}