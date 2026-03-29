package com.iudigital.camilog.reservas_hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iudigital.camilog.reservas_hotel.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
    List<Cliente> findByMembresia(String membresia);
    boolean existsByEmail(String email);
    @Query("SELECT DISTINCT c FROM Cliente c JOIN c.reservas r WHERE r.estado = 'CONFIRMADA'")
    List<Cliente> findClientesConReservasConfirmadas();

}
