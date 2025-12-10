package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.service.ConfigsEmpresaService;
import br.com.fatec.orcamento_mvp.security.UsuarioSistema;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import java.util.Collections;

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

@WebMvcTest(ConfigsEmpresaController.class)
class ConfigsEmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigsEmpresaService configsService;

    // --- MÉTODO AUXILIAR PARA CORRIGIR O ERRO DA SIDEBAR ---
    private RequestPostProcessor usuarioLogado() {
        UsuarioSistema usuario = new UsuarioSistema(
                "Admin Teste",
                "admin@teste.com",
                "123",
                Collections.emptyList()
        );
        return authentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    @Test
    void deveRetornarPaginaDeFormulario() throws Exception {
        when(configsService.getConfigs()).thenReturn(new ConfigsEmpresaDTO());

        mockMvc.perform(get("/configuracoes")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("configuracoes/formulario"))
                .andExpect(model().attributeExists("configs"));
    }

    @Test
    void deveSalvarConfiguracoesComSucesso() throws Exception {
        mockMvc.perform(post("/configuracoes")
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("nome", "Minha Empresa")
                        .param("cnpj", "12345678901234")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/configuracoes"))
                .andExpect(flash().attributeExists("mensagemSucesso"));

        verify(configsService).save(any(ConfigsEmpresaDTO.class));
    }
}