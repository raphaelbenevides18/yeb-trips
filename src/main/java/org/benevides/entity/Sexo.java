package org.benevides.entity;

public enum Sexo {

    MASCULINO("MASCULINO"), FEMININO("FEMININO"), OUTRO("OUTRO"), NAO_INFORMAR("Não Informado");

    private final String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
