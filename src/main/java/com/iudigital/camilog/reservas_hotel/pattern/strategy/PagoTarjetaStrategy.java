package com.iudigital.camilog.reservas_hotel.pattern.strategy;

import org.springframework.stereotype.Component;

@Component("pagoTarjeta")
public class PagoTarjetaStrategy implements PagoStrategy{

    private static final double COMISION_TARJETA = 0.03;

    @Override
    public String procesarPago(Double monto, String concepto) {
        Double comision = calcularComision(monto);
        Double montoFinal = monto + comision;

        System.out.println("[TARJETA] Conectando con pasarela de pago.... ");
        System.out.println("[TARJETA] Monto base: $" + monto);
        System.out.println("[TARJETA] Comision: (3%):$" + comision);
        System.out.println("[TARJETA] Monto total: $" + montoFinal);

        return String.format("Pago con tarjeta procesado. Monto: $%.2f + comision $%.2f = $%.2f", monto, comision, montoFinal);
    }

    @Override
    public String getNombreMetodo() {
       return "TARJETA";
    }

    @Override
    public Double calcularComision(Double monto) {
        return monto * COMISION_TARJETA;
    }

}
