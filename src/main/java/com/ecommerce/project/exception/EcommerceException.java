package com.ecommerce.project.exception;

public class EcommerceException extends RuntimeException{

    private static final long serialVersionId = 1L;

    public EcommerceException(){

    }

    public EcommerceException(String message){
        super(message);
    }
}
