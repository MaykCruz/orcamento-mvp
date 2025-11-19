package br.com.fatec.orcamento_mvp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// DTO para CADASTRAR e ATUALIZAR um produto ou serviço
@Getter
@Setter
public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 150, message = "A descriação não pode exceder 150 caracteres")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço deve ser zero ou maior")
    private BigDecimal preco; // Lembre-se: em software, representamos como BigDecimal

    @Size(max = 255, message = "A URL da imagem não pode exceder 255 caracteres")
    private String imagemUrl;

    @NotNull(message = "É obrigatório definir se o preço é editável")
    private Boolean precoEditavel = false; // Valor padrão
}
