package com.hotelmarau.exception;

/**
 * Exceção lançada quando um quarto não está disponível para
 * uma reserva ou aluguel no período solicitado.
 */
public class QuartoIndisponivelException extends Exception {

    public QuartoIndisponivelException(String message) {
        super(message);
    }

    public QuartoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuartoIndisponivelException() {
        super("O quarto não está disponível para o período solicitado.");
    }
}
