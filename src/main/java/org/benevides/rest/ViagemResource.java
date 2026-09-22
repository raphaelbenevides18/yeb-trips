package org.benevides.rest;

import io.quarkus.panache.common.Page;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Pessoa;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;
import org.benevides.repository.PessoaRepository;
import org.benevides.repository.RoteiroRepository;
import org.benevides.repository.ViagemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("viagem")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ViagemResource {

    @Inject
    ViagemRepository viagemRepository;

    @Inject
    RoteiroRepository roteiroRepository;

    @Inject
    PessoaRepository pessoaRepository;


    private void carregarRelacionamentos(Viagem viagem) {
        if (viagem == null) return;

        // 1. Carrega os Roteiros associados via GSI
        viagem.setRoteiros(roteiroRepository.findByViagemId(viagem.getId()));

        // 2. Carrega as Pessoas a partir dos IDs salvos no DynamoDB
        if (viagem.getPessoaIds() != null && !viagem.getPessoaIds().isEmpty()) {
            List<Pessoa> pessoasCarregadas = new ArrayList<>();
            for (String pessoaId : viagem.getPessoaIds()) {
                Pessoa p = pessoaRepository.findById(pessoaId);
                if (p != null) {
                    pessoasCarregadas.add(p);
                }
            }
            viagem.setPessoas(pessoasCarregadas);
        }
    }
    @GET
    @RolesAllowed({"USER", "ADMIN"})
    public List<Viagem> listarTodas(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size) {
        List<Viagem> viagens = viagemRepository.listAll();

        // Aplica paginação manual em memória (se necessário) e carrega os relacionamentos
        int fromIndex = Math.min(page * size, viagens.size());
        int toIndex = Math.min(fromIndex + size, viagens.size());
        List<Viagem> paginadas = viagens.subList(fromIndex, toIndex);

        for (Viagem v : paginadas) {
            carregarRelacionamentos(v);
        }

        return paginadas;
    }

    // 2. BUSCAR VIAGEM POR ID
    @GET
    @Path("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public Response buscarPorId(@PathParam("id") String id) {
        Viagem viagem = viagemRepository.findById(id);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }

        carregarRelacionamentos(viagem);
        return Response.ok(viagem).build();
    }

    // 3. CRIAR VIAGEM
    @POST
    @RolesAllowed({"ADMIN"})
    public Response criar(@Valid Viagem viagem) {
        // Gera ID único para a Viagem caso não possua
        if (viagem.getId() == null) {
            viagem.setId(UUID.randomUUID().toString());
        }

        // 1. Tratamento e Validação das Pessoas pelo CPF
        if (viagem.getPessoas() != null && !viagem.getPessoas().isEmpty()) {
            List<String> pessoaIds = new ArrayList<>();

            for (Pessoa p : viagem.getPessoas()) {
                if (p.getCpf() == null || p.getCpf().isBlank()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("É necessário informar o CPF de todas as pessoas vinculadas.")
                            .build();
                }

                // Busca a pessoa no DynamoDB pelo CPF
                Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(p.getCpf());

                if (pessoaExistente.isEmpty()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("A pessoa com o CPF '" + p.getCpf() + "' não está cadastrada no sistema.")
                            .build();
                }

                pessoaIds.add(pessoaExistente.get().getId());
            }

            viagem.setPessoaIds(pessoaIds);
        }

        // 2. Persiste a Viagem no DynamoDB
        viagemRepository.persist(viagem);

        // 3. Tratamento dos Roteiros
        if (viagem.getRoteiros() != null && !viagem.getRoteiros().isEmpty()) {
            for (Roteiro roteiro : viagem.getRoteiros()) {
                if (roteiro.getId() == null) {
                    roteiro.setId(UUID.randomUUID().toString());
                }
                roteiro.setViagemId(viagem.getId());
                roteiroRepository.persist(roteiro);
            }
        }

        carregarRelacionamentos(viagem);
        return Response.status(Response.Status.CREATED).entity(viagem).build();
    }

    // 4. ATUALIZAR VIAGEM
    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response atualizar(@PathParam("id") String id, @Valid Viagem viagemAtualizada) {
        Viagem viagemExistente = viagemRepository.findById(id);
        if (viagemExistente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }

        // Atualiza os campos básicos
        viagemExistente.setTitulo(viagemAtualizada.getTitulo());
        viagemExistente.setDescricao(viagemAtualizada.getDescricao());
        viagemExistente.setDataInicio(viagemAtualizada.getDataInicio());
        viagemExistente.setDataFim(viagemAtualizada.getDataFim());

        viagemRepository.persist(viagemExistente);

        carregarRelacionamentos(viagemExistente);
        return Response.ok(viagemExistente).build();
    }

    // 5. DELETAR VIAGEM (Com exclusão manual em cascata dos roteiros)
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    public Response deletar(@PathParam("id") String id) {
        boolean deletado = viagemRepository.deleteById(id);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + id + " não encontrada.")
                    .build();
        }

        // Simula o CascadeType.ALL limpando os Roteiros associados
        List<Roteiro> roteiros = roteiroRepository.findByViagemId(id);
        for (Roteiro r : roteiros) {
            roteiroRepository.deleteById(r.getId());
        }

        return Response.noContent().build();
    }

    // 6. ADICIONAR PESSOA À VIAGEM VIA CPF
    @POST
    @Path("/{viagemId}/pessoas/{cpf}")
    @RolesAllowed({"ADMIN"})
    public Response adicionarPessoa(@PathParam("viagemId") String viagemId, @PathParam("cpf") String cpf) {
        Viagem viagem = viagemRepository.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        Optional<Pessoa> pessoaOpt = pessoaRepository.findByCpf(cpf);
        if (pessoaOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com CPF '" + cpf + "' não encontrada.")
                    .build();
        }

        Pessoa pessoa = pessoaOpt.get();

        if (viagem.getPessoaIds().contains(pessoa.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Pessoa com CPF '" + cpf + "' já está vinculada a esta viagem.")
                    .build();
        }

        viagem.getPessoaIds().add(pessoa.getId());
        viagemRepository.persist(viagem);

        carregarRelacionamentos(viagem);
        return Response.ok(viagem).build();
    }

    // 7. REMOVER PESSOA DA VIAGEM VIA CPF
    @DELETE
    @Path("/{viagemId}/pessoas/{cpf}")
    @RolesAllowed({"ADMIN"})
    public Response removerPessoa(@PathParam("viagemId") String viagemId, @PathParam("cpf") String cpf) {
        Viagem viagem = viagemRepository.findById(viagemId);
        if (viagem == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Viagem com ID " + viagemId + " não encontrada.")
                    .build();
        }

        Optional<Pessoa> pessoaOpt = pessoaRepository.findByCpf(cpf);
        if (pessoaOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com CPF '" + cpf + "' não encontrada.")
                    .build();
        }

        Pessoa pessoa = pessoaOpt.get();

        if (!viagem.getPessoaIds().contains(pessoa.getId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("A pessoa com CPF '" + cpf + "' não está vinculada a esta viagem.")
                    .build();
        }

        viagem.getPessoaIds().remove(pessoa.getId());
        viagemRepository.persist(viagem);

        carregarRelacionamentos(viagem);
        return Response.ok(viagem).build();
    }
}
