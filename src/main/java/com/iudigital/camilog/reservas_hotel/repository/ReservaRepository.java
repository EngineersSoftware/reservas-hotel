package com.iudigital.camilog.reservas_hotel.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iudigital.camilog.reservas_hotel.model.Reserva;
import com.iudigital.camilog.reservas_hotel.model.Enum.EstadoReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByEstado(EstadoReserva estado);
    List<Reserva> findByHabitacionId(Long habitacionId);
    long countByClienteIdAndEstado(Long clienteId, EstadoReserva estado);
    List<Reserva> findByFechaEntradaBetween(LocalDate inicio, LocalDate fin);
    @Query("SELECT COALESCE(SUM(r.total), 0.0) FROM Reserva r WHERE r.estado = 'CONFIRMADA'")
    Double calcularIngresosTotales();
}
