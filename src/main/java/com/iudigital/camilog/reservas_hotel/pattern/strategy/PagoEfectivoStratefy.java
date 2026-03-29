package com.iudigital.camilog.reservas_hotel.pattern.strategy;

import org.springframework.stereotype.Component;

@Component("pagoEfectivo")
public class PagoEfectivoStratefy implements PagoStrategy {

    @Override
    public String procesarPago(Double monto, String concepto) {
        System.out.println("[EFECTIVO] Procesando pago de " + monto);
        System.out.println("[EFECTIVO] Concepto: " + concepto);
        System.out.println("[EFECTIVO] Por favor, entregue los billetes al recepcionista");

        return String.format("Pago en efectivo de $%.2f procesando exitosamente" + "Entregue recibo fisico al cliente",
                monto);
    }

    @Override
    public String getNombreMetodo() {
        return "EFECTIVO";
    }

    @Override
    public Double calcularComision(Double monto) {
        return 0.0;
    }

}
