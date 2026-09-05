package org.benevides.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "pessoa")
public class Pessoa extends PanacheEntity {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    public String nome;

    //@NotNull(message = "O sexo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 20)
    public Sexo sexo;

    @NotBlank(message = "O CPF é obrigatório")
    //@Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    public String cpf;

    //@NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data no passado")
    @Column(name = "data_nascimento", nullable = false)
    public LocalDate dataNascimento;

    // Métodos utilitários convenientes no padrão Active Record do Panache
    public static Pessoa findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }

    public static boolean deleteByCpf(String cpf) {
        return delete("cpf", cpf) > 0;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
