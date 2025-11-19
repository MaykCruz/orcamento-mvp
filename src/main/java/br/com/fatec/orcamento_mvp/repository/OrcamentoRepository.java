package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
}
