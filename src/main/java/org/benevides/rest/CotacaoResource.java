package org.benevides.rest;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Response.ConversaoResponse;
import org.benevides.service.CotacaoService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Path("cotacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CotacaoResource {

    @Inject
    CotacaoService service;

    @GET
    @Path("/converter/{valor}")
    public Response getCotacaoAtual(@PathParam("valor") BigDecimal valorEmReais){
        // Chama o serviço para fazer a conversão via API externa
        BigDecimal valorConvertido = service.converterReaisParaEuros(valorEmReais);

        // Arredonda para 2 casas decimais (padrão de moeda)
        valorConvertido = valorConvertido.setScale(2, RoundingMode.HALF_UP);
        ConversaoResponse resultado = new ConversaoResponse(valorEmReais, valorConvertido);

        return Response.ok(resultado).build();


    }
}
