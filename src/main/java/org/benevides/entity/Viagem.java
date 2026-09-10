package org.benevides.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Viagem extends PanacheEntity {

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    @Column(name = "titulo", nullable = false, length = 100)
    public String titulo;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    @Column(name = "descricao", length = 255)
    public String descricao;

    @NotNull(message = "A data de início é obrigatória")
    @FutureOrPresent(message = "A data de início não pode ser no passado")
    @Column(name = "data_inicio", nullable = false)
    public LocalDate dataInicio;

    @NotNull(message = "A data de fim é obrigatória")
    @FutureOrPresent(message = "A data de fim não pode ser no passado")
    @Column(name = "data_fim", nullable = false)
    public LocalDate dataFim;

    @ManyToMany
    @JoinTable(
            name = "viagem_pessoa",
            joinColumns = @JoinColumn(name = "viagem_id"),
            inverseJoinColumns = @JoinColumn(name = "pessoa_id")
    )
    public List<Pessoa> pessoas = new ArrayList<>();;

    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Roteiro> roteiros = new ArrayList<>();;

    @Transient
    public int getQtdDias() {
        if (dataInicio == null || dataFim == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dataInicio, dataFim);
    }

    @Transient
    public Double getOrcamentoTotal() {
        if (roteiros == null || roteiros.isEmpty()) {
            return 0.0;
        }
        return roteiros.stream()
                .filter(r -> r.valor != null)
                .mapToDouble(r -> r.valor)
                .sum();
    }

    public void adicionarRoteiro(Roteiro roteiro) {
        roteiros.add(roteiro);
        roteiro.viagem = this; // Garante o vínculo do id da viagem no roteiro
    }

    public void removerRoteiro(Roteiro roteiro) {
        roteiros.remove(roteiro);
        roteiro.viagem = null;
    }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public List<Roteiro> getRoteiros() {
        return roteiros;
    }

    public void setRoteiros(List<Roteiro> roteiros) {
        this.roteiros = roteiros;
    }
}
