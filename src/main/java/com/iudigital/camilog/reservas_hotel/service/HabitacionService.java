package com.iudigital.camilog.reservas_hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iudigital.camilog.reservas_hotel.exception.RecursosNoEncontradoException;
import com.iudigital.camilog.reservas_hotel.model.Habitacion;
import com.iudigital.camilog.reservas_hotel.model.Enum.TipoHabitacion;
import com.iudigital.camilog.reservas_hotel.repository.HabitacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    @Transactional(readOnly = true)
    public List<Habitacion> obtenerTodas() {
        return habitacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Habitacion obtenerPorId(Long id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Habitacion", "id", id));
    }

    public Habitacion crear(Habitacion habitacion) {
        try {
            if (habitacion.getPrecioPorNoche() <= 0) {
                throw new IllegalArgumentException("El precio por noche debe ser mayor a 0");
            }
            if (habitacion.getCapacidad() <= 0) {
                throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
            }
            habitacion.setDisponible(true);
            return habitacionRepository.save(habitacion);
        } finally {
            System.out.println("[HabitacionService] Procesando habitacion: " + habitacion.getId());
        }
    }

    public Habitacion actualizar(Long id, Habitacion datos) {
        Habitacion habitacionExistente = obtenerPorId(id);
        habitacionExistente.setTipo(datos.getTipo());
        habitacionExistente.setPrecioPorNoche(datos.getPrecioPorNoche());
        habitacionExistente.setCapacidad(datos.getCapacidad());
        habitacionExistente.setDisponible(datos.getDisponible());
        return habitacionRepository.save(habitacionExistente);
    }

    public void eliminar(Long id) {
        Habitacion habitacion = obtenerPorId(id);
        habitacionRepository.delete(habitacion);
    }

    @Transactional(readOnly = true)
    public List<Habitacion> obtenerHabitacionesDisponibles() {
        return habitacionRepository.findByDisponibleTrue();
    }

    @Transactional(readOnly = true)
    public List<Habitacion> obtenerDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada.isAfter(fechaSalida) || fechaEntrada.isEqual(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada");
        }
        return habitacionRepository.findHabitacionesDisponiblesPorFechas(fechaEntrada, fechaSalida).stream()
                .sorted((h1, h2) -> Double.compare(h1.getPrecioPorNoche(), h2.getPrecioPorNoche()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public double calcularPrecioPromedio(){
        return habitacionRepository.findAll()
                .stream()
                .mapToDouble(Habitacion::getPrecioPorNoche)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<Long> obtenerNumeroHabitacionesPorTipo(TipoHabitacion tipo) {
        return habitacionRepository.findAll()
                .stream()
                .filter(h -> h.getTipo() == tipo)
                .filter(Habitacion::getDisponible)
                .map(Habitacion::getId)
                .sorted()
                .collect(Collectors.toList());
    }
}