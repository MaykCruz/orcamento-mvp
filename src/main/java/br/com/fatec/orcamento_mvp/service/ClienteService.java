package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {

    List<ClienteDTO> findAll(); // Listar todos os clientes

    ClienteDTO findById(Long id); // Buscar um cliente por ID

    ClienteDTO save(ClienteDTO clienteDTO); // Salvar (para criação ou atualização)

    void deleteById(Long id); // Deletar um cliente
}
