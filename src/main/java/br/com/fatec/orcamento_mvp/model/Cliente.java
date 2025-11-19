package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "pessoa_id")
@Getter
@Setter
public class Cliente extends Pessoa { //Herança de Pessoa

    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;
    private String complemento;
}
