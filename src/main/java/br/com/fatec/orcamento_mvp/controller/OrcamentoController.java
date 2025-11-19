package br.com.fatec.orcamento_mvp.controller;

import br.com.fatec.orcamento_mvp.dto.OrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.model.Orcamento;
import br.com.fatec.orcamento_mvp.service.ClienteService;
import br.com.fatec.orcamento_mvp.service.OrcamentoService;
import br.com.fatec.orcamento_mvp.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private ClienteService clienteService; // Necessário para listar clientes

    @Autowired
    private ProdutoService produtoService; // Necessário para listar produtos

    @Autowired
    private br.com.fatec.orcamento_mvp.service.PdfService pdfService;

    /**
     * Rota para DELETAR um orçamento
     * URL: GET /orcamentos/deletar/{id}
     */
    @GetMapping("/deletar/{id}")
    public String deletarOrcamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orcamentoService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Orçamento deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao deletar orçamento: " + e.getMessage());
        }
        return "redirect:/orcamentos/historico";
    }

    @GetMapping("/editar/{id}")
    public String editarOrcamento(@PathVariable Long id, Model model) {
        // 1. Busca o DTO preenchido
        OrcamentoFormDTO dto = orcamentoService.buscarParaEdicao(id);

        model.addAttribute("orcamentoFormDTO", dto); // envia preenchido
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("produtos", produtoService.findAll());

        return "orcamentos/formulario";
    }

    /**
     * Rota para GERAR E BAIXAR O PDF
     * URL: GET /orcamentos/pdf{id}
     */
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        // 1. Busca o orçamento (Reutilizamos o repository via service)
        Orcamento orcamento = orcamentoService.findById(id);

        // 2. Gera os bytes do PDF
        byte[] pdfBytes = pdfService.gerarOrcamentoPdf(orcamento);

        // 3. Retorna como download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orcamento_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    /**
     * Rota para exibir o Histórico de Orçamentos
     * URL: GET /orcamentos/historico
     */
    @GetMapping("/historico")
    public String listarHistorico(Model model) {
        // Busca todos os orçamentos.
        // Nota: Isso carrega todos os dados (incluindo clientes/itens) graças ao JPA
        model.addAttribute("orcamentos", orcamentoService.findAll());

        // Retorna o nome do arquivo HTML
        return "orcamentos/historico"; // -> /resources/templates/orcamentos/historico.html
    }

    /**
     * Rota para EXIBIR o formulário de novo orçamento.
     * URL: GET /orcamentos/novo
     */
    @GetMapping("/novo")
    public String formularioNovoOrcamento(Model model) {

        // 1. Envia um DTO vazio para o formulário (para o Thymeleaf "amarrar")
        model.addAttribute("orcamentoFormDTO", new OrcamentoFormDTO());

        // 2. Envia os dados necessários para os <select> do formulário
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("produtos", produtoService.findAll());

        // 3. Retorna o nome do arquivo HTML
        return "orcamentos/formulario"; // -> /resources/templates/orcamentos/formulario.html
    }

    /**
     * Rota para SALVAR o novo orçamento.
     * URL: POST /orcamentos/novo
     */
    @PostMapping("/novo")
    public String salvarNovoOrcamento(
            @Valid @ModelAttribute("orcamentoFormDTO") OrcamentoFormDTO orcamentoFormDTO,
            BindingResult bindingResult, // Onde os erros de validação do DTO ficam
            Model model, // Para devolver os dados ao formulário em caso de erro
            RedirectAttributes redirectAttributes // Para enviar mensagens de sucesso
    ) {
        // 4. Se o DTO tiver erros de validação (@NotNull, @NotEmpty, etc.)
        if (bindingResult.hasErrors()) {
            // Nós NÃO redirecionamos. Recarregamos a página do formulário
            // para que o usuário veja os erros.

            // Precisamos enviar as listas de clientes e produtos NOVAMENTE
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("produtos", produtoService.findAll());

            return "orcamentos/formulario";
        }

        try {
            // 5. Se estiver tudo OK, chama o Service para salvar
            Orcamento orcamentoSalvo = orcamentoService.saveNewOrcamento(orcamentoFormDTO);

            // 6. Envia mensagem de sucesso e redireciona
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Orçamento #" + orcamentoSalvo.getId() + " salvo com sucesso!");

            // TODO: Redirecionar para a página de detalhes/PDF (P4)
            // Por enquanto, redirecionamos para um novo formulário
            return "redirect:/orcamentos/novo";

        } catch (Exception e) {
            // 7. Se der um erro inesperado (ex: EntityNotFound)

            // Recarrega a página com a mensagem de erro
            model.addAttribute("mensagemErro", "Erro ao salvar orçamento: " + e.getMessage());
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("produtos", produtoService.findAll());

            return "orcamentos/formulario";
        }
    }
}