package org.benevides.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Viagem extends PanacheEntity {

    public String titulo;
    public String descricao;
    public LocalDate dataInicio;
    public LocalDate dataFim;
    public Double orcamentoTotal;
    public int qtdDias;

    @ManyToMany
    @JoinTable(
            name = "viagem_pessoa",
            joinColumns = @JoinColumn(name = "viagem_id"),
            inverseJoinColumns = @JoinColumn(name = "pessoa_id")
    )
    public List<Pessoa> pessoas;

    @OneToMany(mappedBy = "viagem", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Roteiro> roteiros;


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

    public Double getOrcamentoTotal() {
        return orcamentoTotal;
    }

    public void setOrcamentoTotal(Double orcamentoTotal) {
        this.orcamentoTotal = orcamentoTotal;
    }

    public int getQtdDias() {
        return qtdDias;
    }

    public void setQtdDias(int qtdDias) {
        this.qtdDias = qtdDias;
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
