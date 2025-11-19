package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import java.util.List;

public interface ProdutoService {

    List<ProdutoDTO> findAll();

    ProdutoDTO findById(Long id);

    ProdutoDTO save(ProdutoDTO produtoDTO);

    void deleteById(Long id);
}
