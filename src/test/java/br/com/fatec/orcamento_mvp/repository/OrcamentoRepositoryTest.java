package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrcamentoRepositoryTest {

    // 1. Precisamos de TODOS os repositórios envolvidos
    @Autowired private OrcamentoRepository orcamentoRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private FuncionarioRepository funcionarioRepository;
    @Autowired private ProdutoRepository produtoRepository;

    // 2. Vamos criar os dados-base (dependências)
    private Cliente clientePadrao;
    private Funcionario funcionarioPadrao;
    private Produto produtoPadrao;
    private Produto servicoPadrao;

    @BeforeEach // 3. Este método roda ANTES de cada @Test
    void setUp() {
        // Criamos os dados que o Orçamento PRECISA para existir
        clientePadrao = new Cliente();
        clientePadrao.setNome("Cliente Base para Teste");
        clientePadrao.setCpf("11111111111");
        clienteRepository.save(clientePadrao);

        funcionarioPadrao = new Funcionario();
        funcionarioPadrao.setNome("Funcionario Base");
        funcionarioPadrao.setSenha("123");
        funcionarioPadrao.setEmail("func@teste.com");
        funcionarioRepository.save(funcionarioPadrao);

        produtoPadrao = new Produto();
        produtoPadrao.setDescricao("Produto (Preço Fixo)");
        produtoPadrao.setPreco(new BigDecimal("100.00"));
        produtoPadrao.setPrecoEditavel(false);
        produtoRepository.save(produtoPadrao);

        servicoPadrao = new Produto();
        servicoPadrao.setDescricao("Serviço (Preço Editável)");
        servicoPadrao.setPreco(new BigDecimal("50.00")); // Preço sugestão
        servicoPadrao.setPrecoEditavel(true);
        produtoRepository.save(servicoPadrao);
    }

    @Test
    void deveSalvarUmOrcamentoCompletoComItens() {
        // --- Cenário (Arrange) ---
        Orcamento novoOrcamento = new Orcamento();
        novoOrcamento.setCliente(clientePadrao);
        novoOrcamento.setFuncionario(funcionarioPadrao);
        novoOrcamento.setDesconto(new BigDecimal("10.00"));
        novoOrcamento.setObs("Observação de teste.");
        // O total será calculado pelo service depois, por enquanto pode ser nulo ou 0

        // -- Item 1 (Produto Padrão) --
        ItemOrcamento item1 = new ItemOrcamento();
        item1.setOrcamento(novoOrcamento); // Linka o pai
        item1.setProduto(produtoPadrao);   // Linka o produto
        item1.setQtd(2); // 2 unidades
        // Preço foi copiado do cadastro de produto
        item1.setPrecoUnitario(produtoPadrao.getPreco()); // 100.00

        // -- Item 2 (Serviço Editado) --
        ItemOrcamento item2 = new ItemOrcamento();
        item2.setOrcamento(novoOrcamento);
        item2.setProduto(servicoPadrao);
        item2.setQtd(1);
        // Preço foi EDITADO pelo usuário, diferente do cadastro (que era 50.00)
        item2.setPrecoUnitario(new BigDecimal("75.50"));

        // Adiciona os filhos na lista do pai (ESSENCIAL para o Cascade)
        novoOrcamento.getItensOrcamento().add(item1);
        novoOrcamento.getItensOrcamento().add(item2);

        // --- Ação (Act) ---
        // Graças ao CascadeType.ALL, isso deve salvar o Orcamento E os 2 ItensOrcamento
        Orcamento orcamentoSalvo = orcamentoRepository.save(novoOrcamento);

        // --- Verificação (Assert) ---
        assertNotNull(orcamentoSalvo);
        assertNotNull(orcamentoSalvo.getId());
        assertNotNull(orcamentoSalvo.getDataCriacao());
        assertEquals(clientePadrao.getId(), orcamentoSalvo.getCliente().getId());

        // Agora, buscamos do banco para ter certeza que tudo foi persistido
        Orcamento orcamentoDoBanco = orcamentoRepository.findById(orcamentoSalvo.getId()).orElse(null);

        assertNotNull(orcamentoDoBanco);
        // Verifica se os filhos (itens) foram salvos e carregados
        assertEquals(2, orcamentoDoBanco.getItensOrcamento().size());

        // Pega o primeiro item salvo e confere os valores
        // (A ordem pode não ser garantida, mas para este teste simples funciona)
        ItemOrcamento itemSalvo1 = orcamentoDoBanco.getItensOrcamento().get(0);
        assertEquals(produtoPadrao.getId(), itemSalvo1.getProduto().getId());
        assertEquals(2, itemSalvo1.getQtd());
        assertEquals(0, new BigDecimal("100.00").compareTo(itemSalvo1.getPrecoUnitario()));

        // Confere a chave composta
        assertNotNull(itemSalvo1.getId());
        assertEquals(orcamentoSalvo.getId(), itemSalvo1.getId().getOrcamentoId());
        assertEquals(produtoPadrao.getId(), itemSalvo1.getId().getProdutoId());
    }
}