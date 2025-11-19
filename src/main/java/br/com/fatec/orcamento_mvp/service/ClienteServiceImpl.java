package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.model.Cliente;
import br.com.fatec.orcamento_mvp.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // 1. Marca como um Serviço do Spring
public class ClienteServiceImpl implements ClienteService {

    @Autowired // 2. Injeta o repositório que conversa com o banco
    private ClienteRepository clienteRepository;

    // 3. MÉTODOS DE CONVERSÃO (DTO <-> ENTITY)
    // Converte a Entidade (do banco) para o DTO (para a tela)
    private ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setLogradouro(cliente.getLogradouro());
        dto.setNumero(cliente.getNumero());
        dto.setBairro(cliente.getBairro());
        dto.setCep(cliente.getCep());
        dto.setCidade(cliente.getCidade());
        dto.setUf(cliente.getUf());
        dto.setComplemento(cliente.getComplemento());
        return dto;
    }

    // Converte o DTO (vindo da tela) para a Entidade (para o banco)
    private Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        // Nota: O ID é gerenciado pelo JPA ou definido na atualização
        cliente.setId(dto.getId());
        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        // Regra para limpar o CPF (sanitização)
        if (dto.getCpf() != null) {
            cliente.setCpf(dto.getCpf().replaceAll("[^0-9]", ""));
        }
        cliente.setLogradouro(dto.getLogradouro());
        cliente.setNumero(dto.getNumero());
        cliente.setBairro(dto.getBairro());
        cliente.setCep(dto.getCep());
        cliente.setCidade(dto.getCidade());
        cliente.setUf(dto.getUf());
        cliente.setComplemento(dto.getComplemento());
        return cliente;
    }

    // 4. MÉTODOS DO CRUD

    @Override
    @Transactional(readOnly = true) // 5. Otimização: transação apenas de leitura
    public List<ClienteDTO> findAll() {
        // Busca todas as ENTIDADES
        return clienteRepository.findAll()
                .stream()           // Converte a lista para um "stream"
                .map(this::toDTO)   // Mapeia cada Entidade para um DTO (usando nosso método)
                .collect(Collectors.toList()); // Converte de volta para uma Lista de DTOs
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        // Busca a entidade, se não achar, lança uma exceção
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + id));
        // Converte a Entidade encontrada para DTO
        return toDTO(cliente);
    }

    @Override
    @Transactional // 6. Transação de escrita (padrão, readOnly = false)
    public ClienteDTO save(ClienteDTO clienteDTO) {
        // Converte o DTO recebido para Entidade
        Cliente cliente = toEntity(clienteDTO);

        // --- Futuro: Regras de Negócio iriam aqui ---

        // Salva a Entidade no banco
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Converte a Entidade salva (agora com ID) de volta para DTO e retorna
        return toDTO(clienteSalvo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}