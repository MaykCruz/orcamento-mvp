package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ItemOrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.dto.OrcamentoFormDTO;
import br.com.fatec.orcamento_mvp.model.*;
import br.com.fatec.orcamento_mvp.repository.ClienteRepository;
import br.com.fatec.orcamento_mvp.repository.FuncionarioRepository;
import br.com.fatec.orcamento_mvp.repository.OrcamentoRepository;
import br.com.fatec.orcamento_mvp.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrcamentoServiceImpl implements OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ConfigsEmpresaService configsEmpresaService; // Para buscar a validade

    @Override
    public Orcamento findById(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orçamento não encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orcamento> findAll() {
        // Usamos o método findAll do JPA, que retorna a lista de entidades Orcamento
        return orcamentoRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!orcamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Orçamento não encontrado com ID: " + id);
        }
        orcamentoRepository.deleteById(id);
    }

    @Override
    @Transactional // Garante que tudo (Orçamento e Itens) seja salvo ou nada seja
    public Orcamento saveNewOrcamento(OrcamentoFormDTO formDTO) {
        // --- 1. Buscar Entidades Relacionadas ---

        // Busca o Cliente
        Cliente cliente = clienteRepository.findById(formDTO.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + formDTO.getClienteId()));

        // Busca o Funcionário (logado no sistema)
        Funcionario funcionario = getFuncionarioLogado();

        // --- 2. Criar o Orçamento "Pai" ---
        Orcamento orcamento = new Orcamento();
        orcamento.setCliente(cliente);
        orcamento.setFuncionario(funcionario);
        orcamento.setObs(formDTO.getObs());
        orcamento.setDesconto(formDTO.getDesconto() != null ? formDTO.getDesconto() : BigDecimal.ZERO);

        // --- 3. Lógica da Data de Validade (Regra de Negócio) ---
        orcamento.setDataValidade(calcularDataValidade());

        // --- 4. Processar os Itens (Lógica Principal) ---
        Set<ItemOrcamento> itens = new HashSet<>();
        BigDecimal totalItens = BigDecimal.ZERO;

        for (ItemOrcamentoFormDTO itemDTO : formDTO.getItens()) {
            // Busca o produto no banco
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com ID: " + itemDTO.getProdutoId()));

            // Cria o ItemOrcamento (a entidade de ligação)
            ItemOrcamento item = new ItemOrcamento();
            item.setOrcamento(orcamento); // Linka com o pai
            item.setProduto(produto);
            item.setQtd(itemDTO.getQtd());
            item.setDesconto(itemDTO.getDesconto() != null ? itemDTO.getDesconto() : BigDecimal.ZERO);

            // --- AQUI A REGRA DE NEGÓCIO DO PREÇO ---
            // Se o produto for 'precoEditavel', usamos o preço do DTO (que o usuário digitou).
            // Se NÃO for editável, nós IGNORAMOS o preço do DTO (por segurança)
            // e usamos o preço que está no banco de dados.
            if (produto.getPrecoEditavel()) {
                item.setPrecoUnitario(itemDTO.getPrecoUnitario());
            } else {
                item.setPrecoUnitario(produto.getPreco()); // Garante o preço do cadastro
            }

            // Adiciona o item processado ao Set
            itens.add(item);

            // --- 5. Calcular o Subtotal ---
            BigDecimal subtotalItem = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQtd()));
            BigDecimal descontoItem = item.getDesconto();
            totalItens = totalItens.add(subtotalItem.subtract(descontoItem));
        }

        // --- 6. Calcular o Total Final e Salvar ---
        BigDecimal totalFinal = totalItens.subtract(orcamento.getDesconto());
        orcamento.setTotal(totalFinal);

        // Adiciona a lista de filhos (já processada) no pai
        orcamento.getItensOrcamento().addAll(itens);

        // Graças ao CascadeType.ALL, isso salva o Orçamento E todos os ItensOrcamento
        return orcamentoRepository.save(orcamento);
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Busca o funcionário que está logado na sessão do Spring Security.
     */
    private Funcionario getFuncionarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return funcionarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário logado não encontrado no banco: " + email));
    }

    /**
     * Busca as Configs da Empresa e calcula a data de validade.
     * (Implementa a regra de '0' ou 'null' = sem validade)
     */
    private LocalDate calcularDataValidade() {
        try {
            Integer diasValidade = configsEmpresaService.getConfigs().getDiasValidadeOrcamento();

            // REGRA DE NEGÓCIO que definimos:
            if (diasValidade != null && diasValidade > 0) {
                return LocalDate.now().plusDays(diasValidade);
            }
        } catch (Exception e) {
            // Se as configs não existirem ou der erro, não define validade
        }
        return null; // Sem data de validade
    }

    @Override
    @Transactional(readOnly = true)
    public OrcamentoFormDTO buscarParaVisualizar(Long id) {
        Orcamento orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orcamento não encontrado"));

        OrcamentoFormDTO dto = new OrcamentoFormDTO();
        dto.setClienteId(orcamento.getCliente().getId());
        dto.setObs(orcamento.getObs());
        dto.setDesconto(orcamento.getDesconto());

        // Converter os itens
        for (ItemOrcamento item : orcamento.getItensOrcamento()) {
            ItemOrcamentoFormDTO itemDTO = new ItemOrcamentoFormDTO();
            itemDTO.setProdutoId(item.getProduto().getId());
            itemDTO.setQtd(item.getQtd());
            itemDTO.setPrecoUnitario(item.getPrecoUnitario());
            itemDTO.setDesconto(item.getDesconto());

            // Preenche o nome para exibir na tabela
            itemDTO.setProdutoDescricaoAux(item.getProduto().getDescricao());

            dto.getItens().add(itemDTO);
        }

        return dto;
    }
}