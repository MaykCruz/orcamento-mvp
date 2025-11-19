package br.com.fatec.orcamento_mvp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "configs_empresa")
@Getter
@Setter
public class ConfigsEmpresa {

    @Id
    private Long id;

    @Column(length = 100)
    private String nome;

    @Column(length = 18)
    private String cnpj;

    @Column(name = "dias_validade_orcamento")
    private Integer diasValidadeOrcamento;

    @Column(name = "logo_url")
    private String logoUrl;

    // Campos de Endereço da Empresa
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;
    private String cidade;
    private String uf;
}
