package org.benevides.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.Hibernate;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;



@DynamoDbBean
public class Viagem {

        private String id;

        @NotBlank(message = "O título é obrigatório")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
        private String titulo;

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        private String descricao;

        @NotNull(message = "A data de início é obrigatória")
        @FutureOrPresent(message = "A data de início não pode ser no passado")
        @JsonbDateFormat("yyyy-MM-dd")
        private LocalDate dataInicio;

        @NotNull(message = "A data de fim é obrigatória")
        @FutureOrPresent(message = "A data de fim não pode ser no passado")
        @JsonbDateFormat("yyyy-MM-dd")
        private LocalDate dataFim;

        // Em NoSQL, armazenamos a lista de IDs das pessoas associadas à viagem
        private List<String> pessoaIds = new ArrayList<>();

        // Mantidos para envio/recebimento nas respostas JSON
        private List<Pessoa> pessoas = new ArrayList<>();
        private List<Roteiro> roteiros = new ArrayList<>();

        public Viagem() {
        }

        @DynamoDbPartitionKey
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        @DynamoDbAttribute("data_inicio")
        @JsonbDateFormat("yyyy-MM-dd")
        public LocalDate getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(LocalDate dataInicio) {
            this.dataInicio = dataInicio;
        }

        @DynamoDbAttribute("data_fim")
        @JsonbDateFormat("yyyy-MM-dd")
        public LocalDate getDataFim() {
            return dataFim;
        }

        public void setDataFim(LocalDate dataFim) {
            this.dataFim = dataFim;
        }

        @DynamoDbAttribute("pessoa_ids")
        public List<String> getPessoaIds() {
            return pessoaIds;
        }

        public void setPessoaIds(List<String> pessoaIds) {
            this.pessoaIds = pessoaIds;
        }

        // Ignora a lista detalhada de pessoas do mapeamento do DynamoDB
        @DynamoDbIgnore
        public List<Pessoa> getPessoas() {
            return pessoas;
        }

        public void setPessoas(List<Pessoa> pessoas) {
            this.pessoas = pessoas;
        }

        // Ignora a lista detalhada de roteiros do mapeamento do DynamoDB
        @DynamoDbIgnore
        public List<Roteiro> getRoteiros() {
            return roteiros;
        }

        public void setRoteiros(List<Roteiro> roteiros) {
            this.roteiros = roteiros;
        }

        @DynamoDbIgnore
        public int getQtdDias() {
            if (dataInicio == null || dataFim == null) {
                return 0;
            }
            return (int) ChronoUnit.DAYS.between(dataInicio, dataFim);
        }

        @DynamoDbIgnore
        public Double getOrcamentoTotal() {
            if (roteiros == null || roteiros.isEmpty()) {
                return 0.0;
            }
            return roteiros.stream()
                    .filter(r -> r.getValor() != null)
                    .mapToDouble(Roteiro::getValor)
                    .sum();
        }
}
