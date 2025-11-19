package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Cliente;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 1. Carrega o contexto COMPLETO do Spring Boot
@Transactional // 2. Faz "rollback" (desfaz) de cada teste após a execução
class ClienteRepositoryTest {

    @Autowired // 3. Pede ao Spring para injetar nosso repositório
    private ClienteRepository clienteRepository;

    @Test // 4. Marca este método como um teste executável
    void deveSalvarUmNovoCliente() {
        // --- Cenário (Arrange) ---
        Cliente novoCliente = new Cliente();
        novoCliente.setNome("Cliente de Teste");
        novoCliente.setEmail("teste@email.com");
        novoCliente.setCpf("12345678901"); // Lembre-se, 11 dígitos
        novoCliente.setLogradouro("Rua do Teste");

        // --- Ação (Act) ---
        Cliente clienteSalvo = clienteRepository.save(novoCliente);

        // --- Verificação (Assert) ---
        assertNotNull(clienteSalvo); // Garante que o banco retornou um objeto
        assertNotNull(clienteSalvo.getId()); // Garante que o banco gerou um ID
        assertEquals("Cliente de Teste", clienteSalvo.getNome()); // Garante que os dados estão corretos
    }

    @Test
    void deveBuscarClientePorId() {
        // --- Cenário (Arrange) ---
        // Primeiro, criamos um cliente para ter o que buscar
        Cliente novoCliente = new Cliente();
        novoCliente.setNome("Cliente de Busca");
        novoCliente.setEmail("busca@gmail.com");
        novoCliente.setCpf("11122233344");
        Cliente clienteCriado = clienteRepository.save(novoCliente);
        Long id = clienteCriado.getId(); // Pegamos o ID gerado

        // --- Ação (Act) ---
        // Usamos .findById() que retorna um 'Optimal' (pode ou não encontrar)
        Cliente clienteEncontrado = clienteRepository.findById(id).orElse(null);

        // --- Verificação (Assert) ---
        assertNotNull(clienteEncontrado);
        assertEquals(id, clienteEncontrado.getId());
        assertEquals("Cliente de Busca", clienteEncontrado.getNome());
    }
}



















