package org.benevides.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoteiroResourceTest {

    private static Long viagemId;
    private static Long roteiroId;

    private String gerarToken(String username, String... roles) {
        return Jwt.issuer("https://org.benevides.jwt/issuer")
                .upn(username)
                .groups(Set.of(roles))
                .sign();
    }

    @Test
    @Order(1)
    @DisplayName("Criar viagem de suporte para vincular os roteiros")
    public void setupViagem() {
        String token = gerarToken("admin", "ADMIN");

        String viagemJson = """
        {
            "titulo": "Expedição Atacama",
            "descricao": "Viagem pelo deserto",
            "dataInicio": "2026-11-10",
            "dataFim": "2026-11-20"
        }
        """;

        Number idExtraido = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(viagemJson)
                .when().post("/api/yebTrips/viagem")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("titulo", equalTo("Expedição Atacama"))
                .extract().path("id");

        viagemId = idExtraido.longValue();
    }

    @Test
    @Order(1)
    @DisplayName("Deve cadastrar novo roteiro associado à viagem como ADMIN")
    public void testCriarRoteiro() {
        String token = gerarToken("admin", "ADMIN");

        String roteiroJson = """
        {
            "nomeAtividade": "Passeio pelo Valle de la Luna",
            "descricaoAtividade": "Caminhada nas formações rochosas",
            "valor": 250.0,
            "data":"2026-09-17"
        }
        """;
        Number idExtraido = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("viagemId", viagemId)
                .body(roteiroJson)
                .when().post("/api/yebTrips/roteiro/viagem/{viagemId}")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().path("id");

        roteiroId = idExtraido.longValue();
    }

    @Test
    @Order(3)
    @DisplayName("Deve listar todos os roteiros como USER (200)")
    public void testListarRoteiros() {
        String token = gerarToken("leitor", "USER");

        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/yebTrips/roteiro")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar o roteiro como ADMIN (200)")
    public void testAtualizarRoteiro() {
        String token = gerarToken("admin", "ADMIN");

//        Roteiro roteiroAtualizado = new Roteiro();
//        roteiroAtualizado.nomeAtividade = "Mergulho VIP";
//        roteiroAtualizado.descricaoAtividade = "Atividade de aventura com fotos";
//        roteiroAtualizado.valor = 450.00;
//        roteiroAtualizado.data = LocalDate.now().plusDays(1);

        String roteiroAtualizadoJson = """
        {
            "nomeAtividade": "Mergulho VIP",
            "descricaoAtividade": "Mergulho irado",
            "valor": 450.0,
            "data":"2026-09-17"
        }
        """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", roteiroId)
                .body(roteiroAtualizadoJson)
                .when().put("/api/yebTrips/roteiro/{id}")
                .then()
                .statusCode(200)
                .body("nomeAtividade", equalTo("Mergulho VIP"));
    }

    @Test
    @Order(5)
    @DisplayName("Deve deletar o roteiro como ADMIN (204)")
    public void testDeletarRoteiro() {
        String token = gerarToken("admin", "ADMIN");

        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", roteiroId)
                .when().delete("/api/yebTrips/roteiro/{id}")
                .then()
                .statusCode(204);
    }
}
