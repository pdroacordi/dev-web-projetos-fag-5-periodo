package io.acordi.classroom.application.service;

import io.acordi.classroom.application.dto.TurmaRequestDto;
import io.acordi.classroom.application.dto.TurmaResponseDto;
import io.acordi.classroom.domain.model.Turma;
import io.acordi.classroom.domain.repository.TurmaRepository;
import io.acordi.classroom.infrastructure.exception.TurmaNotFoundException;
import io.acordi.classroom.infrastructure.exception.TurmaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurmaService {
    
    private final TurmaRepository turmaRepository;
    
    @Transactional
    public TurmaResponseDto createTurma(TurmaRequestDto requestDto) {
        log.info("Criando nova turma: {}", requestDto.nome());
        
        return Optional.of(requestDto)
            .filter(this::naoExisteNomeDuplicado)
            .map(TurmaRequestDto::toEntity)
            .map(turmaRepository::save)
            .map(TurmaResponseDto::fromEntity)
            .orElseThrow(() -> new TurmaValidationException(
                "Já existe uma turma com o nome: " + requestDto.nome()));
    }
    
    @Transactional(readOnly = true)
    public TurmaResponseDto findById(Long id) {
        log.debug("Buscando turma por ID: {}", id);
        
        return turmaRepository.findById(id)
            .map(TurmaResponseDto::fromEntity)
            .orElseThrow(turmaNotFoundById(id));
    }
    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findAll() {
        log.debug("Buscando todas as turmas");
        
        return turmaRepository.findAllAsStream()
            .map(TurmaResponseDto::fromEntity)
            .collect(Collectors.toList());
    }
    

    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findByNomeContaining(String nome) {
        log.debug("Buscando turmas por nome contendo: {}", nome);
        
        return buscarTurmasOuFalhar(
            () -> turmaRepository.findByNomeContaining(nome),
            () -> "Nenhuma turma encontrada com nome contendo: " + nome
        );
    }
    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findByCursoContaining(String curso) {
        log.debug("Buscando turmas por curso contendo: {}", curso);
        
        return buscarTurmasOuFalhar(
            () -> turmaRepository.findByCursoContaining(curso),
            () -> "Nenhuma turma encontrada para o curso: " + curso
        );
    }
    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findByPeriodo(Integer periodo) {
        log.debug("Buscando turmas por período: {}", periodo);
        
        return buscarTurmasOuFalhar(
            () -> turmaRepository.findByPeriodo(periodo),
            () -> "Nenhuma turma encontrada para o período: " + periodo
        );
    }
    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findByCursoAndPeriodo(String curso, Integer periodo) {
        log.debug("Buscando turmas por curso: {} e período: {}", curso, periodo);
        
        return buscarTurmasOuFalhar(
            () -> turmaRepository.findByCursoAndPeriodo(curso, periodo),
            () -> String.format("Nenhuma turma encontrada para curso '%s' e período %d", curso, periodo)
        );
    }
    
    @Transactional(readOnly = true)
    public List<TurmaResponseDto> findWithFilters(Optional<String> nome, Optional<String> curso, Optional<Integer> periodo) {
        log.debug("Buscando turmas com filtros dinâmicos - nome: {}, curso: {}, período: {}", nome, curso, periodo);
        
        // Converte Optional para null para a query JPQL
        String nomeParam = nome.filter(n -> !n.trim().isEmpty()).orElse(null);
        String cursoParam = curso.filter(c -> !c.trim().isEmpty()).orElse(null);
        Integer periodoParam = periodo.orElse(null);
        
        return turmaRepository.findWithFilters(nomeParam, cursoParam, periodoParam)
            .stream()
            .map(TurmaResponseDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long countByCurso(String curso) {
        log.debug("Contando turmas por curso: {}", curso);
        return turmaRepository.countByCurso(curso);
    }
    
    @Transactional(readOnly = true)
    public long countByPeriodo(Integer periodo) {
        log.debug("Contando turmas por período: {}", periodo);
        return turmaRepository.countByPeriodo(periodo);
    }
    
    @Transactional
    public void deleteById(Long id) {
        log.info("Deletando turma com ID: {}", id);
        
        turmaRepository.findById(id)
            .ifPresentOrElse(
                turma -> {
                    turmaRepository.delete(turma);
                    log.info("Turma deletada com sucesso: {}", turma);
                },
                () -> {
                    throw turmaNotFoundById(id).get();
                }
            );
    }
    
    @Transactional
    public TurmaResponseDto updateTurma(Long id, TurmaRequestDto requestDto) {
        log.info("Atualizando turma com ID: {}", id);
        
        return turmaRepository.findById(id)
            .map(turma -> turma.atualizarInformacoes(
                requestDto.nome(), 
                requestDto.curso(), 
                requestDto.periodo(), 
                requestDto.descricao()
            ))
            .map(turmaRepository::save)
            .map(TurmaResponseDto::fromEntity)
            .orElseThrow(turmaNotFoundById(id));
    }
    
    private boolean naoExisteNomeDuplicado(TurmaRequestDto requestDto) {
        return !turmaRepository.existsByNome(requestDto.nome());
    }
    
    private List<TurmaResponseDto> buscarTurmasOuFalhar(
            Supplier<List<Turma>> buscador, 
            Supplier<String> mensagemErro) {
        
        return Optional.of(buscador.get())
            .filter(turmas -> !turmas.isEmpty())
            .map(turmas -> turmas.stream()
                .map(TurmaResponseDto::fromEntity)
                .collect(Collectors.toList()))
            .orElseThrow(() -> new TurmaNotFoundException(mensagemErro.get()));
    }
    

    
    private Supplier<TurmaNotFoundException> turmaNotFoundById(Long id) {
        return () -> new TurmaNotFoundException("Turma não encontrada com ID: " + id);
    }
}