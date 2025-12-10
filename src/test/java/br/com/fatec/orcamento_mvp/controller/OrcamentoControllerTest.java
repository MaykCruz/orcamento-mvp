package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.model.Orcamento;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import br.com.fatec.orcamento_mvp.service.OrcamentoService;
import br.com.fatec.orcamento_mvp.service.PdfService;
import br.com.fatec.orcamento_mvp.service.ProdutoService;

import br.com.fatec.orcamento_mvp.security.UsuarioSistema;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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

    // Método auxiliar para criar o Usuário
    private RequestPostProcessor usuarioLogado() {
        UsuarioSistema usuario = new UsuarioSistema(
                "Vendedor Teste",
                "vendedor@easyflow.com",
                "123",
                Collections.emptyList()
        );
        return authentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

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
        mockMvc.perform(get("/orcamentos/novo")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("orcamentos/formulario"))
                .andExpect(model().attributeExists("clientes", "produtos", "orcamentoFormDTO"));
    }

    @Test
    void deveSalvarNovoOrcamentoComSucesso() throws Exception {
        Orcamento orcamentoSalvo = new Orcamento();
        orcamentoSalvo.setId(5L);
        when(orcamentoService.saveNewOrcamento(any())).thenReturn(orcamentoSalvo);

        mockMvc.perform(post("/orcamentos/novo")
                        .locale(Locale.US)
                        .with(usuarioLogado())
                        .with(csrf())
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
                        .locale(Locale.US)
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("itens[0].produtoId", "10")
                        .param("itens[0].qtd", "1")
                        .param("itens[0].precoUnitario", "50.00")
                        .param("itens[0].desconto", "0.00")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("orcamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors("orcamentoFormDTO", "clienteId"));

        verify(orcamentoService, never()).saveNewOrcamento(any());
    }
}