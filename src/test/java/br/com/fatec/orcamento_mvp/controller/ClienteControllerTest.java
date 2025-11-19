package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Carrega APENAS a camada Web para o ClienteController
@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc; // 2. Objeto para SIMULAR requisições HTTP (GET, POST)

    @MockBean // 3. Cria um Mock do Service (substitui o @Mock do teste anterior)
    private ClienteService clienteService;

    @Test
    void deveRetornarPaginaDeListaDeClientes() throws Exception {
        // Cenário: Ensinamos o service a retornar uma lista vazia
        when(clienteService.findAll()).thenReturn(Collections.emptyList());

        // Ação: Simula um 'GET /clientes'
        mockMvc.perform(get("/clientes").with(user("testUser")))
                // Verificação (Assert)
                .andExpect(status().isOk()) // Espera HTTP 200 (OK)
                .andExpect(view().name("clientes/lista")) // Espera o HTML 'clientes/lista.html'
                .andExpect(model().attributeExists("clientes")); // Espera que a lista 'clientes' foi posta no Model
    }

    @Test
    void deveRetornarPaginaDeNovoCliente() throws Exception {
        // Ação: Simula um 'GET /clientes/novo'
        mockMvc.perform(get("/clientes/novo").with(user("testUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("clientes/formulario"))
                .andExpect(model().attributeExists("cliente")); // Espera um DTO 'cliente' vazio
    }

    @Test
    void deveSalvarNovoClienteComSucesso() throws Exception {
        // Cenário: POST para /clientes
        // O DTO simulado tem 'nome' preenchido (passa na validação)

        // Ação: Simula um 'POST /clientes'
        mockMvc.perform(post("/clientes")
                        .with(user("testUser")) // 1. Simula usuário logado
                        .with(csrf())                    // 2.  Adiciona token CSRF
                        .param("nome", "Cliente Válido") // Simula o preenchimento do campo 'nome'
                        .param("email", "teste@teste.com")
                )
                // Verificação
                .andExpect(status().is3xxRedirection()) // Espera um REDIRECT (HTTP 302)
                .andExpect(redirectedUrl("/clientes")) // Espera que o redirect seja para a lista
                .andExpect(flash().attributeExists("mensagemSucesso")); // Espera a msg de sucesso

        // Verifica se o CONTROLLER chamou o SERVICE
        verify(clienteService).save(any(ClienteDTO.class));
    }

    @Test
    void deveRetornarFormularioComErroDeValidacao() throws Exception {
        // Cenário: POST para /clientes
        // O DTO simulado tem 'nome' VAZIO (falha na validação @NotBlank)

        // Ação: Simula um 'POST /clientes'
        mockMvc.perform(post("/clientes")
                        .with(user("testUser")) // 1. Simula usuário logado
                        .with(csrf())                    // 2. Adiciona token CSRF
                        .param("nome", "") // NOME VAZIO
                )
                // Verificação
                .andExpect(status().isOk()) // Espera HTTP 200 (OK) - *NÃO* é redirect
                .andExpect(view().name("clientes/formulario")); // Espera que ele retorne para o FORMULÁRIO

        // Verifica se o CONTROLLER *NÃO* CHAMOU* o SERVICE (pois a validação falhou antes)
        verify(clienteService, never()).save(any(ClienteDTO.class));
    }
}