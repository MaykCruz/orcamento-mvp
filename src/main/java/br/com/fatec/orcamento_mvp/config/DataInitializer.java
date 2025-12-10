package br.com.fatec.orcamento_mvp.config;

import br.com.fatec.orcamento_mvp.model.Funcionario;
import br.com.fatec.orcamento_mvp.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // O BCrypt que foi criado no SecurityConfig

    @Override
    public void run(String... args) throws Exception {
        // Aqui verificamos se o usuário admin@email.com JÁ EXISTE
        // para não tentar inserir toda vez que o app reiniciar
        if (funcionarioRepository.findByEmail("admin@email.com"). isEmpty()) {

            System.out.println("--- INSERINDO USUÁRIO ADMIN PADRÃO ---");

            Funcionario admin = new Funcionario();
            admin.setNome("Admin");
            admin.setEmail("admin@email.com");

            // Nós criptografamos a senha "123456" antes de salvar
            admin.setSenha(passwordEncoder.encode("123456"));

            funcionarioRepository.save(admin);

            System.out.println("--- USUÁRIO ADMIN CRIADO COM SENHA '123456' ---");
        }
    }
}
