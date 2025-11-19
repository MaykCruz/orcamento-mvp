package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.dto.ItemOrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.dto.OrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.model.*;
import br.com.fatec.orcamento_mvp.repository.ClienteRepository;
import br.com.fatec.orcamento_mvp.repository.FuncionarioRepository;
import br.com.fatec.orcamento_mvp.repository.OrcamentoRepository;
import br.com.fatec.orcamento_mvp.repository.ProdutoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoServiceImplTest {

    @Mock private OrcamentoRepository orcamentoRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private ConfigsEmpresaService configsEmpresaService;

    // Mocks para simular o Login (Spring Security)
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private OrcamentoServiceImpl orcamentoService;

    @Captor
    private ArgumentCaptor<Orcamento> orcamentoCaptor; // Para capturar o que foi salvo

    // Dados fictícios para o teste
    private Cliente clienteMock;
    private Funcionario funcionarioMock;
    private Produto produtoFixoMock;     // Produto (R$ 100, não editável)
    private Produto produtoEditavelMock; // Serviço (R$ 50, editável)
    private ConfigsEmpresaDTO configsMock;

    @BeforeEach
    void setUp() {
        // --- 1. Simular Usuário Logado ---
        User userDetails = new User("admin@email.com", "senha", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // --- 2. Preparar Dados do Banco Falso ---
        clienteMock = new Cliente();
        clienteMock.setId(1L);

        funcionarioMock = new Funcionario();
        funcionarioMock.setId(1L);
        funcionarioMock.setEmail("admin@email.com");

        // Produto Fixo: Preço é R$ 100.00
        produtoFixoMock = new Produto();
        produtoFixoMock.setId(10L);
        produtoFixoMock.setPreco(new BigDecimal("100.00"));
        produtoFixoMock.setPrecoEditavel(false);

        // Serviço Editável: Preço base é R$ 50.00
        produtoEditavelMock = new Produto();
        produtoEditavelMock.setId(20L);
        produtoEditavelMock.setPreco(new BigDecimal("50.00"));
        produtoEditavelMock.setPrecoEditavel(true);

        configsMock = new ConfigsEmpresaDTO();
        configsMock.setDiasValidadeOrcamento(10); // Validade de 10 dias
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Limpa o contexto de segurança
    }

    @Test
    void deveSalvarOrcamentoComLogicaDePrecoCorreta() {
        // --- Cenário (Arrange) ---

        // 1. O Formulário preenchido pelo usuário
        OrcamentoFormDTO formDTO = new OrcamentoFormDTO();
        formDTO.setClienteId(1L);
        formDTO.setDesconto(new BigDecimal("20.00")); // Desconto total no orçamento

        // Item 1: Usuário tenta comprar o Produto Fixo (R$ 100) por R$ 1,00.
        ItemOrcamentoFormDTO item1 = new ItemOrcamentoFormDTO();
        item1.setProdutoId(10L);
        item1.setQtd(2);
        item1.setPrecoUnitario(new BigDecimal("1.00")); // TENTATIVA DE FRAUDE/ERRO
        item1.setDesconto(new BigDecimal("10.00"));     // Desconto no item

        // Item 2: Usuário contrata Serviço (R$ 50) mas combina valor de R$ 75,00.
        ItemOrcamentoFormDTO item2 = new ItemOrcamentoFormDTO();
        item2.setProdutoId(20L);
        item2.setQtd(1);
        item2.setPrecoUnitario(new BigDecimal("75.00")); // PREÇO NEGOCIADO (Permitido)
        item2.setDesconto(BigDecimal.ZERO);

        formDTO.getItens().add(item1);
        formDTO.getItens().add(item2);

        // 2. Ensinar os Mocks a retornarem nossos dados
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(funcionarioRepository.findByEmail("admin@email.com")).thenReturn(Optional.of(funcionarioMock));
        when(produtoRepository.findById(10L)).thenReturn(Optional.of(produtoFixoMock));
        when(produtoRepository.findById(20L)).thenReturn(Optional.of(produtoEditavelMock));
        when(configsEmpresaService.getConfigs()).thenReturn(configsMock);

        // Quando o repository salvar, retorna o próprio objeto salvo
        when(orcamentoRepository.save(any(Orcamento.class))).thenAnswer(i -> i.getArgument(0));

        // --- Ação (Act) ---
        // Executa a lógica real do Service
        Orcamento orcamentoSalvo = orcamentoService.saveNewOrcamento(formDTO);

        // --- Verificação (Assert) ---

        // Captura o objeto que o Service tentou salvar no banco
        verify(orcamentoRepository).save(orcamentoCaptor.capture());
        Orcamento capturado = orcamentoCaptor.getValue();

        // Validação 1: A lista de itens foi populada?
        assertEquals(2, capturado.getItensOrcamento().size());

        // Validação 2: Regra do Preço Fixo (Item 1)
        // O Service deve ter ignorado o "1.00" e usado "100.00" do banco
        ItemOrcamento itemFixo = capturado.getItensOrcamento().stream()
                .filter(i -> i.getProduto().getId().equals(10L)).findFirst().get();
        assertEquals(0, new BigDecimal("100.00").compareTo(itemFixo.getPrecoUnitario()));

        // Validação 3: Regra do Preço Editável (Item 2)
        // O Service deve ter aceitado o "75.00" digitado pelo usuário
        ItemOrcamento itemEditavel = capturado.getItensOrcamento().stream()
                .filter(i -> i.getProduto().getId().equals(20L)).findFirst().get();
        assertEquals(0, new BigDecimal("75.00").compareTo(itemEditavel.getPrecoUnitario()));

        // Validação 4: Cálculo do Total
        // Item 1: (2 * 100) - 10 = 190
        // Item 2: (1 * 75) - 0 = 75
        // Subtotal Itens: 265
        // Desconto Geral: 20
        // Total Final Esperado: 245.00
        assertEquals(0, new BigDecimal("245.00").compareTo(capturado.getTotal()));

        // Validação 5: Data de Validade
        assertEquals(LocalDate.now().plusDays(10), capturado.getDataValidade());
    }
}