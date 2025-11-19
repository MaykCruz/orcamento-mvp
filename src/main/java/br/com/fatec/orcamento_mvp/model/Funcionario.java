package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "funcionarios")
@PrimaryKeyJoinColumn(name = "pessoa_id")
@Getter
@Setter
public class Funcionario extends Pessoa { //Herança de Pessoa

    // O 'pessoa_id' é gerenciado pelo @PrimaryKeyJoinColumn
    // Não precisamos declarar o 'id' aqui, ele vem de Pessoa.

    private String senha;
    // A senha é salva criptografada (Hash BCrypt) pelo Spring Security
}
