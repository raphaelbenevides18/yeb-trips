package org.benevides.health;

import org.benevides.service.CotacaoClient;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
public class ReadinessCheck implements HealthCheck {

    @RestClient
    CotacaoClient client;

    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Cotacao API External Check");

        try {

            var cotacao = client.buscarCotacaoBrlParaEur();

            if (cotacao != null && cotacao.getBrlEur() != null) {
                return responseBuilder.up()
                        .withData("apiStatus", "Disponível")
                        .withData("cotacaoAtual", cotacao.getBrlEur().getBid())
                        .build();
            } else {
                return responseBuilder.down()
                        .withData("apiStatus", "Resposta inválida da API")
                        .build();
            }
        } catch (Exception e) {
            // Se der timeout ou erro de conexão, marca o Readiness como DOWN
            return responseBuilder.down()
                    .withData("apiStatus", "Indisponível")
                    .withData("erro", e.getMessage())
                    .build();
        }
    }

}
