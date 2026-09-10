package org.benevides.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "roteiro")
public class Roteiro extends PanacheEntity {

    @NotNull(message = "A data da atividade é obrigatória")
    @Future(message = "A data da atividade deve ser uma data futura")
    @Column(name = "data", nullable = false)
    public LocalDate data;

    @NotBlank(message = "O nome da atividade é obrigatório")
    @Size(min = 2, max = 40, message = "O nome da atividade deve ter entre 2 e 40 caracteres")
    @Column(name = "nome_atividade", nullable = false, length = 40)
    public String nomeAtividade;

    @Size(max = 150, message = "A descrição deve ter até 150 caracteres")
    @Column(name = "descricao_atividade", length = 150)
    public String descricaoAtividade;

    @NotNull(message = "O valor é obrigatório")
    @PositiveOrZero(message = "O valor não pode ser negativo")
    @Column(name = "valor", nullable = false)
    public Double valor;

    // Chave estrangeira no banco de dados que aponta para Viagem
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id")
    @JsonbTransient // Evita loop infinito no JSON (Viagem -> Roteiro -> Viagem...)
    public Viagem viagem;


    public Roteiro() {
    }


    public Roteiro(LocalDate data, String nomeAtividade, String descricaoAtividade, Double valor, Viagem viagem) {
        this.data = data;
        this.nomeAtividade = nomeAtividade;
        this.descricaoAtividade = descricaoAtividade;
        this.valor = valor;
        this.viagem = viagem;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }

    public String getDescricaoAtividade() {
        return descricaoAtividade;
    }

    public void setDescricaoAtividade(String descricaoAtividade) {
        this.descricaoAtividade = descricaoAtividade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
    @JsonbTransient
    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }
}
