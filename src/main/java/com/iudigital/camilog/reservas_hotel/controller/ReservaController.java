package com.iudigital.camilog.reservas_hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iudigital.camilog.reservas_hotel.model.dto.ReservaRequestDTO;
import com.iudigital.camilog.reservas_hotel.model.dto.ReservaResponseDTO;
import com.iudigital.camilog.reservas_hotel.pattern.singleton.GestorSistema;
import com.iudigital.camilog.reservas_hotel.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final GestorSistema gestorSistema;

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody ReservaRequestDTO dto) {
        ReservaResponseDTO creada = reservaService.crearReserva(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorCliente(
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerReservasPorCliente(clienteId));
    }

    @GetMapping("/ingresos")
    public ResponseEntity<Double> obtenerIngresos() {
        return ResponseEntity.ok(reservaService.calcularIngresosTotal());
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas() {
        return ResponseEntity.ok(reservaService.contarReservasPorEstado());
    }

    @GetMapping("/mes-actual")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerDelMes() {
        return ResponseEntity.ok(reservaService.obtenerReservasDelMes());
    }

    @GetMapping("/sistema/estado")
    public ResponseEntity<String> obtenerEstadoSistema() {
        return ResponseEntity.ok(gestorSistema.getEstadoSistema());
    }
}
