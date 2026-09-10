package org.benevides.rest;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;

import java.util.List;

@Path("roteiro")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoteiroResource {

    @GET
    public List<Roteiro> getRoteiro() {

        return Roteiro.listAll();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Roteiro roteiro = Roteiro.findById(id);
        if (roteiro == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro com ID " + id + " não encontrado.")
                    .build();
        }
        return Response.ok(roteiro).build();
    }

    @POST
    @Path("/viagem/{viagemId}")
    @Transactional
    public Response criarRoteiro(@PathParam("viagemId") Long viagemId, @Valid Roteiro roteiro) {
        Viagem viagem = Viagem.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Não foi possível criar o roteiro. Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        roteiro.viagem = viagem; // Estabelece a chave estrangeira (viagem_id)
        roteiro.persist();

        return Response.status(Response.Status.CREATED)
                .entity(roteiro)
                .build();
    }

    // 4. ATUALIZAR ROTEIRO EXISTENTE
    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizarRoteiro(@PathParam("id") Long id, @Valid Roteiro roteiroAtualizado) {
        Roteiro roteiroExistente = Roteiro.findById(id);
        if (roteiroExistente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro com ID " + id + " não encontrado.")
                    .build();
        }

        // Atualiza os campos do Roteiro
        roteiroExistente.data = roteiroAtualizado.data;
        roteiroExistente.nomeAtividade = roteiroAtualizado.nomeAtividade;
        roteiroExistente.descricaoAtividade = roteiroAtualizado.descricaoAtividade;
        roteiroExistente.valor = roteiroAtualizado.valor;

        return Response.ok(roteiroExistente).build();
    }

    // 5. DELETAR ROTEIRO
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Roteiro.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro com ID " + id + " não encontrado.")
                    .build();
        }
        return Response.noContent().build();
    }

    // 6. LISTAR ROTEIROS DE UMA VIAGEM ESPECÍFICA
    @GET
    @Path("/viagem/{viagemId}")
    public Response listarPorViagem(@PathParam("viagemId") Long viagemId) {
        Viagem viagem = Viagem.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        List<Roteiro> roteiros = Roteiro.list("viagem", viagem);
        return Response.ok(roteiros).build();
    }



}
