package org.benevides.rest;

import io.quarkus.panache.common.Page;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Pessoa;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;

import java.util.ArrayList;
import java.util.List;

@Path("viagem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ViagemResource {

    // 1. LISTAR TODAS AS VIAGENS (GET)
    @GET
    public List<Viagem> listarTodas(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size) {
        return Viagem.findAll().page(Page.of(page, size)).list();
    }

    // 2. BUSCAR VIAGEM POR ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Viagem viagem = Viagem.findById(id);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }
        return Response.ok(viagem).build();
    }

    @POST
    @Transactional
    public Response criar(@Valid Viagem viagem) {
        // 1. Tratamento e Validação das Pessoas pelo CPF
        if (viagem.pessoas != null && !viagem.pessoas.isEmpty()) {
            List<Pessoa> pessoasEncontradas = new ArrayList<>();

            for (Pessoa p : viagem.pessoas) {
                if (p.cpf == null || p.cpf.isBlank()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("É necessário informar o CPF de todas as pessoas vinculadas.")
                            .build();
                }

                // Busca a pessoa no banco de dados pelo CPF
                Pessoa pessoaExistente = Pessoa.findByCpf(p.cpf);

                if (pessoaExistente == null) {
                    // Aborta o salvamento e avisa qual CPF não existe
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("A pessoa com o CPF '" + p.cpf + "' não está cadastrada no sistema.")
                            .build();
                }

                pessoasEncontradas.add(pessoaExistente);
            }

            // Substitui a lista com as entidades gerenciadas pelo Hibernate
            viagem.pessoas = pessoasEncontradas;
        }

        // 2. Tratamento dos Roteiros: Garante o vínculo bidirecional
        if (viagem.roteiros != null && !viagem.roteiros.isEmpty()) {
            for (Roteiro roteiro : viagem.roteiros) {
                roteiro.viagem = viagem;
            }
        }

        // 3. Persiste a Viagem no banco de dados
        viagem.persist();

        return Response.status(Response.Status.CREATED).entity(viagem).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid Viagem viagemAtualizada) {
        Viagem viagemExistente = Viagem.findById(id);
        if (viagemExistente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }

        // Atualiza os campos básicos
        viagemExistente.titulo = viagemAtualizada.titulo;
        viagemExistente.descricao = viagemAtualizada.descricao;
        viagemExistente.dataInicio = viagemAtualizada.dataInicio;
        viagemExistente.dataFim = viagemAtualizada.dataFim;

        if (viagemExistente.pessoas != null) {
            viagemExistente.pessoas.size();
        }
        if (viagemExistente.roteiros != null) {
            viagemExistente.roteiros.size();
        }

        return Response.ok(viagemExistente).build();
    }

    // 5. DELETAR VIAGEM (Deleta em cascata os roteiros vinculados devido ao CascadeType.ALL)
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deletar(@PathParam("id") Long id) {
        boolean deletado = Viagem.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/{viagemId}/pessoas/{cpf}")
    @Transactional
    public Response adicionarPessoa(@PathParam("viagemId") Long viagemId, @PathParam("cpf") String cpf) {
        Viagem viagem = Viagem.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        Pessoa pessoa = Pessoa.findByCpf(cpf);
        if (pessoa == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com CPF '" + cpf + "' não encontrada.")
                    .build();
        }

        if (!viagem.pessoas.contains(pessoa)) {
            viagem.pessoas.add(pessoa);
        }else{

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Pessoa com CPF '" + cpf + "' já está vinculada a esta viagem.")
                    .build();

        }

        if (viagem.pessoas != null) viagem.pessoas.size();
        if (viagem.roteiros != null) viagem.roteiros.size();

        return Response.ok(viagem).build();
    }

    @DELETE
    @Path("/{viagemId}/pessoas/{cpf}")
    @Transactional
    public Response removerPessoa(@PathParam("viagemId") Long viagemId, @PathParam("cpf") String cpf) {
        Viagem viagem = Viagem.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        Pessoa pessoa = Pessoa.findByCpf(cpf);
        if (pessoa == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com CPF '" + cpf + "' não encontrada.")
                    .build();
        }

        if (!viagem.pessoas.contains(pessoa)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("A pessoa com CPF '" + cpf + "' não está vinculada a esta viagem.")
                    .build();
        }

        viagem.pessoas.remove(pessoa);
        if (viagem.pessoas != null) viagem.pessoas.size();
        if (viagem.roteiros != null) viagem.roteiros.size();

        return Response.ok(viagem).build();
    }
}
