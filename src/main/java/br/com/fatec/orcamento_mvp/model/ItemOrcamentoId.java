package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable // Marca esta classe como "embutível" (parte de outra entidade)
@Getter
@Setter
@EqualsAndHashCode // Essencial para chaves compostas
public class ItemOrcamentoId implements Serializable {

    private Long orcamentoId; // Mesmo tipo do Orcamento.id
    private Long produtoId; // Mesmo tipo do Produto.id
}
