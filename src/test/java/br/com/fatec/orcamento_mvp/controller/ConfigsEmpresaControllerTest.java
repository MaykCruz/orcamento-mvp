package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.service.ConfigsEmpresaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

// Importações de segurança
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfigsEmpresaController.class) // Carrega a fatia Web para este controller
class ConfigsEmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Para simular requisições

    @MockBean // Cria um Mock do Service
    private ConfigsEmpresaService configsService;

    @Test
    void deveRetornarPaginaDeFormulario() throws Exception {
        // Cenário: Service retorna um DTO vazio
        when(configsService.getConfigs()).thenReturn(new ConfigsEmpresaDTO());

        // Ação: Simula um 'GET /configuracoes' (logado)
        mockMvc.perform(get("/configuracoes").with(user("testUser")))
                // Verificação (Assert)
                .andExpect(status().isOk()) // Espera HTTP 200 (OK)
                .andExpect(view().name("configuracoes/formulario")) // Espera o HTML
                .andExpect(model().attributeExists("configs")); // Espera o DTO 'configs'
    }

    @Test
    void deveSalvarConfiguracoesComSucesso() throws Exception {
        // Cenário: POST para /configuracoes com dados válidos

        // Ação: Simula um 'POST /configuracoes' (logado e com token CSRF)
        mockMvc.perform(post("/configuracoes")
                        .with(user("testUser"))
                        .with(csrf())
                        .param("nome", "Minha Empresa") // Simula campos do formulário
                        .param("cnpj", "12345678901234")
                )
                // Verificação
                .andExpect(status().is3xxRedirection()) // Espera um REDIRECT (HTTP 302)
                .andExpect(redirectedUrl("/configuracoes")) // Espera que o redirect seja para ele mesmo
                .andExpect(flash().attributeExists("mensagemSucesso"));

        // Verifica se o CONTROLLER chamou o SERVICE
        verify(configsService).save(any(ConfigsEmpresaDTO.class));
    }
}