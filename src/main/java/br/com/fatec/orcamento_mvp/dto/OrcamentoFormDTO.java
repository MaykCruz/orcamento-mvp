package br.com.fatec.orcamento_mvp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO "mestre" que representa o formulário de criação de orçamento
 */
@Getter
@Setter
public class OrcamentoFormDTO {

    @NotNull(message = "O cliente é obrigatório")
    private Long clienteId; // O ID do cliente selecionado no <select>

    private String obs; // Observações

    private BigDecimal desconto; // Desconto em R$ no TOTAL do orçamento

    @Valid // 1. Manda o Spring validar os itens da lista
    @NotEmpty(message = "O orçamento deve ter pelo menos um item") // 2. A lista não pode estar vazia
    private List<ItemOrcamentoFormDTO> itens = new ArrayList<>();
}