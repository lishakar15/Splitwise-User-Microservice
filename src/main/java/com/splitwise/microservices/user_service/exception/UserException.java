package com.splitwise.microservices.user_service.exception;


public class UserException extends Exception{

    public UserException(String message){
        super(message);
    }
    public UserException(String errMsg, Exception exception){
        super(errMsg,exception);
    }
}
