package br.com.fatec.orcamento_mvp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Este DTO será usado para CADASTRAR e ATUALIZAR um cliente
@Getter
@Setter
public class ClienteDTO {

    // Não expomos o ID para criação, mas precisamos dele para atualização
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(max = 20, message = "O telefone não pode exceder 20 caracteres")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    @Size(max = 100, message = "O e-mail não pode exceder 100 caracteres")
    private String email;

    @Size(max = 11, message = "O CPF deve conter 11 dígitos")
    // Note: A validação REAL de CPF (algoritmo) é mais complexa.
    // Para o MVP iremos utilizar apenas o @Size.
    private String cpf;

    // Endereço
    @Size(max = 255)
    private String logradouro;
    @Size(max = 10)
    private String numero;
    @Size(max = 50)
    private String bairro;
    @Size(max = 9, message = "O CEP deve ter 8 ou 9 dígitos (com ou sem traço)")
    private String cep;
    @Size(max = 50)
    private String cidade;
    @Size(max = 2, message = "A UF deve ter 2 caracteres")
    private String uf;
    @Size(max = 100)
    private String complemento;











}
