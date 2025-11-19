package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamentos")
@Getter
@Setter
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp // Define a data/hora automaticamente na criação
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Lob // Para textos longos
    private String obs;

    // --- Relacionamentos (Chaves Estrangeiras) ---

    @ManyToOne(fetch = FetchType.LAZY) // "Muitos" Orçamentos para "Um" Cliente
    @JoinColumn(name = "cliente_id", nullable = false) // Coluna no banco
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY) // "Muitos" Orçamentos para "Um" Funcionario
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    // --- Relacionamento Oposto ---

    @OneToMany(
            mappedBy = "orcamento", // "orcamento" é o nome do campo na classe ItemOrcamento
            cascade = CascadeType.ALL, // Salva/Deleta os filhos junto com o pai
            orphanRemoval = true // Remove filhos que não estão mais na lista
    )
    private List<ItemOrcamento> itensOrcamento = new ArrayList<>();
}