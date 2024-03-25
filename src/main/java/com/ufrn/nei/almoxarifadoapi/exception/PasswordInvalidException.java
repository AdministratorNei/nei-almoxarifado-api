package com.ufrn.nei.almoxarifadoapi.exception;

public class PasswordInvalidException extends RuntimeException {
    public PasswordInvalidException() {
        super("Erro nas validações de senha");
    }

    public PasswordInvalidException(String message) {
        super(message);
    }
}
