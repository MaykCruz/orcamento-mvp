package br.com.fatec.orcamento_mvp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UsuarioSistema extends User {

    private final String nome;

    public UsuarioSistema(String nome, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
