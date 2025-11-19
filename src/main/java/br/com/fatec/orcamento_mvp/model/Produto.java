package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Getter
@Setter
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String descricao;

    @NotNull // Preço pode ser 0, mas não nulo
    @PositiveOrZero // Validação: deve ser 0 ou maior
    @Column(nullable = false, precision = 10, scale = 2) // Ex: 12345678.99
    private BigDecimal preco;

    @Column(name = "imagem_url")
    private String imagemUrl;

    @NotNull
    @Column(name = "preco_editavel", nullable = false)
    private Boolean precoEditavel = false; // Valor padrão
}
