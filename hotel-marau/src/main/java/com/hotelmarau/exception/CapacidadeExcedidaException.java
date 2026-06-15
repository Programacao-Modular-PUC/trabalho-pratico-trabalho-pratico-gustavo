package com.hotelmarau.exception;

/**
 * Exceção lançada quando o número de hóspedes
 * excede a capacidade de um quarto.
 */
public class CapacidadeExcedidaException extends Exception {

    public CapacidadeExcedidaException(String message) {
        super(message);
    }

    public CapacidadeExcedidaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CapacidadeExcedidaException() {
        super("O número de hóspedes excede a capacidade do quarto.");
    }
}
