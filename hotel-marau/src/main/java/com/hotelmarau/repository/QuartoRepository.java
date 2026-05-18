package com.hotelmarau.repository;

import com.hotelmarau.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    List<Quarto> findByResidenciaId(Long residenciaId);

    List<Quarto> findByResidenciaIdAndAtivoTrue(Long residenciaId);

    /**
     * Busca quartos disponíveis em um período (sem aluguéis ativos sobrepostos)
     */
    @Query("""
            SELECT q FROM Quarto q
            WHERE q.residencia.id = :residenciaId
              AND q.ativo = true
              AND q.id NOT IN (
                  SELECT a.quarto.id FROM Aluguel a
                  WHERE a.status <> 'CANCELADO'
                    AND a.dataEntrada < :dataFim
                    AND a.dataSaida > :dataInicio
              )
            """)
    List<Quarto> findQuartosDisponiveis(
            @Param("residenciaId") Long residenciaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
