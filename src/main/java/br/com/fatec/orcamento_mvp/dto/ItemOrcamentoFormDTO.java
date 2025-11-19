package br.com.fatec.orcamento_mvp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para representar um item (Produto ou Serviço)
 * DENTRO do formulário de criação de orçamento.
 */
@Getter
@Setter
public class ItemOrcamentoFormDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId; // O ID do produto que foi adicionado

    @NotNull(message = "O preço unitário é obrigatório")
    private BigDecimal precoUnitario; // O preço (seja o padrão ou o editado)

    @NotNull
    @Positive(message = "A quantidade deve ser maior que zero")
    private int qtd;

    private BigDecimal desconto; // Desconto em R$ para este item

    private String produtoDescricaoAux;
}