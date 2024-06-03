package dev.transactionapp.exception;

public class InvalidTransactionTypeException extends RuntimeException{

    public InvalidTransactionTypeException(String msg){
        super(msg);
    }
}
