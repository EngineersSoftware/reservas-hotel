package com.iudigital.camilog.reservas_hotel.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iudigital.camilog.reservas_hotel.exception.RecursosNoEncontradoException;
import com.iudigital.camilog.reservas_hotel.model.Cliente;
import com.iudigital.camilog.reservas_hotel.repository.ClienteRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradoException("Cliente", "id", id));
    }

    public Cliente crear(Cliente cliente) {
        try {
            if (clienteRepository.existsByEmail(cliente.getEmail())) {
                throw new IllegalArgumentException("Ya existe un cliente con el email: " + cliente.getEmail());
            }
            if (cliente.getMembresia() == null || cliente.getMembresia().isEmpty()) {
                cliente.setMembresia("BASICO");
            }
            return clienteRepository.save(cliente);
        } catch (IllegalArgumentException e) {
            throw e;
        } finally {
            System.out.println("[ClienteService] Intento de creacion de cliente: " + cliente.getEmail());
        }
    }

    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = obtenerPorId(id);
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setMembresia(clienteActualizado.getMembresia());

        return clienteRepository.save(clienteExistente);
    }

    public void eliminar(Long id) {
        Cliente cliente = obtenerPorId(id);
        clienteRepository.delete(cliente);
    }

    @Transactional(readOnly = true)
    public Map<String, List<Cliente>> obtenerClientesPorMembresia() {
        return clienteRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Cliente::getMembresia));
    }

    @Transactional(readOnly = true)
    public List<String> obtenerClientesNombresGold() {
        return clienteRepository.findAll()
                .stream()
                .filter(c -> "GOLD".equals(c.getMembresia()))
                .map(Cliente::getNombre)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long contarTotalReservasDelSistema(){
        return clienteRepository.findAll()
                .stream()
                .flatMap(c -> c.getReservas().stream())
                .count();
    }
}
