package org.benevides.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.benevides.entity.Roteiro;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@ApplicationScoped
public class RoteiroRepository {

    private final DynamoDbTable<Roteiro> roteiroTable;
    private final DynamoDbIndex<Roteiro> viagemIdIndex;

    @Inject
    public RoteiroRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.roteiroTable = dynamoDbEnhancedClient.table("Roteiro", TableSchema.fromBean(Roteiro.class));
        this.viagemIdIndex = roteiroTable.index("viagemId-index");
    }

    public void persist(Roteiro roteiro) {
        roteiroTable.putItem(roteiro);
    }

    public Roteiro findById(String id) {
        return roteiroTable.getItem(Key.builder().partitionValue(id).build());
    }

    public List<Roteiro> listAll() {
        return roteiroTable.scan().items().stream().toList();
    }

    // Busca todos os roteiros associados a uma Viagem específica usando o GSI
    public List<Roteiro> findByViagemId(String viagemId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(viagemId).build()
        );

        return viagemIdIndex.query(queryConditional)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public boolean deleteById(String id) {
        Roteiro roteiro = findById(id);
        if (roteiro != null) {
            roteiroTable.deleteItem(Key.builder().partitionValue(id).build());
            return true;
        }
        return false;
    }
}
