package com.iudigital.camilog.reservas_hotel.pattern.singleton;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

@Component
public class GestorSistema {

    private final String nombreHotel = "Hotel IUDigital";
    private final String versionSistema = "1.0.0";
    private final LocalDateTime fechaInicio = LocalDateTime.now();

    private final AtomicInteger totalReservasCreadas = new AtomicInteger(0);
    private final AtomicInteger totalPagosProcesados = new AtomicInteger(0);
    private final AtomicInteger totalCancelaciones = new AtomicInteger(0);

    public void registrarNuevaReserva() {
        totalReservasCreadas.incrementAndGet();
        System.out.println("[SISTEMA] Nueva reserva registrada. Total: " + totalReservasCreadas.get());
    }

    public void registrarPagoProcesado() {
        totalPagosProcesados.incrementAndGet();
        System.out.println("[SISTEMA] Pago procesado. Total pagos: " + totalPagosProcesados.get());
    }

    public void registrarCancelacion() {
        totalCancelaciones.incrementAndGet();
        System.out.println("[SISTEMA] Cancelación registrada. Total cancelaciones: " + totalCancelaciones.get());
    }

    public String getEstadoSistema() {
        return String.format(
                " ------ %s (v%s) ------%n" +
                        "Inicio: %s%n" +
                        "Reservas creadas: %d%n" +
                        "Pagos procesados: %d%n" +
                        "Cancelaciones: %d",
                nombreHotel,
                versionSistema,
                fechaInicio,
                totalReservasCreadas.get(),
                totalPagosProcesados.get(),
                totalCancelaciones.get());
    }

    public String getNombreHotel() {
        return nombreHotel;
    }

    public String getVersionSistema() {
        return versionSistema;
    }

    public int getTotalReservasCreadas() {
        return totalReservasCreadas.get();
    }

    public int getTotalPagosProcesados() {
        return totalPagosProcesados.get();
    }

    public int getTotalCancelaciones() {
        return totalCancelaciones.get();
    }

}
