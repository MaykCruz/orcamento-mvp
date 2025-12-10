package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Métodos como save(), findById(), findAll(), deleteById()
    // já estão prontos graças ao JPA
}