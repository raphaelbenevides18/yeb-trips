package org.benevides.entity.Response;

import java.math.BigDecimal;

public class ConversaoResponse {

    public String moedaOrigem = "BRL";
    public String moedaDestino = "EUR";
    public BigDecimal valorOriginal;
    public BigDecimal valorConvertido;


    public ConversaoResponse(BigDecimal valorOriginal, BigDecimal valorConvertido) {
        this.valorOriginal = valorOriginal;
        this.valorConvertido = valorConvertido;
    }


    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorConvertido() {
        return valorConvertido;
    }

    public void setValorConvertido(BigDecimal valorConvertido) {
        this.valorConvertido = valorConvertido;
    }
}
