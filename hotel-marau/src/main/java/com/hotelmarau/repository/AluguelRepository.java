package com.hotelmarau.repository;

import com.hotelmarau.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    List<Aluguel> findByClienteId(Long clienteId);
    List<Aluguel> findByResidenciaId(Long residenciaId);
    List<Aluguel> findByQuartoId(Long quartoId);
}
