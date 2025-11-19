package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.ConfigsEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigsEmpresaRepository extends JpaRepository<ConfigsEmpresa, Long> {
}
