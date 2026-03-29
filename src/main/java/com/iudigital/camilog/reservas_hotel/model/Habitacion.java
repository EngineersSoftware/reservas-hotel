package com.iudigital.camilog.reservas_hotel.model;

import java.util.ArrayList;
import java.util.List;

import com.iudigital.camilog.reservas_hotel.model.Enum.TipoHabitacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "habitacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHabitacion tipo;

    @Column(name = "precio_por_noche", nullable = false)
    private Double precioPorNoche;

    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(nullable = false)
    private Integer capacidad;

    @OneToMany(mappedBy = "habitacion", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

}
