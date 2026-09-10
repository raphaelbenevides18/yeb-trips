package org.benevides.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.benevides.entity.Viagem;

import java.util.List;

@Path("viagem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ViagemResource {

    // 1. LISTAR TODAS AS VIAGENS (GET)
    @GET
    public List<Viagem> listarTodas() {
        return Viagem.listAll();
    }
}
