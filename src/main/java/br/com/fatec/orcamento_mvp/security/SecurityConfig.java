package br.com.fatec.orcamento_mvp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // 1. Expõe o Encoder para o Spring
    public PasswordEncoder passwordEncoder() {
        // Usando o BCrypt, o padrão-ouro para hashing de senhas
        return new BCryptPasswordEncoder();
    }

    @Bean // 2. O "filtro" principal de segurança
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 3. Permite acesso público a CSS, JS, Imagens
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()
                        // 4. Qualquer outra requisição...
                        .anyRequest().authenticated() // ...deve ser autenticada
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para deslogar
                        .logoutSuccessUrl("/login?logout") // Para onde ir após deslogar
                        .permitAll()
                )
                // Desabilitar CSRF por enquanto para simplificar o MVP com Thymeleaf
                // Em produção, habilitaríamos e usaríamos os tokens do Thymeleaf
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}