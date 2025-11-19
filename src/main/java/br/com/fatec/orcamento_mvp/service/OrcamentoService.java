package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.OrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.model.Orcamento;

import java.util.List;

public interface OrcamentoService {

    /**
     * Recebe o DTO do formulário, aplica as regras de negócio
     * (cálculo de totais, regra de preço editável)
     * e salva o Orçamento e seus Itens no banco.
     *
     * @param formDTO O DTO vindo do controller
     * @return A entidade Orcamento que foi salva (para o controller redirecionar)
     */
    Orcamento saveNewOrcamento(OrcamentoFormDTO formDTO);

    List<Orcamento> findAll();

    Orcamento findById(Long id);

    OrcamentoFormDTO buscarParaEdicao(Long id);

    void deleteById(Long id);
}