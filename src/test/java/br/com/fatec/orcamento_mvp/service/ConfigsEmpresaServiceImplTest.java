package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.model.ConfigsEmpresa;
import br.com.fatec.orcamento_mvp.repository.ConfigsEmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigsEmpresaServiceImplTest {

    @Mock
    private ConfigsEmpresaRepository repository;

    @InjectMocks
    private ConfigsEmpresaServiceImpl service;

    private static final Long CONFIG_ID = 1L;

    @Test
    void deveRetornarConfiguracoesExistentes() {
        // --- Cenário (Arrange) ---
        // Simula que o banco JÁ TEM um registro com ID 1
        ConfigsEmpresa configDoBanco = new ConfigsEmpresa();
        configDoBanco.setId(CONFIG_ID);
        configDoBanco.setNome("Empresa Teste");

        // Ensinar o Mock
        when(repository.findById(CONFIG_ID)).thenReturn(Optional.of(configDoBanco));

        // --- Ação (Act) ---
        ConfigsEmpresaDTO dto = service.getConfigs();

        // --- Verificação (Assert) ---
        assertNotNull(dto);
        assertEquals(CONFIG_ID, dto.getId());
        assertEquals("Empresa Teste", dto.getNome());
        verify(repository).findById(CONFIG_ID); // Verifica se 'findById' foi chamado
        verify(repository, never()).save(any()); // Verifica que 'save' NÃO foi chamado
    }

    @Test
    void deveRetornarNovoDTOSeNenhumaConfiguracaoExistir() {
        // --- Cenário (Arrange) ---
        // Simula que o banco está VAZIO

        // Ensinar o Mock
        when(repository.findById(CONFIG_ID)).thenReturn(Optional.empty());

        // --- Ação (Act) ---
        ConfigsEmpresaDTO dto = service.getConfigs();

        // --- Verificação (Assert) ---
        // O serviço deve criar um DTO "em branco" com o ID=1
        assertNotNull(dto);
        assertEquals(CONFIG_ID, dto.getId());
        assertNull(dto.getNome()); // Nome está vazio
        verify(repository).findById(CONFIG_ID);
        verify(repository, never()).save(any());
    }

    @Test
    void deveSalvarConfiguracoes() {
        // --- Cenário (Arrange) ---
        // DTO vindo da tela
        ConfigsEmpresaDTO dtoDaTela = new ConfigsEmpresaDTO();
        dtoDaTela.setId(CONFIG_ID);
        dtoDaTela.setNome("Nova Empresa");

        // Entidade que será salva (simulando a lógica do service)
        ConfigsEmpresa configParaSalvar = new ConfigsEmpresa();
        configParaSalvar.setId(CONFIG_ID);
        configParaSalvar.setNome("Nova Empresa");

        // Ensinar o Mock (find e save)
        // Simula que o 'findById' (do nosso service.save()) encontrou uma entidade vazia
        when(repository.findById(CONFIG_ID)).thenReturn(Optional.of(new ConfigsEmpresa()));
        // Ensinamos o 'save' a retornar a entidade salva
        when(repository.save(any(ConfigsEmpresa.class))).thenReturn(configParaSalvar);

        // --- Ação (Act) ---
        ConfigsEmpresaDTO dtoSalvo = service.save(dtoDaTela);

        // --- Verificação (Assert) ---
        assertNotNull(dtoSalvo);
        assertEquals("Nova Empresa", dtoSalvo.getNome());
        verify(repository).findById(CONFIG_ID); // Chamado pelo 'save'
        verify(repository).save(any(ConfigsEmpresa.class)); // Chamado pelo 'save'
    }
}