package io.acordi.classroom.infrastructure.repository;

import io.acordi.classroom.domain.model.Turma;
import io.acordi.classroom.domain.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class TurmaRepositoryImpl implements TurmaRepository {
    
    private final TurmaJpaRepository jpaRepository;
    
    @Override
    @Transactional
    public Turma save(Turma turma) {
        log.debug("Salvando turma: {}", turma);
        return jpaRepository.save(turma);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Turma> findById(Long id) {
        log.debug("Buscando turma por ID: {}", id);
        return jpaRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findAll() {
        log.debug("Buscando todas as turmas");
        return jpaRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Stream<Turma> findAllAsStream() {
        log.debug("Obtendo stream de todas as turmas");
        return jpaRepository.findAllAsStream();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findByNomeContaining(String nome) {
        log.debug("Buscando turmas por nome contendo: {}", nome);
        return jpaRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findByCursoContaining(String curso) {
        log.debug("Buscando turmas por curso contendo: {}", curso);
        return jpaRepository.findByCursoContainingIgnoreCase(curso);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findByPeriodo(Integer periodo) {
        log.debug("Buscando turmas por período: {}", periodo);
        return jpaRepository.findByPeriodo(periodo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Turma> findByNomeExato(String nome) {
        log.debug("Buscando turma por nome exato: {}", nome);
        return jpaRepository.findByNomeIgnoreCase(nome);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findByCursoAndPeriodo(String curso, Integer periodo) {
        log.debug("Buscando turmas por curso: {} e período: {}", curso, periodo);
        return jpaRepository.findByCursoIgnoreCaseAndPeriodo(curso, periodo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByCurso(String curso) {
        log.debug("Contando turmas por curso: {}", curso);
        return jpaRepository.countByCursoIgnoreCase(curso);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countByPeriodo(Integer periodo) {
        log.debug("Contando turmas por período: {}", periodo);
        return jpaRepository.countByPeriodo(periodo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByNome(String nome) {
        log.debug("Verificando existência de turma por nome: {}", nome);
        return jpaRepository.existsByNomeIgnoreCase(nome);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        log.debug("Verificando existência de turma por ID: {}", id);
        return jpaRepository.existsById(id);
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deletando turma por ID: {}", id);
        jpaRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void delete(Turma turma) {
        log.debug("Deletando turma: {}", turma);
        jpaRepository.delete(turma);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Contando total de turmas");
        return jpaRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Turma> findWithFilters(String nome, String curso, Integer periodo) {
        log.debug("Buscando turmas com filtros dinâmicos - nome: {}, curso: {}, período: {}", 
            nome, curso, periodo);
        return jpaRepository.findWithDynamicFilters(nome, curso, periodo);
    }
}