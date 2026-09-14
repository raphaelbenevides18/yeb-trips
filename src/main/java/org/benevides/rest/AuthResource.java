package org.benevides.rest;

import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final String ISSUER = "https://org.benevides.jwt/issuer";

    // Endpoint para gerar o Token Leitor (Role USER)
    @GET
    @Path("/token/user")
    @PermitAll
    public Response gerarTokenUser() {
        String token = Jwt.issuer(ISSUER)
                .upn("usuario@benevides.org")
                .groups(new HashSet<>(Arrays.asList("USER")))
                .expiresIn(3600) // 1 hora
                .sign();

        return Response.ok(Map.of("token", token, "role", "USER")).build();
    }

    // Endpoint para gerar o Token Admin (Role ADMIN + USER)
    @GET
    @Path("/token/admin")
    @PermitAll
    public Response gerarTokenAdmin() {
        String token = Jwt.issuer(ISSUER)
                .upn("admin@benevides.org")
                .groups(new HashSet<>(Arrays.asList("ADMIN", "USER")))
                .expiresIn(3600) // 1 hora
                .sign();

        return Response.ok(Map.of("token", token, "role", "ADMIN")).build();
    }
}
