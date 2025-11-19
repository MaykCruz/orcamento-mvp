package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pessoas")
@Inheritance(strategy = InheritanceType.JOINED) // Estratégia de Herança
@Getter
@Setter
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank // Validação: não pode ser nulo nem vazio
    @Column(nullable = false, length = 100) // Configuração da coluna no banco
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Email // Validação: deve ter formato de e-mail
    @Column(length = 100)
    private String email;

    @Column(length = 11) // Para 11122233344
    private String cpf;
}
