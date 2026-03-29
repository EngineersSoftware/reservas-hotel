package com.iudigital.camilog.reservas_hotel.service;

import com.iudigital.camilog.reservas_hotel.exception.RecursosNoEncontradoException;
import com.iudigital.camilog.reservas_hotel.exception.ReservaInvalidadException;
import com.iudigital.camilog.reservas_hotel.model.Cliente;
import com.iudigital.camilog.reservas_hotel.model.Habitacion;
import com.iudigital.camilog.reservas_hotel.model.Pago;
import com.iudigital.camilog.reservas_hotel.model.Reserva;
import com.iudigital.camilog.reservas_hotel.model.Enum.EstadoPago;
import com.iudigital.camilog.reservas_hotel.model.Enum.EstadoReserva;
import com.iudigital.camilog.reservas_hotel.model.Enum.MetodoPago;
import com.iudigital.camilog.reservas_hotel.model.dto.ReservaRequestDTO;
import com.iudigital.camilog.reservas_hotel.model.dto.ReservaResponseDTO;
import com.iudigital.camilog.reservas_hotel.pattern.singleton.GestorSistema;
import com.iudigital.camilog.reservas_hotel.pattern.strategy.PagoEfectivoStratefy;
import com.iudigital.camilog.reservas_hotel.pattern.strategy.PagoStrategy;
import com.iudigital.camilog.reservas_hotel.pattern.strategy.PagoTarjetaStrategy;
import com.iudigital.camilog.reservas_hotel.pattern.strategy.PagoTransferenciaStrategy;
import com.iudigital.camilog.reservas_hotel.repository.ClienteRepository;
import com.iudigital.camilog.reservas_hotel.repository.HabitacionRepository;
import com.iudigital.camilog.reservas_hotel.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final HabitacionRepository habitacionRepository;
    private final GestorSistema gestorSistema;
    private final PagoEfectivoStratefy pagoEfectivo;
    private final PagoTarjetaStrategy pagoTarjeta;
    private final PagoTransferenciaStrategy pagoTransferencia;

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodas() {
        return reservaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Reserva", "id", id));
        return convertirADTO(reserva);
    }

    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto) {
        try {
            validarFechas(dto.getFechaEntrada(), dto.getFechaSalida());
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RecursosNoEncontradoException(
                            "Cliente", "id", dto.getClienteId()));

            Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                    .orElseThrow(() -> new RecursosNoEncontradoException(
                            "Habitacion", "id", dto.getHabitacionId()));
            if (!habitacion.getDisponible()) {
                throw new ReservaInvalidadException(
                        "La habitación " + habitacion.getId() + " no está disponible",
                        "HABITACION_NO_DISPONIBLE");
            }
            List<Habitacion> disponibles = habitacionRepository
                    .findHabitacionesDisponiblesPorFechas(
                            dto.getFechaEntrada(), dto.getFechaSalida());

            boolean estaDisponible = disponibles
                    .stream()
                    .anyMatch(h -> h.getId().equals(dto.getHabitacionId()));

            if (!estaDisponible) {
                throw new ReservaInvalidadException(
                        "La habitación no está disponible para las fechas seleccionadas",
                        "FECHAS_NO_DISPONIBLES");
            }
            long noches = ChronoUnit.DAYS.between(
                    dto.getFechaEntrada(), dto.getFechaSalida());
            Double total = noches * habitacion.getPrecioPorNoche();

            total = aplicarDescuentoMembresia(total, cliente.getMembresia());

            Reserva reserva = Reserva.builder()
                    .cliente(cliente)
                    .habitacion(habitacion)
                    .fechaEntrada(dto.getFechaEntrada())
                    .fechaSalida(dto.getFechaSalida())
                    .estado(EstadoReserva.PENDIENTE)
                    .total(total)
                    .build();

            reserva = reservaRepository.save(reserva);

            PagoStrategy estrategiaPago = seleccionarEstrategiaPago(dto.getMetodoPago());
            String resultadoPago = estrategiaPago.procesarPago(
                    total,
                    "Reserva habitación " + habitacion.getId());
            System.out.println("[PAGO] " + resultadoPago);

            Pago pago = Pago.builder()
                    .reserva(reserva)
                    .monto(total)
                    .metodoPago(MetodoPago.valueOf(dto.getMetodoPago()))
                    .fechaPago(LocalDate.now())
                    .estadoPago(EstadoPago.COMPLETADO)
                    .build();

            reserva.setPago(pago);
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva = reservaRepository.save(reserva);

            habitacion.setDisponible(false);
            habitacionRepository.save(habitacion);

            gestorSistema.registrarNuevaReserva();
            gestorSistema.registrarPagoProcesado();

            return convertirADTO(reserva);
        } catch (ReservaInvalidadException | RecursosNoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new ReservaInvalidadException(
                    "Error al crear la reserva: " + e.getMessage());
        } finally {
            System.out.println("[AUDITORIA] Intento de creación de reserva procesado.");
        }
    }

    public ReservaResponseDTO cancelarReserva(Long id) {
        try {
            Reserva reserva = reservaRepository.findById(id)
                    .orElseThrow(() -> new RecursosNoEncontradoException("Reserva", "id", id));

            if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                throw new ReservaInvalidadException("La reserva ya está cancelada");
            }
            if (reserva.getEstado() == EstadoReserva.COMPLETADO) {
                throw new ReservaInvalidadException("No se puede cancelar una reserva completada");
            }

            reserva.setEstado(EstadoReserva.CANCELADA);

            reserva.getHabitacion().setDisponible(true);
            habitacionRepository.save(reserva.getHabitacion());

            if (reserva.getPago() != null) {
                reserva.getPago().setEstadoPago(EstadoPago.REEMBOLSADO);
            }

            reserva = reservaRepository.save(reserva);

            gestorSistema.registrarCancelacion();

            return convertirADTO(reserva);

        } finally {
            System.out.println("[AUDITORIA] Intento de cancelación de reserva: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerReservasPorCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursosNoEncontradoException("Cliente", "id", clienteId);
        }

        return reservaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double calcularIngresosTotal() {
        return reservaRepository.findByEstado(EstadoReserva.CONFIRMADA)
                .stream()
                .mapToDouble(Reserva::getTotal)
                .sum();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> contarReservasPorEstado() {
        return reservaRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEstado().name(),
                        Collectors.counting()));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerReservasDelMes() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = inicioMes.plusMonths(1).minusDays(1);

        List<Reserva> reservasDelMes = reservaRepository
                .findByFechaEntradaBetween(inicioMes, finMes)
                .stream()
                .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                .collect(Collectors.toList());

        reservasDelMes.forEach(r -> System.out.printf("[REPORTE] Reserva #%d - Cliente: %s - Total: $%.2f%n",
                r.getId(),
                r.getCliente().getNombre(),
                r.getTotal()));

        return reservasDelMes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private PagoStrategy seleccionarEstrategiaPago(String metodoPago) {
        return switch (metodoPago.toUpperCase()) {
            case "EFECTIVO" -> pagoEfectivo;
            case "TARJETA" -> pagoTarjeta;
            case "TRANSFERENCIA" -> pagoTransferencia;
            default -> throw new ReservaInvalidadException(
                    "Método de pago no reconocido: " + metodoPago);
        };
    }

    private Double aplicarDescuentoMembresia(Double total, String membresia) {

        double descuento = switch (membresia) {
            case "GOLD" -> 0.20;
            case "SILVER" -> 0.10;
            default -> 0.0;
        };
        double totalConDescuento = total * (1 - descuento);
        System.out.printf("[DESCUENTO] Membresía %s: %.0f%% off → $%.2f%n",
                membresia, descuento * 100, totalConDescuento);
        return totalConDescuento;
    }

    private void validarFechas(LocalDate entrada, LocalDate salida) {
        if (entrada == null || salida == null) {
            throw new ReservaInvalidadException("Las fechas no pueden ser nulas");
        }
        if (!salida.isAfter(entrada)) {
            throw new ReservaInvalidadException(
                    "La fecha de salida debe ser posterior a la de entrada",
                    "FECHAS_INVALIDAS");
        }
        if (ChronoUnit.DAYS.between(entrada, salida) > 30) {
            throw new ReservaInvalidadException(
                    "La reserva no puede superar 30 noches",
                    "DURACION_EXCEDIDA");
        }
    }

    private ReservaResponseDTO convertirADTO(Reserva reserva) {
        long noches = ChronoUnit.DAYS.between(
                reserva.getFechaEntrada(), reserva.getFechaSalida());

        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .nombreCliente(reserva.getCliente().getNombre())
                .clienteId(reserva.getCliente().getId())
                .numeroHabitacion(reserva.getHabitacion().getId().toString())
                .tipoHabitacion(reserva.getHabitacion().getTipo().name())
                .fechaEntrada(reserva.getFechaEntrada())
                .fechaSalida(reserva.getFechaSalida())
                .estado(reserva.getEstado().name())
                .total(reserva.getTotal())
                .numeroNoches(noches)
                .estadoPago(reserva.getPago() != null
                        ? reserva.getPago().getEstadoPago().name()
                        : "SIN_PAGO")
                .metodoPago(reserva.getPago() != null
                        ? reserva.getPago().getMetodoPago().name()
                        : null)
                .build();
    }
}
