package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

// Importações de segurança que já sabemos que são necessárias
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class) // Carrega APENAS a camada Web para o ProdutoController
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Objeto para SIMULAR requisições HTTP

    @MockBean // Cria um Mock do Service
    private ProdutoService produtoService;

    @Test
    void deveRetornarPaginaDeListaDeProdutos() throws Exception {
        // Cenário: Service retorna uma lista vazia
        when(produtoService.findAll()).thenReturn(Collections.emptyList());

        // Ação: Simula um 'GET /produtos' (logado)
        mockMvc.perform(get("/produtos").with(user("testUser")))
                // Verificação (Assert)
                .andExpect(status().isOk()) // Espera HTTP 200 (OK)
                .andExpect(view().name("produtos/lista")) // Espera o HTML 'produtos/lista.html'
                .andExpect(model().attributeExists("produtos")); // Espera a lista 'produtos'
    }

    @Test
    void deveRetornarPaginaDeNovoProduto() throws Exception {
        // Ação: Simula um 'GET /produtos/novo' (logado)
        mockMvc.perform(get("/produtos/novo").with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/formulario"))
                .andExpect(model().attributeExists("produto")); // Espera um DTO 'produto' vazio
    }

    @Test
    void deveSalvarNovoProdutoComSucesso() throws Exception {
        // Cenário: POST para /produtos com dados válidos

        // Ação: Simula um 'POST /produtos' (logado e com token CSRF)
        mockMvc.perform(post("/produtos")
                        .with(user("testUser"))
                        .with(csrf())
                        .param("descricao", "Produto Válido") // Simula campos do formulário
                        .param("preco", "10.00")
                        .param("precoEditavel", "false")
                )
                // Verificação
                .andExpect(status().is3xxRedirection()) // Espera um REDIRECT (HTTP 302)
                .andExpect(redirectedUrl("/produtos")) // Espera que o redirect seja para a lista
                .andExpect(flash().attributeExists("mensagemSucesso"));
        // Verifica se o CONTROLLER chamou o SERIVCE
        verify(produtoService).save(any(ProdutoDTO.class));
    }

    @Test
    void deveRetornarFormularioComErroDeValidacao() throws Exception {
        // Cenário: POST para /produtos com 'descricao' vazia (falha na validação)

        // Ação: Simula um 'POST /produtos'
        mockMvc.perform(post("/produtos")
                .with(user("testUser"))
                .with(csrf())
                .param("descricao", "") // Descrição VAZIA
                .param("preco", "10.00")
                .param("precoEditavel", "false")
        )
                // Verificação
                .andExpect(status().isOk()) // Espera HTTP 200 (OK) - *NÃO* é redirect
                .andExpect(view().name("produtos/formulario")); // Espera que ele retorne para o FORMULARIO

        // Verifica se o CONTROLER *NÃO* CHAMOU* o SERVICE (validação falhou antes)
        verify(produtoService, never()).save(any(ProdutoDTO.class));
    }
}









































