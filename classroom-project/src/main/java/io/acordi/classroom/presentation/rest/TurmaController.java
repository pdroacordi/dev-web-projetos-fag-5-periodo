package io.acordi.classroom.presentation.rest;

import io.acordi.classroom.application.dto.TurmaRequestDto;
import io.acordi.classroom.application.dto.TurmaResponseDto;
import io.acordi.classroom.application.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/turmas")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Turmas", description = "API para gerenciamento de turmas")
public class TurmaController {
    
    private final TurmaService turmaService;
    
    @PostMapping
    @Operation(summary = "Criar nova turma", description = "Cria uma nova turma no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Turma criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Turma já existe")
    })
    public ResponseEntity<TurmaResponseDto> createTurma(
            @Valid @RequestBody TurmaRequestDto requestDto) {
        
        log.info("Requisição para criar turma: {}", requestDto.nome());
        
        return Optional.of(requestDto)
            .map(turmaService::createTurma)
            .map(this::buildCreatedResponse)
            .orElseThrow();
    }
    
    @GetMapping("/{turmaId}")
    @Operation(summary = "Buscar turma por ID", description = "Retorna uma turma específica pelo seu ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turma encontrada"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public ResponseEntity<TurmaResponseDto> getTurmaById(
            @Parameter(description = "ID da turma", required = true)
            @PathVariable @Min(1) Long turmaId) {
        
        log.debug("Buscando turma por ID: {}", turmaId);
        
        TurmaResponseDto turma = turmaService.findById(turmaId);
        return ResponseEntity.ok(turma);
    }
    
    @GetMapping
    @Operation(
        summary = "Buscar turmas", 
        description = "Busca turmas com filtros opcionais via query parameters. Sem filtros retorna todas as turmas."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de turmas retornada"),
        @ApiResponse(responseCode = "204", description = "Nenhuma turma encontrada")
    })
    public ResponseEntity<List<TurmaResponseDto>> getTurmas(
            @Parameter(description = "Filtro por nome (busca parcial, case-insensitive)")
            @RequestParam Optional<String> nome,
            @Parameter(description = "Filtro por curso (busca parcial, case-insensitive)")
            @RequestParam Optional<String> curso,
            @Parameter(description = "Filtro por período exato")
            @RequestParam Optional<@Min(1) Integer> periodo) {
        
        log.debug("Requisição de busca de turmas - nome: {}, curso: {}, período: {}", nome, curso, periodo);
        
        // Se nenhum filtro, retorna todas as turmas
        if (nome.isEmpty() && curso.isEmpty() && periodo.isEmpty()) {
            List<TurmaResponseDto> turmas = turmaService.findAll();
            return turmas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(turmas);
        }
        
        // Caso contrário, aplica filtros via query no banco
        List<TurmaResponseDto> turmas = turmaService.findWithFilters(nome, curso, periodo);
        return turmas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(turmas);
    }
    
    @GetMapping("/stats/curso/{curso}")
    @Operation(summary = "Contar turmas por curso", description = "Retorna a quantidade de turmas de um curso")
    public ResponseEntity<Long> countByCurso(
            @Parameter(description = "Nome do curso", required = true)
            @PathVariable @NotBlank String curso) {
        
        return ResponseEntity.ok(turmaService.countByCurso(curso));
    }
    
    @GetMapping("/stats/periodo/{periodo}")
    @Operation(summary = "Contar turmas por período", description = "Retorna a quantidade de turmas de um período")
    public ResponseEntity<Long> countByPeriodo(
            @Parameter(description = "Período", required = true)
            @PathVariable @Min(1) Integer periodo) {
        
        return ResponseEntity.ok(turmaService.countByPeriodo(periodo));
    }
    
    @PutMapping("/{turmaId}")
    @Operation(summary = "Atualizar turma", description = "Atualiza uma turma existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<TurmaResponseDto> updateTurma(
            @Parameter(description = "ID da turma", required = true)
            @PathVariable @Min(1) Long turmaId,
            @Valid @RequestBody TurmaRequestDto requestDto) {
        
        log.info("Atualizando turma ID: {} com dados: {}", turmaId, requestDto.nome());
        
        return ResponseEntity.ok(turmaService.updateTurma(turmaId, requestDto));
    }
    
    @DeleteMapping("/{turmaId}")
    @Operation(summary = "Deletar turma", description = "Remove uma turma do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Turma deletada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    public ResponseEntity<Void> deleteTurma(
            @Parameter(description = "ID da turma", required = true)
            @PathVariable @Min(1) Long turmaId) {
        
        log.info("Deletando turma ID: {}", turmaId);
        
        turmaService.deleteById(turmaId);
        return ResponseEntity.noContent().build();
    }
    
    private ResponseEntity<TurmaResponseDto> buildCreatedResponse(TurmaResponseDto turma) {
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(turma.id())
            .toUri();
        
        return ResponseEntity.created(location).body(turma);
    }
    

}