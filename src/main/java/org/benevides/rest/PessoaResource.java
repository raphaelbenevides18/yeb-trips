package org.benevides.rest;

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
    public List<Pessoa> getPessoa() {
        return Pessoa.listAll();
    }

    @GET
    @Path("findByCpf")
    public Pessoa findByCpf(@QueryParam("cpf") String cpf) {
        return Pessoa.findByCpf(cpf);
    }


    @POST
    @Transactional
    public Pessoa createPessoa(@Valid Pessoa pessoa){
        pessoa.id = null;
        pessoa.persist();

        return pessoa;

    }
    @PUT
    @Transactional
    public Pessoa updatePessoa(@Valid Pessoa pessoa) {
        Pessoa p = Pessoa.findById(pessoa.id);
        p.nome = pessoa.nome;
        p.cpf = pessoa.cpf;
        p.sexo = pessoa.sexo;
        p.dataNascimento = pessoa.dataNascimento;
        p.persist();

        return p;
    }

    @DELETE
    @Path("/{cpf}")
    @Transactional
    public Response deletePessoa(@PathParam("cpf") String cpf) {
        boolean deletado = Pessoa.deleteByCpf(cpf);

        if (deletado) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }



}
