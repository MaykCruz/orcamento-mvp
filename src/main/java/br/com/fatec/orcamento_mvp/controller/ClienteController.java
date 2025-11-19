package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ClienteDTO;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes") // 1. Todas as URLs neste controller começarão com /clientes
public class ClienteController {

    @Autowired // 2. Injeta o Service que foi criado
    private ClienteService clienteService;

    // -- ROTA DE LISTAGEM (READ) ---
    // URL: GET /clientes
    @GetMapping
    public String listarClientes(Model model) {
        // 3. Busca todos os clientes (DTOs) no service
        model.addAttribute("clientes", clienteService.findAll());

        // 4. Retorna o nome do arquivo HTML
        return "clientes/lista"; // -> /resources/templates/clientes/lista.html
    }

    // --- ROTA PARA MOSTRAR O FORMULÁRIO DE NOVO CLIENTE (CREATE) ---
    // URL: GET /clientes/novo
    @GetMapping("/novo")
    public String formularioNovoCliente(Model model) {
        // 5. Envia um DTO vazio para o formulário (para o Thymeleaf "amarrar" os campos)
        model.addAttribute("cliente", new ClienteDTO());
        return "clientes/formulario"; // -> /resources/templates/clientes/formulario.html
    }

    // --- ROTA PARA MOSTRAR O FORMULÁRIO DE EDIÇÃO (UPDATE) ---
    // URL: GET /clientes/editar/{id} (Ex: /clientes/editar/1)
    @GetMapping("/editar/{id}")
    public String formularioEditarCliente(@PathVariable Long id, Model model) {
        // 6. Busca o cliente por ID e o envia para o formulário
        model.addAttribute("cliente", clienteService.findById(id));
        return "clientes/formulario"; // É reutilizada a mesma view.
    }

    // --- ROTA PARA SALVAR (CREATED OU UPDATE) ---
    // URL: POST /clientes
    @PostMapping
    public String salvarCliente(
            @Valid @ModelAttribute("cliente") ClienteDTO clienteDTO, // 7. Valida o DTO
            BindingResult bindingResult, // 8. Onde os erros de validação ficam
            RedirectAttributes redirectAttributes // 9. Para enviar mensagens de sucesso
    ) {
        // 10. se o DTO tiver erros (ex: @NotBlank, @Email)
        if (bindingResult.hasErrors()) {
            // Volta para o formulário, mas mantendo os dados que o usuário digitou
            return "clientes/formulario";
        }

        // Se estiver tudo OK, salva
        clienteService.save(clienteDTO);

        // 11. Adiciona uma mensagem de "flash" (que dura só 1 requisição)
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Cliente salvo com sucesso!");

        // 12. Redireciona o navegador para a lista (evita reenvio do formulário)
        return "redirect:/clientes";
    }

    // --- ROTA PARA DELETAR (DELETE) ---
    // ULR: GET /clientes/deletar/{id}
    @GetMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cliente deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao deletar cliente. Pode estar em uso.");
        }
        return "redirect:/clientes";
    }
}
