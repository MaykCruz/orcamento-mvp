package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ProdutoDTO;
import br.com.fatec.orcamento_mvp.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos") // 1. Prefixo de URL para produtos
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // --- ROTA DE LISTAGEM (READ) ---
    // URL: GET /produtos
    @GetMapping
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "produtos/lista"; // -> /resources/templates/produtos/lista.html
    }

    // --- ROTA PARA FORMULÁRIO DE NOVO PRODUTO (CREATE) ---
    // URL: GET /produtos/novo
    @GetMapping("/novo")
    public String formularioNovoProduto(Model model) {
        model.addAttribute("produto", new ProdutoDTO());
        return "produtos/formulario"; // -> /resources/templates/produtos/formulario.html
    }

    // --- ROTA PARA FORMULÁRIO DE EDIÇÃO (UPDATE) ---
    // URL: GET /produtos/editar/{id}
    @GetMapping("/editar/{id}")
    public String formularioEditarProduto(@PathVariable Long id, Model model) {
        model.addAttribute("produto", produtoService.findById(id));
        return "produtos/formulario"; // Reutiliza a mesma view
    }

    // --- ROTA PARA SALVAR (CREATE OU UPDATE) ---
    // URL: POST /produtos
    @PostMapping
    public String salvarProduto(
            @Valid @ModelAttribute("produto") ProdutoDTO produtoDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Se houver erros de validação (@NotBlank, @NotNull, etc. no DTO)
        if (bindingResult.hasErrors()) {
            return "produtos/formulario"; // Volta para o formulário
        }

        produtoService.save(produtoDTO);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto/Serviço salvo com sucesso!");

        return "redirect:/produtos"; // Padrão Post-Redirect-Get
    }

    // --- ROTA PARA DELETAR (DELETE) ---
    // URL: GET /produtos/deletar/{id}
    @GetMapping("/deletar/{id}")
    public String deletarProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Produto/Serviço deletado com sucesso!");
        } catch (Exception e) {
            // Se o produto estiver em um orçamento, o banco dará erro de FK
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao deletar. Este item pode estar em uso em um orçamento.");
        }
        return "redirect:/produtos";
    }
}































