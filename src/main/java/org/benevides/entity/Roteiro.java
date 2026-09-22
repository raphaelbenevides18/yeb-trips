package org.benevides.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDate;

@DynamoDbBean
public class Roteiro{

    private String id;

    @NotNull(message = "A data da atividade é obrigatória")
    @Future(message = "A data da atividade deve ser uma data futura")
    private LocalDate data;

    @NotBlank(message = "O nome da atividade é obrigatório")
    @Size(min = 2, max = 40, message = "O nome da atividade deve ter entre 2 e 40 caracteres")
    private String nomeAtividade;

    @Size(max = 150, message = "A descrição deve ter até 150 caracteres")
    private String descricaoAtividade;

    @NotNull(message = "O valor é obrigatório")
    @PositiveOrZero(message = "O valor não pode ser negativo")
    private Double valor;

    // Referência à Viagem via ID (Chave Estrangeira lógica no NoSQL)
    private String viagemId;

    public Roteiro() {
    }

    public Roteiro(String id, LocalDate data, String nomeAtividade, String descricaoAtividade, Double valor, String viagemId) {
        this.id = id;
        this.data = data;
        this.nomeAtividade = nomeAtividade;
        this.descricaoAtividade = descricaoAtividade;
        this.valor = valor;
        this.viagemId = viagemId;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    @DynamoDbAttribute("nome_atividade")
    public String getNomeAtividade() {
        return nomeAtividade;
    }

    public void setNomeAtividade(String nomeAtividade) {
        this.nomeAtividade = nomeAtividade;
    }

    @DynamoDbAttribute("descricao_atividade")
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

    @DynamoDbSecondaryPartitionKey(indexNames = "viagemId-index")
    @DynamoDbAttribute("viagem_id")
    public String getViagemId() {
        return viagemId;
    }

    public void setViagemId(String viagemId) {
        this.viagemId = viagemId;
    }
}
