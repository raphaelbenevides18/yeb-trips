package org.benevides.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Roteiro extends PanacheEntity {

    public LocalDate data;
    public String nomeAtividade;
    public String descricaoAtividade;
    public Double valor;

    // Chave estrangeira no banco de dados que aponta para Viagem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id")
    @JsonIgnore // Evita loop infinito no JSON (Viagem -> Roteiro -> Viagem...)
    public Viagem viagem;
}
