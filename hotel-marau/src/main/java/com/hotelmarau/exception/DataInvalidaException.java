package com.hotelmarau.exception;

/**
 * Exceção lançada quando as datas informadas
 * são inválidas ou inconsistentes.
 */
public class DataInvalidaException extends Exception {

    public DataInvalidaException(String message) {
        super(message);
    }

    public DataInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataInvalidaException() {
        super("Data inválida ou inconsistente. Verifique o período solicitado.");
    }
}
