package br.com.fatec.orcamento_mvp.security;

import br.com.fatec.orcamento_mvp.model.Funcionario;
import br.com.fatec.orcamento_mvp.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // É um serviço de negócio (encontrar usuários)
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // 1. O Spring Security chama este método quando o usuário tenta logar
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // O "username" é o e-mail

        // 2. Precisamos de um método no repositório para buscar por e-mail
        Funcionario funcionario = funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email));

        // 3. Convertemos nosso Funcionario para o formato que o Spring Security entende
        return new UsuarioSistema(
                funcionario.getNome(),        // O Nome real
                funcionario.getEmail(),       // O username
                funcionario.getSenha(),       // A senha criptografada
                Collections.emptyList()       // A lista de "Roles" (Permissões). Vazio por enquanto.
        );
    }
}