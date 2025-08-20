package io.acordi.classroom.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.acordi.classroom.domain.model.Turma;

import java.util.Optional;

public record TurmaResponseDto(
    @JsonProperty("id")
    Long id,
    
    @JsonProperty("nome")
    String nome,
    
    @JsonProperty("curso")
    String curso,
    
    @JsonProperty("periodo")
    Integer periodo,
    
    @JsonProperty("descricao")
    String descricao
) {
    
    public static TurmaResponseDto fromEntity(Turma turma) {
        return new TurmaResponseDto(
            turma.getId(),
            turma.getNome(),
            turma.getCurso(),
            turma.getPeriodo(),
            turma.getDescricao().orElse(null)
        );
    }
    
    public boolean temDescricao() {
        return Optional.ofNullable(descricao)
            .map(d -> !d.trim().isEmpty())
            .orElse(false);
    }
    
    public Optional<String> getDescricaoOpcional() {
        return Optional.ofNullable(descricao)
            .filter(d -> !d.trim().isEmpty());
    }
}