package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.service.ProdutoService;
import br.com.fatec.orcamento_mvp.security.UsuarioSistema;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import java.util.Collections;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    private RequestPostProcessor usuarioLogado() {
        UsuarioSistema usuario = new UsuarioSistema(
                "Admin Produtos",
                "admin@produtos.com",
                "123",
                Collections.emptyList()
        );
        return authentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    @Test
    void deveRetornarPaginaDeListaDeProdutos() throws Exception {
        when(produtoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/produtos")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/lista"))
                .andExpect(model().attributeExists("produtos"));
    }

    @Test
    void deveRetornarPaginaDeNovoProduto() throws Exception {
        mockMvc.perform(get("/produtos/novo")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/formulario"))
                .andExpect(model().attributeExists("produto"));
    }

    @Test
    void deveSalvarNovoProdutoComSucesso() throws Exception {
        mockMvc.perform(post("/produtos")
                        .locale(Locale.US)
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("descricao", "Produto Válido")
                        .param("preco", "10.00")
                        .param("precoEditavel", "false")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/produtos"))
                .andExpect(flash().attributeExists("mensagemSucesso"));

        verify(produtoService).save(any(ProdutoDTO.class));
    }

    @Test
    void deveRetornarFormularioComErroDeValidacao() throws Exception {
        mockMvc.perform(post("/produtos")
                        .locale(Locale.US)
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("descricao", "")
                        .param("preco", "10.00")
                        .param("precoEditavel", "false")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("produtos/formulario"));

        verify(produtoService, never()).save(any(ProdutoDTO.class));
    }
}