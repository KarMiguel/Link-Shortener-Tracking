package io.github.karMiguel.capzip.exceptions;

public class InvalidJwtAuthenticationException  extends RuntimeException{

    public InvalidJwtAuthenticationException(String ex){
        super(ex);
    }
}

