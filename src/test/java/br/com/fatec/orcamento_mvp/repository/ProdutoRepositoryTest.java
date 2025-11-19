package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Produto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Lembre-se desfaz tudo no banco após cada teste
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void deveSalvarUmNovoProduto() {
        // --- Cenário (Arrange) ---
        Produto novoProduto = new Produto();
        novoProduto.setDescricao("Produto de Teste (Ex: Teclado)");
        novoProduto.setPreco(new BigDecimal("199.99")); // Use o construtor de String
        novoProduto.setPrecoEditavel(false); // É um produto, não um serviço

        // --- Ação (Act) ---
        Produto produtoSalvo = produtoRepository.save(novoProduto);

        // --- Verificação (Assert) ---
        assertNotNull(produtoSalvo);
        assertNotNull(produtoSalvo.getId());
        assertEquals("Produto de Teste (Ex: Teclado)", produtoSalvo.getDescricao());
        // Compara BigDecimals com .compareTo()
        assertEquals(0, new BigDecimal("199.99").compareTo(produtoSalvo.getPreco()));
    }

    @Test
    void deveSalvarUmNovoServico() {
        // --- Cenário (Arrange) ---
        Produto novoServico = new Produto();
        novoServico.setDescricao("Serviço de Teste (Ex: Mão de Obra)");
        novoServico.setPreco(new BigDecimal("100.00")); // Preço base/sugestão
        novoServico.setPrecoEditavel(true); // É um serviço

        // --- Ação (Act) ---
        Produto servicoSalvo = produtoRepository.save(novoServico);

        // --- Verificação (Assert) ---
        assertNotNull(servicoSalvo);
        assertNotNull(servicoSalvo.getId());
        assertTrue(servicoSalvo.getPrecoEditavel()); // Verifica a flag
    }
}















