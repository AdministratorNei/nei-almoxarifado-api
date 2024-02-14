package com.ufrn.nei.almoxarifadoapi.exception;

public class PasswordInvalidException extends RuntimeException {
    public PasswordInvalidException() {
        super("Erro nas validação da senha");
    }

    public PasswordInvalidException(String message) {
        super(message);
    }
}
