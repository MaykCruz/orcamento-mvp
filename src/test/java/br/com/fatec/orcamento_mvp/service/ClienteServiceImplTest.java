package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.model.Cliente;
import br.com.fatec.orcamento_mvp.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // 1. Ativa o Mockito
class ClienteServiceImplTest {

    @Mock // 2. Cria um "dublê" falso do Repositório
    private ClienteRepository clienteRepository;

    @InjectMocks // 3. Cria uma instância REAL do Service e injeta o Mock acima nele
    private ClienteServiceImpl clienteService;

    @Captor // 4. Cria um "capturador" de argumentos
    private ArgumentCaptor<Cliente> clienteArgumentCaptor;

    @Test
    void deveSalvarClienteLimpandoCPF() {
        // --- Cenário (Arrange) ---

        // 1. O DTO que vem da "tela" (com CPF "sujo")
        ClienteDTO dtoSujo = new ClienteDTO();
        dtoSujo.setNome("Cliente Teste");
        dtoSujo.setCpf("123.456.789-00"); // CPF formatado

        // 2. O que esperamos que o repositório retorne após salvar
        Cliente clienteSalvoSimulado = new Cliente();
        clienteSalvoSimulado.setId(1L);
        clienteSalvoSimulado.setNome("Cliente Teste");
        clienteSalvoSimulado.setCpf("12345678900"); // CPF limpo

        // 3. Ensinar o Mock (o "dublê"):
        // "QUANDO (when) o clienteRepository.save() for chamado com QUALQUER (any) Cliente..."
        // "...ENTÃO RETORNE (thenReturn) o clienteSalvoSimulado."
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvoSimulado);

        // --- Ação (Act) ---
        // Executamos o método que queremos testar
        ClienteDTO dtoRetornado = clienteService.save(dtoSujo);

        // -- Verificação (Assert) ---

        // 5. Verifica se o método 'save' do REPOSITÓRIO foi chamado
        //    e captura o objeto que foi passado para ele
        verify(clienteRepository).save(clienteArgumentCaptor.capture());

        // 6. Pega o objeto que o service tentou salvar
        Cliente clienteCapturado = clienteArgumentCaptor.getValue();

        // 7. Verificamos se a LÓGICA DE NEGÓCIO (limpar CPF) funcionou
        //    Este é o teste mais importante!
        assertNotNull(clienteCapturado);
        assertEquals("12345678900", clienteCapturado.getCpf()); // Provou que o 'toEntity' limpou o CPF
        assertNull(clienteCapturado.getId()); // Provou que era um cliente NOVO (sem ID)

        // 8. Verifica se o DTO retornado para o "Controller" está correto
        assertNotNull(dtoRetornado);
        assertEquals(1L, dtoRetornado.getId()); // O ID que o mock retornou
        assertEquals("12345678900", dtoRetornado.getCpf()); // O CPF limpo
    }
}






















