package org.benevides.rest;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.benevides.entity.Roteiro;

import java.util.List;

@Path("roteiro")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoteiroResource {

    @GET
    public List<Roteiro> getRoteiro() {

        return Roteiro.listAll();
    }


}
