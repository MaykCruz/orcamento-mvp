package br.com.fatec.orcamento_mvp.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigsEmpresaDTO {

    // O ID será sempre 1, mas precisamos dele
    private Long id;

    @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
    private String nome;

    @Size(max = 18, message = "O CNPJ não pode exceder 18 caracteres")
    private String cnpj;

    private Integer diasValidadeOrcamento;

    @Size(max = 255, message = "A URL do logo não pode exceder 255 caracteres")
    private String logoUrl;

    // Endereço
    @Size(max = 255)
    private String logradouro;
    @Size(max = 10)
    private String numero;
    @Size(max = 50)
    private String bairro;
    @Size(max = 9)
    private String cep;
    @Size(max = 50)
    private String cidade;
    @Size(max = 2)
    private String uf;

}


























