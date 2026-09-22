package org.benevides.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.benevides.entity.Pessoa;
import org.benevides.entity.Roteiro;
import org.benevides.entity.Viagem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@ApplicationScoped
public class DynamoDbTableInitializer {

    @Inject
    DynamoDbEnhancedClient enhancedClient;

    void onStart(@Observes StartupEvent ev) {
        criarTabelaSeNaoExistir("Pessoa", Pessoa.class);
        criarTabelaSeNaoExistir("Viagem", Viagem.class);
        criarTabelaSeNaoExistir("Roteiro", Roteiro.class);
    }

    private <T> void criarTabelaSeNaoExistir(String nomeTabela, Class<T> clazz) {
        try {
            enhancedClient.table(nomeTabela, TableSchema.fromBean(clazz)).createTable();
            System.out.println("Tabela '" + nomeTabela + "' criada com sucesso no DynamoDB!");
        } catch (ResourceInUseException e) {
            // A tabela já existe no banco, ignora a exceção
        } catch (Exception e) {
            System.err.println("Erro ao verificar/criar tabela '" + nomeTabela + "': " + e.getMessage());
        }
    }
}
