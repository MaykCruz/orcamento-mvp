package br.com.fatec.orcamento_mvp.repository;

import br.com.fatec.orcamento_mvp.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    // O Spring Data JPA vai ler o nome do método e "entender"
    // que ele deve criar um SQL: "SELECT * FROM funcionarios WHERE email = ?"
    Optional<Funcionario> findByEmail(String email);
}
