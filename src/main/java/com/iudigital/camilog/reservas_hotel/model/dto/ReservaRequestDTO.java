package com.iudigital.camilog.reservas_hotel.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class ReservaRequestDTO {

    @NotNull(message = "El ID de la habitacion es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID de la habitacion es obligatorio")
    private Long habitacionId;

    @NotNull(message = "La fecha de entrada es obligatoria")
    @FutureOrPresent(message = "La fecha de entrada no puede ser en el pasado")
    private LocalDate fechaEntrada;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser en el futuro")
    private LocalDate fechaSalida;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @Pattern(regexp = "EFECTIVO|TARJETA|TRANSFERENCIA", message = "Metodo de pago invalido. Use: EFECTIVO, TARJETA o TRANSFERENCIA")
    private String metodoPago;

}
