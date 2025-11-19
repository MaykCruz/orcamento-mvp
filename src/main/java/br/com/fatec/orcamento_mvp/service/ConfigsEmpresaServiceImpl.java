package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.model.ConfigsEmpresa;
import br.com.fatec.orcamento_mvp.repository.ConfigsEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigsEmpresaServiceImpl implements ConfigsEmpresaService {

    @Autowired
    private ConfigsEmpresaRepository repository;

    // ID Padrão para a configuração da empresa (single-tenant)
    private static final Long CONFIG_ID = 1L;

    // --- MÉTODOS DE CONVERSÃO ---

    private ConfigsEmpresaDTO toDTO(ConfigsEmpresa entity) {
        ConfigsEmpresaDTO dto = new ConfigsEmpresaDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnpj(entity.getCnpj());
        dto.setDiasValidadeOrcamento(entity.getDiasValidadeOrcamento());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setLogradouro(entity.getLogradouro());
        dto.setNumero(entity.getNumero());
        dto.setBairro(entity.getBairro());
        dto.setCep(entity.getCep());
        dto.setCidade(entity.getCidade());
        dto.setUf(entity.getUf());
        return dto;
    }

    private ConfigsEmpresa toEntity(ConfigsEmpresaDTO dto) {
        ConfigsEmpresa entity = new ConfigsEmpresa();
        entity.setId(dto.getId()); // O ID virá do DTO (será 1L)
        entity.setNome(dto.getNome());
        entity.setCnpj(dto.getCnpj());
        entity.setDiasValidadeOrcamento(dto.getDiasValidadeOrcamento());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        return entity;
    }

    // --- MÉTODOS DE NEGÓCIO ---

    @Override
    @Transactional(readOnly = true)
    public ConfigsEmpresaDTO getConfigs() {
        // Tenta buscar a config com ID 1
        // Se não encontrar (primeira vez que o app roda),
        // retorna um DTO vazio (com o ID 1L setado) pra o formulário preencher.
        ConfigsEmpresa entity = repository.findById(CONFIG_ID).orElseGet(() -> {
            ConfigsEmpresa novaConfig = new ConfigsEmpresa();
            novaConfig.setId(CONFIG_ID); // Garante que tenha o ID 1
            return novaConfig;
        });

        return toDTO(entity);
    }

    // Este método agora recebe a ENTIDADE a ser atualizada
    private void updateEntityFromDTO(ConfigsEmpresa entity, ConfigsEmpresaDTO dto) {
        entity.setNome(dto.getNome());
        entity.setCnpj(dto.getCnpj());
        entity.setDiasValidadeOrcamento(dto.getDiasValidadeOrcamento());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        // O ID é gerenciado pelo método 'save'
    }

    @Override
    @Transactional
    public ConfigsEmpresaDTO save(ConfigsEmpresaDTO configsDTO) {
        // 1. Busca a entidade existente (ou cria uma nova se for a primeira vez)
        ConfigsEmpresa entity = repository.findById(CONFIG_ID).orElseGet(() -> {
            ConfigsEmpresa novaConfig = new ConfigsEmpresa();
            novaConfig.setId(CONFIG_ID); // Define o ID que controlamos
            return novaConfig;
        });

        // 2. Atualiza a entidade (que está 'managed' pelo JPA) com os dados do DTO
        updateEntityFromDTO(entity, configsDTO);

        // 3. Salva (que agora funcionará como um 'update' ou 'insert' para o ID=1)
        ConfigsEmpresa entitySalva = repository.save(entity);

        return toDTO(entitySalva);
    }
}
