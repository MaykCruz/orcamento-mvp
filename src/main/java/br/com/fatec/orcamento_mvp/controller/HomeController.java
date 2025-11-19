package br.com.fatec.orcamento_mvp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // Responde na raiz do site
    public String home() {
        // Redireciona para o histórico de orçamentos
        return "redirect:/orcamentos/historico";
    }

    @GetMapping("/login") // Controller para a página de login
    public String login() {
        return "login"; // Nome do arquivo HTML: "login.html"
    }
}
