package com.iudigital.camilog.reservas_hotel.pattern.strategy;

public interface PagoStrategy {

    String procesarPago(Double monto, String concepto);
    String getNombreMetodo();
    Double calcularComision(Double monto);

}
