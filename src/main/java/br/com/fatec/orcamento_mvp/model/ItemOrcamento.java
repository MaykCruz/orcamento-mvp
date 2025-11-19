package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_orcamento")
@Getter
@Setter
public class ItemOrcamento {

    @EmbeddedId // 1. Informa que a chave é composta e está definida na classe abaixo
    private ItemOrcamentoId id = new ItemOrcamentoId(); // Importante instanciar

    // --- Mapeamento dos campos da Chave Composta para os Relacionamentos ---

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orcamentoId") // 2. Mapeia o 'orcamentoId' de dentro do ItemOrcamentoId
    @JoinColumn(name = "orcamento_id")
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produtoId") // 3. Mapeia o 'produtoId' de dentro do ItemOrcamentoId
    @JoinColumn(name = "produto_id")
    private Produto produto;

    // --- Campos "extras" da tabela associativa ---

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario; // O preço "congelado" no momento da venda

    @Column(nullable = false)
    private int qtd;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal desconto;
}