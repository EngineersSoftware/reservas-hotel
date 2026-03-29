package com.iudigital.camilog.reservas_hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iudigital.camilog.reservas_hotel.model.Habitacion;
import com.iudigital.camilog.reservas_hotel.model.Enum.TipoHabitacion;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByDisponibleTrue();

    List<Habitacion> findByTipo(TipoHabitacion tipo);

    List<Habitacion> findByPrecioPorNocheBetween(Double precioMin, Double precioMax);

    @Query("SELECT h FROM Habitacion h WHERE h.disponible = true " +
            "AND h.id NOT IN (" +
            "SELECT r.habitacion.id FROM Reserva r " +
            "WHERE r.estado != 'CANCELADA' " +
            "AND r.fechaEntrada < :fechaSalida " +
            "AND r.fechaSalida > :fechaEntrada)")
    List<Habitacion> findHabitacionesDisponiblesPorFechas(LocalDate fechaEntrada, LocalDate fechaSalida);

}
