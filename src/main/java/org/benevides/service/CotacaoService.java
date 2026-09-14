package org.benevides.service;


import jakarta.enterprise.context.ApplicationScoped;
import org.benevides.entity.Response.CotacaoResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;

@ApplicationScoped
public class CotacaoService {

    @RestClient
    CotacaoClient cotacaoClient;

    public BigDecimal converterReaisParaEuros(BigDecimal valorEmReais) {

        CotacaoResponse response = cotacaoClient.buscarCotacaoBrlParaEur();
        BigDecimal taxa = new BigDecimal(response.getBrlEur().getBid());
        return valorEmReais.multiply(taxa);
    }
}
