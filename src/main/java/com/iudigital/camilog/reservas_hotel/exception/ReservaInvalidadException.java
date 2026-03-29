package com.iudigital.camilog.reservas_hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReservaInvalidadException extends RuntimeException{

    private final String codigoError;

    public ReservaInvalidadException(String mensaje) {
        super(mensaje);
        this.codigoError = "RESERVA_INVALIDA";
    }

    public ReservaInvalidadException(String mensaje, String codigoError) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }

}
