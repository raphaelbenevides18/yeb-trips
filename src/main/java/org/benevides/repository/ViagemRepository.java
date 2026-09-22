package org.benevides.repository;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.benevides.entity.Viagem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@ApplicationScoped
public class ViagemRepository {

    private final DynamoDbTable<Viagem> viagemTable;

    @Inject
    public ViagemRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.viagemTable = dynamoDbEnhancedClient.table("Viagem", TableSchema.fromBean(Viagem.class));
    }

    public void persist(Viagem viagem) {
        viagemTable.putItem(viagem);
    }

    public Viagem findById(String id) {
        return viagemTable.getItem(Key.builder().partitionValue(id).build());
    }

    public List<Viagem> listAll() {
        return viagemTable.scan().items().stream().toList();
    }

    public boolean deleteById(String id) {
        Viagem viagem = findById(id);
        if (viagem != null) {
            viagemTable.deleteItem(Key.builder().partitionValue(id).build());
            return true;
        }
        return false;
    }
}
