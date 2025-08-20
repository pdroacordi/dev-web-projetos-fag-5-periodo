package io.acordi.classroom.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "turmas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 100)
    private String curso;
    
    @Column(nullable = false)
    private Integer periodo;
    
    @Column(length = 500)
    private String descricao;

    private Turma(String nome, String curso, Integer periodo, String descricao) {
        this.nome = validarNome(nome);
        this.curso = validarCurso(curso);
        this.periodo = validarPeriodo(periodo);
        this.descricao = descricao;
    }

    public static Turma criar(String nome, String curso, Integer periodo) {
        return new Turma(nome, curso, periodo, null);
    }

    public static Turma criar(String nome, String curso, Integer periodo, String descricao) {
        return new Turma(nome, curso, periodo, descricao);
    }

    public Turma atualizarInformacoes(String nome, String curso, Integer periodo, String descricao) {
        this.nome = validarNome(nome);
        this.curso = validarCurso(curso);
        this.periodo = validarPeriodo(periodo);
        this.descricao = descricao;
        return this;
    }

    public Optional<String> getDescricao() {
        return Optional.ofNullable(descricao);
    }

    public boolean temDescricao() {
        return descricao != null && !descricao.trim().isEmpty();
    }

    public boolean pertenceAoCurso(String curso) {
        return this.curso.equalsIgnoreCase(curso);
    }

    public boolean temNome(String nome) {
        return this.nome.equalsIgnoreCase(nome);
    }

    public boolean doPeriodo(Integer periodo) {
        return Objects.equals(this.periodo, periodo);
    }

    private String validarNome(String nome) {
        return Optional.ofNullable(nome)
            .filter(n -> !n.trim().isEmpty())
            .map(String::trim)
            .orElseThrow(() -> new IllegalArgumentException("Nome não pode ser nulo ou vazio"));
    }

    private String validarCurso(String curso) {
        return Optional.ofNullable(curso)
            .filter(c -> !c.trim().isEmpty())
            .map(String::trim)
            .orElseThrow(() -> new IllegalArgumentException("Curso não pode ser nulo ou vazio"));
    }

    private Integer validarPeriodo(Integer periodo) {
        return Optional.ofNullable(periodo)
            .filter(p -> p > 0 && p <= 10)
            .orElseThrow(() -> new IllegalArgumentException("Período deve estar entre 1 e 10"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Turma turma)) return false;
        return Objects.equals(id, turma.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("Turma{id=%d, nome='%s', curso='%s', periodo=%d}", 
            id, nome, curso, periodo);
    }
}