package com.iudigital.camilog.reservas_hotel.pattern.strategy;

import org.springframework.stereotype.Component;

@Component("pagoTransferencia")
public class PagoTransferenciaStrategy implements PagoStrategy {

    private static final double COMISION_TRANSFERENCIA = 0.01;

    @Override
    public String procesarPago(Double monto, String concepto) {
        Double comision = calcularComision(monto);

        System.out.println("[TRANSFERENCIA] Verificando comprobante bancario....");
        System.out.println("[TRANSFERENCIA] Concepto a registrar: $" + concepto);

        return String.format(
                "Transferencia de $%.2f verificada. Comision bancaria: $%.2f"
                        + "Tiempo de acreditacion: 1-2 días habiles.",
                monto, comision);
    }

    @Override
    public String getNombreMetodo() {
        return "TRANSFERENCIA";
    }

    @Override
    public Double calcularComision(Double monto) {
        return monto * COMISION_TRANSFERENCIA;
    }

}
