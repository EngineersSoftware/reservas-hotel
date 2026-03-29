package com.iudigital.camilog.reservas_hotel.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iudigital.camilog.reservas_hotel.model.Habitacion;
import com.iudigital.camilog.reservas_hotel.service.HabitacionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @GetMapping
    public ResponseEntity<List<Habitacion>> obtenerTodas() {
        return ResponseEntity.ok(habitacionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Habitacion> crear(@RequestBody Habitacion habitacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitacionService.crear(habitacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Habitacion> actualizar(
            @PathVariable Long id,
            @RequestBody Habitacion habitacion) {
        return ResponseEntity.ok(habitacionService.actualizar(id, habitacion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Habitacion>> obtenerDisponibles() {
        return ResponseEntity.ok(habitacionService.obtenerHabitacionesDisponibles());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Habitacion>> buscarPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaEntrada,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida) {
        return ResponseEntity.ok(
                habitacionService.obtenerDisponibles(fechaEntrada, fechaSalida));
    }

    @GetMapping("/precio-promedio")
    public ResponseEntity<Double> obtenerPrecioPromedio() {
        return ResponseEntity.ok(habitacionService.calcularPrecioPromedio());
    }
}
