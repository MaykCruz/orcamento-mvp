package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;

public interface ConfigsEmpresaService {

    // Busca a configuração (assumindo ID 1)
    ConfigsEmpresaDTO getConfigs();

    // Salva ou atualiza a configuração
    ConfigsEmpresaDTO save(ConfigsEmpresaDTO configsDTO);
}
