package com.iudigital.camilog.reservas_hotel.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

    private Long id;
    private String nombreCliente;
    private Long clienteId;
    private String numeroHabitacion;
    private String tipoHabitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private String estado;
    private Double total;
    private Long numeroNoches;
    private String estadoPago;
    private String metodoPago;

}
