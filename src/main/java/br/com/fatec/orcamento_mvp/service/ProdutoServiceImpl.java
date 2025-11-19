package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.model.Produto;
import br.com.fatec.orcamento_mvp.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // --- MÉTODOS DE CONVERSÃO ---

    private ProdutoDTO toDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setImagemUrl(produto.getImagemUrl());
        dto.setPrecoEditavel(produto.getPrecoEditavel());
        return dto;
    }

    private Produto toEntity(ProdutoDTO dto) {
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setImagemUrl(dto.getImagemUrl());
        // Garante que o valor não seja nulo, embora o DTO já o inicialize como 'false'
        produto.setPrecoEditavel(dto.getPrecoEditavel() != null && dto.getPrecoEditavel());
        return produto;
    }

    // --- MÉTODOS DO CRUD ---

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findAll() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + id));
        return toDTO(produto);
    }

    @Override
    @Transactional
    public ProdutoDTO save(ProdutoDTO produtoDTO) {
        Produto produto = toEntity(produtoDTO);
        Produto produtoSalvo = produtoRepository.save(produto);
        return toDTO(produtoSalvo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }
}
























