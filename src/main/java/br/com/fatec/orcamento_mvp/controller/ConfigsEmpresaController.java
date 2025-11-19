package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.service.ConfigsEmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracoes") // Prefixo da URL
public class ConfigsEmpresaController {

    @Autowired
    private ConfigsEmpresaService configsService;

    /**
     * Intercepta os dados do formulário ANTES de "amarrá-los" ao DTO.
     * Converte strings vazias ("") para null.
     * Isso evita o NumberFormatException ao tentar converter "" para Integer.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // O 'true' significa "converter string vazia para null"
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // --- ROATA PARA MOSTRAR O FORMULÁRIO (READ/UPDATE) ---
    // URL: GET /configuracoes
    @GetMapping
    public String formularioConfiguracoes(Model model) {
        // Busca as configurações atuais (ou um DTO vazio com ID=1)
        ConfigsEmpresaDTO configs = configsService.getConfigs();

        model.addAttribute("configs", configs);

        // Retorna o nome do arquivo HTML
        return "configuracoes/formulario"; // -> /resources/templates/configuracoes/formulario.html
    }

    // --- ROTA PARA SALVAR (UPDATE) ---
    // URL: POST /configuracoes
    @PostMapping
    public String salvarConfiguracoes(
            @Valid @ModelAttribute("configs") ConfigsEmpresaDTO configsDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Se houver erros de validação (ex: @Size)
        if (bindingResult.hasErrors()) {
            return "configuracoes/formulario"; // Volta para o formulario
        }

        configsService.save(configsDTO);

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Configurações salvas com sucesso!");

        // Redireciona de volta para a própria página de configurações
        return "redirect:/configuracoes";
    }
}































