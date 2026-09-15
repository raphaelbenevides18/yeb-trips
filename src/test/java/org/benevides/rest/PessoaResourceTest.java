package org.benevides.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import org.benevides.entity.Pessoa;
import org.benevides.entity.Sexo;
import org.junit.jupiter.api.*;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PessoaResourceTest {

    private static final String CPF_TESTE = "11122233344";

    private String gerarToken(String username, String... roles) {
        return Jwt.issuer("https://org.benevides.jwt/issuer")
                .upn(username)
                .groups(Set.of(roles))
                .sign();
    }

    @Test
    @Order(1)
    @DisplayName("Deve bloquear listagem sem token (401)")
    public void testSemToken() {
        given()
                .when().get("/api/yebTrips/pessoa")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(2)
    @DisplayName("Deve proibir criação de pessoa por perfil USER (403)")
    public void testCriarComoUser() {
        String token = gerarToken("leitor", "USER");

        Pessoa p = new Pessoa();
        p.cpf = "99900011122";
        p.nome = "Teste User";

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(p)
                .when().post("/api/yebTrips/pessoa")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(3)
    @DisplayName("Deve cadastrar nova pessoa como ADMIN (201)")
    public void testCriarComoAdmin() {
        String token = gerarToken("admin", "ADMIN");


                String pessoaJson = """
        {
            "cpf": "11122233344",
            "nome": "Maria Oliveira",
            "sexo":"FEMININO",
            "dataNascimento":"1991-09-15"
        }
        """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(pessoaJson)
                .when().post("/api/yebTrips/pessoa")
                .then()
                .statusCode(201)
                .body("cpf", equalTo(CPF_TESTE))
                .body("nome", equalTo("Maria Oliveira"));
    }

    @Test
    @Order(4)
    @DisplayName("Deve listar pessoas como USER (200)")
    public void testListarComoUser() {
        String token = gerarToken("leitor", "USER");

        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/yebTrips/pessoa")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(5)
    @DisplayName("Deve buscar pessoa por CPF como USER (200)")
    public void testBuscarPorCpf() {
        String token = gerarToken("leitor", "USER");

        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("cpf", CPF_TESTE)
                .when().get("/api/yebTrips/pessoa/findByCpf/{cpf}")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Maria Oliveira"));
    }

    @Test
    @Order(6)
    @DisplayName("Deve atualizar dados da pessoa como ADMIN (200)")
    public void testAtualizarPessoa() {
        String token = gerarToken("admin", "ADMIN");

        String pessoaJson = """
        {
            "cpf": "11122233344",
            "nome": "Maria Oliveira da Silva",
            "sexo":"FEMININO",
            "dataNascimento":"1991-09-15"
        }
        """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("cpf", CPF_TESTE)
                .body(pessoaJson)
                .when().put("/api/yebTrips/pessoa/{cpf}")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Maria Oliveira da Silva"));
    }

    @Test
    @Order(7)
    @DisplayName("Deve excluir pessoa como ADMIN (204)")
    public void testDeletarPessoa() {
        String token = gerarToken("admin", "ADMIN");

        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("cpf", CPF_TESTE)
                .when().delete("/api/yebTrips/pessoa/{cpf}")
                .then()
                .statusCode(204);
    }
}