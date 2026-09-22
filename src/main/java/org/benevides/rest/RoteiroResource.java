package org.benevides.rest;


import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;
import org.benevides.repository.RoteiroRepository;
import org.benevides.repository.ViagemRepository;

import java.util.List;
import java.util.UUID;

@Path("roteiro")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoteiroResource {

    @Inject
    RoteiroRepository roteiroRepository;

    @Inject
    ViagemRepository viagemRepository;


    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public List<Roteiro> getRoteiro() {

        return roteiroRepository.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response buscarPorId(@PathParam("id") String id) {
        Roteiro roteiro = roteiroRepository.findById(id);
        if (roteiro == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro não encontrado para o ID: " + id)
                    .build();
        }
        return Response.ok(roteiro).build();
    }

    @POST
    @Path("/viagem/{viagemId}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response criarRoteiro(@PathParam("viagemId") String viagemId, @Valid Roteiro roteiro) {
        // 1. Verifica se a viagem existe usando o ViagemRepository
        Viagem viagem = viagemRepository.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Não foi possível criar o roteiro. Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        // 2. Associa a chave/ID da viagem ao roteiro
        roteiro.setViagemId(viagemId);

        // 3. Garante que o Roteiro tenha um ID único (UUID)
        if (roteiro.getId() == null) {
            roteiro.setId(UUID.randomUUID().toString());
        }

        // 4. Salva no DynamoDB via RoteiroRepository
        roteiroRepository.persist(roteiro);

        return Response.status(Response.Status.CREATED)
                .entity(roteiro)
                .build();
    }

    // 4. ATUALIZAR ROTEIRO EXISTENTE
    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response atualizarRoteiro(@PathParam("id") String id, @Valid Roteiro roteiroAtualizado) {
        Roteiro roteiroExistente = roteiroRepository.findById(id);

        if (roteiroExistente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro com ID " + id + " não encontrado.")
                    .build();
        }

        roteiroExistente.setData(roteiroAtualizado.getData());
        roteiroExistente.setNomeAtividade(roteiroAtualizado.getNomeAtividade());
        roteiroExistente.setDescricaoAtividade(roteiroAtualizado.getDescricaoAtividade());
        roteiroExistente.setValor(roteiroAtualizado.getValor());
        roteiroExistente.setViagemId(roteiroAtualizado.getViagemId());

        roteiroRepository.persist(roteiroExistente);

        return Response.ok(roteiroExistente).build();
    }

    // 5. DELETAR ROTEIRO
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deletar(@PathParam("id") String id) {
        boolean deletado = roteiroRepository.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Roteiro com ID " + id + " não encontrado.")
                    .build();
        }
        return Response.noContent().build();
    }


    @GET
    @Path("/viagem/{viagemId}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response listarPorViagem(@PathParam("viagemId") String viagemId) {
        Viagem viagem = viagemRepository.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        // 2. Busca os roteiros vinculados a essa viagem via GSI no DynamoDB
        List<Roteiro> roteiros = roteiroRepository.findByViagemId(viagemId);
        return Response.ok(roteiros).build();
    }



}
