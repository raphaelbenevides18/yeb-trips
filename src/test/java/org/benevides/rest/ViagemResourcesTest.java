package org.benevides.rest;


import io.restassured.http.ContentType;
import io.smallrye.jwt.build.Jwt;
import org.benevides.entity.Viagem;
// Imports do Quarkus
import io.quarkus.test.junit.QuarkusTest;


// Imports do REST Assured e Hamcrest (matchers)
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// Imports do JUnit 5
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


import java.time.LocalDate;
import java.util.Set;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ViagemResourcesTest {

    private static Long viagemIdCriada;

    private String gerarToken(String username, String... roles) {
        return Jwt.issuer("https://org.benevides.jwt/issuer")
                .upn(username)
                .groups(Set.of(roles))
                .sign();
    }

    @Test
    @Order(1)
    @DisplayName("Deve retornar 401 Unauthorized ao acessar sem token")
    public void testSemAutenticacao() {
        given()
                .when().get("/api/yebTrips/viagem")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(2)
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar viagem como USER")
    public void testCriarViagemComoUserSemPermissao() {
        String token = gerarToken("leitor", "USER");

        Viagem viagem = new Viagem();
        viagem.titulo = "Viagem Sem Permissão";
        viagem.descricao = "Teste Perfil USER";
        viagem.dataInicio = LocalDate.now();
        viagem.dataFim = LocalDate.now().plusDays(5);

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(viagem)
                .when().post("/api/yebTrips/viagem")
                .then()
                .statusCode(403);
    }

    @Test
    @Order(3)
    @DisplayName("Deve criar uma nova viagem com sucesso como ADMIN")
    public void testCriarViagemComoAdmin() {
        String token = gerarToken("admin", "ADMIN");

        //Viagem viagem = new Viagem();
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

        viagemIdCriada = idExtraido.longValue();
    }

    @Test
    @Order(4)
    @DisplayName("Deve listar viagens como USER")
    public void testListarViagensComoUser() {
        String token = gerarToken("leitor", "USER");

        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/yebTrips/viagem")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(5)
    @DisplayName("Deve buscar viagem por ID como USER")
    public void testBuscarViagemPorId() {
        String token = gerarToken("leitor", "USER");

        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", viagemIdCriada)
                .when().get("/api/yebTrips/viagem/{id}")
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Expedição Atacama"));
    }

    @Test
    @Order(6)
    @DisplayName("Deve atualizar a viagem como ADMIN")
    public void testAtualizarViagem() {
        String token = gerarToken("admin", "ADMIN");

        String viagemAtualizada = """
        {
            "titulo": "Expedição Atacama - Alterado",
            "descricao": "Viagem pelo deserto",
            "dataInicio": "2026-11-10",
            "dataFim": "2026-11-20"
        }
        """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", viagemIdCriada)
                .body(viagemAtualizada)
                .when().put("/api/yebTrips/viagem/{id}")
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Expedição Atacama - Alterado"));
    }

    @Test
    @Order(7)
    @DisplayName("Deve deletar a viagem como ADMIN")
    public void testDeletarViagem() {
        String token = gerarToken("admin", "ADMIN");

        given()
                .header("Authorization", "Bearer " + token)
                .pathParam("id", viagemIdCriada)
                .when().delete("/api/yebTrips/viagem/{id}")
                .then()
                .statusCode(204);
    }
}
