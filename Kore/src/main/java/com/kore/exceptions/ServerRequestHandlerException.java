package com.kore.exceptions;

public class ServerRequestHandlerException extends RemoteErrorException {
    public ServerRequestHandlerException(String message) {
        super(message);
    }
}
