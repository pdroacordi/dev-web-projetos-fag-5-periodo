package io.acordi.classroom.application.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.acordi.classroom.domain.model.Turma;

import java.util.Optional;

public record TurmaRequestDto(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @JsonProperty("nome")
    String nome,
    
    @NotBlank(message = "Curso é obrigatório")
    @Size(min = 2, max = 100, message = "Curso deve ter entre 2 e 100 caracteres")
    @JsonProperty("curso")
    String curso,
    
    @NotNull(message = "Período é obrigatório")
    @Min(value = 1, message = "Período deve ser no mínimo 1")
    @Max(value = 10, message = "Período deve ser no máximo 10")
    @JsonProperty("periodo")
    Integer periodo,
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @JsonProperty("descricao")
    String descricao
) {
    
    public TurmaRequestDto {
        nome = Optional.ofNullable(nome).map(String::trim).orElse(nome);
        curso = Optional.ofNullable(curso).map(String::trim).orElse(curso);
        descricao = Optional.ofNullable(descricao)
            .filter(d -> !d.trim().isEmpty())
            .map(String::trim)
            .orElse(null);
    }
    
    public Turma toEntity() {
        return Optional.ofNullable(descricao)
            .map(desc -> Turma.criar(nome, curso, periodo, desc))
            .orElseGet(() -> Turma.criar(nome, curso, periodo));
    }
    
    public boolean temDescricao() {
        return Optional.ofNullable(descricao)
            .map(d -> !d.trim().isEmpty())
            .orElse(false);
    }
}