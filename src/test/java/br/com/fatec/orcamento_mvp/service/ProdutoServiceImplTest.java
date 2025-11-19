package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.model.Produto;
import br.com.fatec.orcamento_mvp.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Ativa o Mockito
class ProdutoServiceImplTest {

    @Mock // Cria um "dublê" falso do Repositório
    private ProdutoRepository produtoRepository;

    @InjectMocks // Cria uma instância REAL do Service e injeata o Mock acima
    private ProdutoServiceImpl produtoService;

    @Test
    void deveSalvarUmNovoProduto() {
        // --- Cenário (Arrange) ---

        // 1. O DTO que vem da "tela"
        ProdutoDTO dto = new ProdutoDTO();
        dto.setDescricao("teclado");
        dto.setPreco(new BigDecimal("150.00"));
        dto.setPrecoEditavel(false);

        // 2. O que esperamos que o repositório retorne após salvar
        Produto produtoSalvoSimulado = new Produto();
        produtoSalvoSimulado.setId(1L);
        produtoSalvoSimulado.setDescricao("Teclado");
        produtoSalvoSimulado.setPreco(new BigDecimal("150.00"));
        produtoSalvoSimulado.setPrecoEditavel(false);

        // 3. Ensinar o Mock:
        // "QUANDO (when) o produtoRepository.save() for chamado com QUALQUER (any) Produto..."
        // "... ENTÃO RETORNE (thenReturn) o produtoSalvoSimulado."
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoSalvoSimulado);

        // --- Ação (Act) ---
        // Executamos o método que queremos testar
        ProdutoDTO dtoRetornado = produtoService.save(dto);

        // --- Verificação (Assert) ---
        // Verifica se o DTO retornado para o "Controller" está correto
        assertNotNull(dtoRetornado);
        assertEquals(1L, dtoRetornado.getId()); // O ID que o mock retornou
        assertEquals("Teclado", dtoRetornado.getDescricao());
        assertEquals(false, dtoRetornado.getPrecoEditavel());
    }

    @Test
    void deveBuscarProdutoPorId() {
        // --- Cenário (Arrange) ---

        // 1. O produto que simulamos existir no banco
        Produto produtoDoBanco = new Produto();
        produtoDoBanco.setId(1L);
        produtoDoBanco.setDescricao("Mouse");
        produtoDoBanco.setPreco(new BigDecimal("50.00"));

        // 2. Ensinar o Mock:
        // "QUANDO (when) o produtoRepository.findById(1L) for chamado..."
        // "...ENTÃO RETORNE (thenReturn) um Optional contendo o produtoDoBanco."
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoDoBanco));

        // --- Ação (Act) ---
        ProdutoDTO dtoEncontrado = produtoService.findById(1L);

        // --- Verificação (Assert) ---
        assertNotNull(dtoEncontrado);
        assertEquals(1L, dtoEncontrado.getId());
        assertEquals("Mouse", dtoEncontrado.getDescricao());
        // Compara BigDecimals com .compareTo()
        assertEquals(0, new BigDecimal("50.00").compareTo(dtoEncontrado.getPreco()));
    }
}





































