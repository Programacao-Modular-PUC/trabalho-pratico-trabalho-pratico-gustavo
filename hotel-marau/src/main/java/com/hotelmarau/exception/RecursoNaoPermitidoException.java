package com.hotelmarau.exception;

/**
 * Exceção lançada quando um recurso é solicitado
 * mas não é compatível com o tipo de quarto.
 */
public class RecursoNaoPermitidoException extends Exception {

    public RecursoNaoPermitidoException(String message) {
        super(message);
    }

    public RecursoNaoPermitidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecursoNaoPermitidoException() {
        super("O recurso solicitado não é permitido para este tipo de quarto.");
    }
}
