package org.benevides.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.benevides.entity.Pessoa;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PessoaRepository {

    private final DynamoDbTable<Pessoa> pessoaTable;

    @Inject
    public PessoaRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        // Mapeia a tabela "Pessoa" do DynamoDB para o objeto Java Pessoa
        this.pessoaTable = dynamoDbEnhancedClient.table("Pessoa", TableSchema.fromBean(Pessoa.class));
    }

    public void persist(Pessoa pessoa) {
        pessoaTable.putItem(pessoa);
    }

    public Pessoa findById(String id) {
        return pessoaTable.getItem(Key.builder().partitionValue(id).build());
    }

    public Optional<Pessoa> findByCpf(String cpf) {
        DynamoDbIndex<Pessoa> cpfIndex = pessoaTable.index("cpf-index");

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(cpf).build()
        );

        return cpfIndex.query(queryConditional)
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    // --- DELETAR POR CPF ---
    public boolean deleteByCpf(String cpf) {
        Optional<Pessoa> pessoa = findByCpf(cpf);
        if (pessoa.isPresent()) {
            pessoaTable.deleteItem(Key.builder().partitionValue(pessoa.get().getId()).build());
            return true;
        }
        return false;
    }

    public List<Pessoa> listAll() {
        return pessoaTable.scan().items().stream().toList();
    }

    public boolean deleteById(String id) {
        Pessoa pessoa = findById(id);
        if (pessoa != null) {
            pessoaTable.deleteItem(Key.builder().partitionValue(id).build());
            return true;
        }
        return false;
    }
}
