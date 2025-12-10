package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import br.com.fatec.orcamento_mvp.security.UsuarioSistema;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    private RequestPostProcessor usuarioLogado() {
        UsuarioSistema usuario = new UsuarioSistema(
                "Usuário de Teste",
                "teste@email.com",
                "123",
                Collections.emptyList()
        );
        return authentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }

    @Test
    void deveRetornarPaginaDeListaDeClientes() throws Exception {
        when(clienteService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/clientes")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/lista"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    void deveRetornarPaginaDeNovoCliente() throws Exception {
        mockMvc.perform(get("/clientes/novo")
                        .with(usuarioLogado()))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"))
                .andExpect(model().attributeExists("cliente"));
    }

    @Test
    void deveSalvarNovoClienteComSucesso() throws Exception {
        mockMvc.perform(post("/clientes")
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("nome", "Cliente Válido")
                        .param("email", "teste@teste.com")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes"))
                .andExpect(flash().attributeExists("mensagemSucesso"));

        verify(clienteService).save(any(ClienteDTO.class));
    }

    @Test
    void deveRetornarFormularioComErroDeValidacao() throws Exception {
        mockMvc.perform(post("/clientes")
                        .with(usuarioLogado())
                        .with(csrf())
                        .param("nome", "")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"));

        verify(clienteService, never()).save(any(ClienteDTO.class));
    }
}