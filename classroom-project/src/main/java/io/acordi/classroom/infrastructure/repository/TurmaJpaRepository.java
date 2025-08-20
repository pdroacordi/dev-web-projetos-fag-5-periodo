package io.acordi.classroom.infrastructure.repository;

import io.acordi.classroom.domain.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface TurmaJpaRepository extends JpaRepository<Turma, Long> {
    
    List<Turma> findByNomeContainingIgnoreCase(String nome);
    
    List<Turma> findByCursoContainingIgnoreCase(String curso);
    
    List<Turma> findByPeriodo(Integer periodo);
    
    Optional<Turma> findByNomeIgnoreCase(String nome);
    
    List<Turma> findByCursoIgnoreCaseAndPeriodo(String curso, Integer periodo);
    
    long countByCursoIgnoreCase(String curso);
    
    long countByPeriodo(Integer periodo);
    
    boolean existsByNomeIgnoreCase(String nome);
    
    @Query("SELECT t FROM Turma t")
    Stream<Turma> findAllAsStream();
    
    @Query("SELECT COUNT(t) > 0 FROM Turma t WHERE LOWER(t.curso) = LOWER(:curso) AND t.periodo = :periodo")
    boolean existsByCursoAndPeriodo(@Param("curso") String curso, @Param("periodo") Integer periodo);

    @Query("""
      SELECT t FROM Turma t WHERE 
      (:nome IS NULL  OR LOWER(t.nome)  LIKE CONCAT('%', LOWER(CAST(:nome as string)),  '%')) AND
      (:curso IS NULL OR LOWER(t.curso) LIKE CONCAT('%', LOWER(CAST(:curso as string)), '%')) AND
      (:periodo IS NULL OR t.periodo = :periodo)
      ORDER BY t.nome ASC
    """)
    List<Turma> findWithDynamicFilters(
        @Param("nome") String nome, 
        @Param("curso") String curso, 
        @Param("periodo") Integer periodo
    );
}