package dev.transactionapp.exception;

public class CurrencyNotMatchException extends RuntimeException{

    public CurrencyNotMatchException(String msg){
        super(msg);
    }
}