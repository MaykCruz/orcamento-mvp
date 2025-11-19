package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
