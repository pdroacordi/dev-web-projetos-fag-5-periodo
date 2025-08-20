package io.acordi.classroom.domain.repository;

import io.acordi.classroom.domain.model.Turma;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface TurmaRepository {
    
    Turma save(Turma turma);
    
    Optional<Turma> findById(Long id);
    
    List<Turma> findAll();
    
    Stream<Turma> findAllAsStream();
    
    List<Turma> findByNomeContaining(String nome);
    
    List<Turma> findByCursoContaining(String curso);
    
    List<Turma> findByPeriodo(Integer periodo);
    
    Optional<Turma> findByNomeExato(String nome);
    
    List<Turma> findByCursoAndPeriodo(String curso, Integer periodo);
    
    List<Turma> findWithFilters(String nome, String curso, Integer periodo);
    
    long countByCurso(String curso);
    
    long countByPeriodo(Integer periodo);
    
    boolean existsByNome(String nome);
    
    boolean existsById(Long id);
    
    void deleteById(Long id);
    
    void delete(Turma turma);
    
    long count();
}