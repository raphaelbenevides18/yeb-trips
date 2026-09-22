package org.benevides.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Pessoa;
import org.benevides.repository.PessoaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Path("pessoa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @Inject
    PessoaRepository pessoaRepository;

    @GET
    //@Counted(value = "counted.getPessoa")
    @RolesAllowed({"ADMIN", "USER"})
    public List<Pessoa> getPessoa() {

        return pessoaRepository.listAll();
    }

    @GET
    @Path("findByCpf/{cpf}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response findByCpf(@PathParam("cpf") String cpf) {
        Optional<Pessoa> pessoa = pessoaRepository.findByCpf(cpf);

        return pessoa.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }


    @POST
    @RolesAllowed({"ADMIN"})
    public Response createPessoa(@Valid Pessoa pessoa){
        if (pessoa.getId() == null) {
            pessoa.setId(UUID.randomUUID().toString()); // Gera chave única para o DynamoDB
        }
        pessoaRepository.persist(pessoa);
        return Response.status(Response.Status.CREATED).entity(pessoa).build();
    }

    @PUT
    @Path("/{cpf}")
    @RolesAllowed({"ADMIN"})
    public Response updatePessoa(@PathParam("cpf") String cpf, @Valid Pessoa pessoa) {

        Optional<Pessoa> p = pessoaRepository.findByCpf(cpf);

        if (p.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com cpf " + cpf + " não encontrada.")
                    .build();
        }
        Pessoa pessoaExistente = p.get();
        pessoaExistente.setNome(pessoa.getNome());
        pessoaExistente.setSexo(pessoa.getSexo());
        pessoaExistente.setDataNascimento(pessoa.getDataNascimento());


        pessoaRepository.persist(pessoaExistente);

        return Response.ok(pessoaExistente).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response deletePessoa(@PathParam("cpf") String cpf) {
        boolean deletado = pessoaRepository.deleteByCpf(cpf);
        if (!deletado) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }



}
