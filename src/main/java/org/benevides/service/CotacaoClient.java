package org.benevides.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.benevides.entity.Response.CotacaoResponse;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.time.temporal.ChronoUnit;

@Path("/last")
@RegisterRestClient(configKey = "awesome-api")
public interface CotacaoClient {

    @GET
    @Path("/BRL-EUR")
    @Timeout(value = 5, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "buscarCotacaoFallback")
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 10000,
            successThreshold = 2
    )
    CotacaoResponse buscarCotacaoBrlParaEur();

    default CotacaoResponse buscarCotacaoFallback() {
        CotacaoResponse response = new CotacaoResponse();
        CotacaoResponse.CotacaoDetalhe detalhe = new CotacaoResponse.CotacaoDetalhe();
        detalhe.setBid("0.18");
        detalhe.setCode("BRL");
        detalhe.setCodein("EUR");
        response.setBrlEur(detalhe);
        return response;
    }
}
