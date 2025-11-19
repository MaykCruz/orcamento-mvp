package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.model.Orcamento;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import br.com.fatec.orcamento_mvp.service.OrcamentoService;
import br.com.fatec.orcamento_mvp.service.PdfService;
import br.com.fatec.orcamento_mvp.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrcamentoController.class)
class OrcamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private OrcamentoService orcamentoService;
    @MockBean private ClienteService clienteService;
    @MockBean private ProdutoService produtoService;
    @MockBean private PdfService pdfService;

    private ClienteDTO clienteMock;
    private ProdutoDTO produtoMock;

    @BeforeEach
    void setUp() {
        clienteMock = new ClienteDTO();
        clienteMock.setId(1L);
        clienteMock.setNome("Cliente Teste");

        produtoMock = new ProdutoDTO();
        produtoMock.setId(10L);
        produtoMock.setDescricao("Produto X");
        produtoMock.setPreco(new BigDecimal("50.00"));

        when(clienteService.findAll()).thenReturn(Arrays.asList(clienteMock));
        when(produtoService.findAll()).thenReturn(Arrays.asList(produtoMock));
    }

    @Test
    void deveRetornarFormularioComDados() throws Exception {
        mockMvc.perform(get("/orcamentos/novo").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("orcamentos/formulario"))
                // CORREÇÃO 1: Alterado de "orcamentoForm" para "orcamentoFormDTO"
                .andExpect(model().attributeExists("clientes", "produtos", "orcamentoFormDTO"));
    }

    @Test
    void deveSalvarNovoOrcamentoComSucesso() throws Exception {
        Orcamento orcamentoSalvo = new Orcamento();
        orcamentoSalvo.setId(5L);
        when(orcamentoService.saveNewOrcamento(any())).thenReturn(orcamentoSalvo);

        mockMvc.perform(post("/orcamentos/novo")
                        .with(user("admin")).with(csrf())
                        .param("clienteId", "1")
                        .param("desconto", "10.00")
                        .param("itens[0].produtoId", "10")
                        .param("itens[0].qtd", "2")
                        .param("itens[0].precoUnitario", "50.00")
                        .param("itens[0].desconto", "0.00")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orcamentos/novo"))
                .andExpect(flash().attribute("mensagemSucesso", "Orçamento #5 salvo com sucesso!"));

        verify(orcamentoService).saveNewOrcamento(any());
    }

    @Test
    void deveFalharNaValidacaoSeNaoHouverCliente() throws Exception {
        mockMvc.perform(post("/orcamentos/novo")
                        .with(user("admin")).with(csrf())
                        // Note que NÃO estamos enviando o clienteId aqui de propósito
                        .param("itens[0].produtoId", "10")
                        .param("itens[0].qtd", "1")
                        .param("itens[0].precoUnitario", "50.00")
                        // CORREÇÃO 2: Adicionado o desconto zerado para evitar erro matemático no Thymeleaf
                        .param("itens[0].desconto", "0.00")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("orcamentos/formulario"))
                // CORREÇÃO 1: Alterado para "orcamentoFormDTO"
                .andExpect(model().attributeHasFieldErrors("orcamentoFormDTO", "clienteId"));

        verify(orcamentoService, never()).saveNewOrcamento(any());
    }
}