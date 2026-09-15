package org.benevides.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.benevides.entity.Pessoa;

import java.util.List;


@Path("pessoa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {

    @GET
    //@Counted(value = "counted.getPessoa")
    @RolesAllowed({"ADMIN", "USER"})
    public List<Pessoa> getPessoa() {
        return Pessoa.listAll();
    }

    @GET
    @Path("findByCpf/{cpf}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response findByCpf(@PathParam("cpf") String cpf) {
        Pessoa pessoa = Pessoa.findByCpf(cpf);

        if (pessoa == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com cpf " + cpf + " não encontrado.")
                    .build();
        }
        return Response.ok(pessoa).build();
    }


    @POST
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response createPessoa(@Valid Pessoa pessoa){
        pessoa.id = null;
        pessoa.persist();

        return Response.status(Response.Status.CREATED)
                .entity(pessoa)
                .build();
    }

    @PUT
    @Path("/{cpf}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response updatePessoa(@PathParam("cpf") String cpf, @Valid Pessoa pessoa) {

        Pessoa p = Pessoa.findByCpf(cpf);

        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Pessoa com cpf " + cpf + " não encontrado.")
                    .build();
        }

        p.nome = pessoa.nome;
        p.sexo = pessoa.sexo;
        p.dataNascimento = pessoa.dataNascimento;
        p.persist();

        return Response.ok(p).build();
    }

    @DELETE
    @Path("/{cpf}")
    @Transactional
    @RolesAllowed({"ADMIN"})
    public Response deletePessoa(@PathParam("cpf") String cpf) {
        boolean deletado = Pessoa.deleteByCpf(cpf);

        if (deletado) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



}
