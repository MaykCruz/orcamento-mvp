package br.com.fatec.orcamento_mvp.service;

import br.com.fatec.orcamento_mvp.dto.ConfigsEmpresaDTO;
import br.com.fatec.orcamento_mvp.model.ItemOrcamento;
import br.com.fatec.orcamento_mvp.model.Orcamento;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PdfService {

    @Autowired
    private ConfigsEmpresaService configsEmpresaService;

    public byte[] gerarOrcamentoPdf(Orcamento orcamento) {
        // 1. Cria o documento em memória (ByteArrayOutputStream)
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Busca as configurações da empresa (Logo, CNPJ, etc.)
            ConfigsEmpresaDTO empresa = configsEmpresaService.getConfigs();

            // --- CABEÇALHO (Logo e Dados da Empresa) ---
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3}); // Coluna 1 (Logo) menor que Coluna 2 (Texto)

            // Logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            try {
                if (empresa.getLogoUrl() != null && !empresa.getLogoUrl().isEmpty()) {
                    Image logo = Image.getInstance(new URL(empresa.getLogoUrl()));
                    logo.scaleToFit(100, 80); // Tamanho máximo do logo
                    logoCell.addElement(logo);
                } else {
                    logoCell.addElement(new Phrase("Sem Logo"));
                }
            } catch (Exception e) {
                logoCell.addElement(new Phrase("(Erro ao carregar logo)"));
            }
            headerTable.addCell(logoCell);

            // Dados da Empresa
            PdfPCell dadosEmpresaCell = new PdfPCell();
            dadosEmpresaCell.setBorder(Rectangle.NO_BORDER);
            dadosEmpresaCell.addElement(new Paragraph(empresa.getNome() != null ? empresa.getNome() : "Minha Empresa", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            dadosEmpresaCell.addElement(new Phrase("CNPJ: " + (empresa.getCnpj() != null ? empresa.getCnpj() : "N/A")));
            dadosEmpresaCell.addElement(new Phrase(formatarEndereco(empresa)));
            headerTable.addCell(dadosEmpresaCell);

            document.add(headerTable);
            document.add(new Paragraph("\n")); // Espaço

            // --- TÍTULO DO DOCUMENTO ---
            Paragraph titulo = new Paragraph("ORÇAMENTO #" + orcamento.getId(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLUE));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // --- DADOS DO CLIENTE E DATAS ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);

            PdfPCell clienteCell = new PdfPCell();
            clienteCell.setBorder(Rectangle.BOX);
            clienteCell.setPadding(10);
            clienteCell.addElement(new Phrase("Cliente:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            clienteCell.addElement(new Phrase(orcamento.getCliente().getNome()));
            clienteCell.addElement(new Phrase("CPF: " + orcamento.getCliente().getCpf()));
            if (orcamento.getCliente().getEmail() != null) clienteCell.addElement(new Phrase("Email: " + orcamento.getCliente().getEmail()));
            infoTable.addCell(clienteCell);

            PdfPCell datasCell = new PdfPCell();
            datasCell.setBorder(Rectangle.BOX);
            datasCell.setPadding(10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            datasCell.addElement(new Phrase("Data de Criação: " + orcamento.getDataCriacao().format(formatter)));

            // Lógica da Data de Validade (Só mostra se existir)
            if (orcamento.getDataValidade() != null) {
                datasCell.addElement(new Phrase("Válido até: " + orcamento.getDataValidade().format(formatter), FontFactory.getFont(FontFactory.HELVETICA, 12, Color.RED)));
            }
            infoTable.addCell(datasCell);

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // --- TABELA DE ITENS ---
            PdfPTable itensTable = new PdfPTable(6); // Imagem, Descrição, Qtd, Preço Unit, Desconto, Subtotal
            itensTable.setWidthPercentage(100);
            itensTable.setWidths(new float[]{1, 4, 1, 2, 2, 2});

            // Cabeçalho da Tabela
            String[] headers = {"Img", "Descrição", "Qtd", "Vl. Unit.", "Desc", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
                cell.setBackgroundColor(Color.DARK_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                itensTable.addCell(cell);
            }

            // Linhas dos Itens
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (ItemOrcamento item : orcamento.getItensOrcamento()) {
                // 1. Imagem do Produto
                PdfPCell imgCell = new PdfPCell();
                try {
                    String imgUrl = item.getProduto().getImagemUrl();
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Image prodImg = Image.getInstance(new URL(imgUrl));
                        prodImg.scaleToFit(30, 30); // Miniatura
                        imgCell.addElement(prodImg);
                    }
                } catch (Exception e) {
                    // Ignora erro de imagem
                }
                itensTable.addCell(imgCell);

                // 2. Descrição
                itensTable.addCell(new Phrase(item.getProduto().getDescricao(), FontFactory.getFont(FontFactory.HELVETICA, 10)));

                // 3. Qtd
                PdfPCell qtdCell = new PdfPCell(new Phrase(String.valueOf(item.getQtd()), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                qtdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itensTable.addCell(qtdCell);

                // 4. Preço Unit (Já considerando se foi editado ou não, pois está salvo no item)
                PdfPCell precoCell = new PdfPCell(new Phrase(nf.format(item.getPrecoUnitario()), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                precoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itensTable.addCell(precoCell);

                // 5. Desconto
                BigDecimal descVal = item.getDesconto() != null ? item.getDesconto() : BigDecimal.ZERO;
                PdfPCell descCell = new PdfPCell(new Phrase(nf.format(descVal), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                descCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itensTable.addCell(descCell);

                // 6. Subtotal ((Preço * Qtd) - DescontoItem)
                BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQtd())).subtract(item.getDesconto() != null ? item.getDesconto() : BigDecimal.ZERO);
                PdfPCell subCell = new PdfPCell(new Phrase(nf.format(subtotal), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                subCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                itensTable.addCell(subCell);
            }

            document.add(itensTable);

            // --- TOTAIS ---
            Paragraph pTotal = new Paragraph("Total Geral: " + nf.format(orcamento.getTotal()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            pTotal.setSpacingBefore(10);
            document.add(pTotal);

            if (orcamento.getDesconto() != null && orcamento.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                Paragraph pDesconto = new Paragraph("(Desconto Aplicado: " + nf.format(orcamento.getDesconto()) + ")", FontFactory.getFont(FontFactory.HELVETICA, 10));
                pDesconto.setAlignment(Element.ALIGN_RIGHT);
                document.add(pDesconto);
            }

            // --- OBSERVAÇÕES ---
            if (orcamento.getObs() != null && !orcamento.getObs().isEmpty()) {
                document.add(new Paragraph("\nObservações:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                document.add(new Paragraph(orcamento.getObs()));
            }

            // --- ASSINATURA ---
            document.add(new Paragraph("\n\n\n_______________________________________________"));
            document.add(new Paragraph("Assinatura do Responsável (" + orcamento.getFuncionario().getNome() + ")"));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }

        return out.toByteArray();
    }

    private String formatarEndereco(ConfigsEmpresaDTO e) {
        // Helper simples para montar string de endereço
        String end = "";
        if (e.getLogradouro() != null) end += e.getLogradouro();
        if (e.getNumero() != null) end += ", " + e.getNumero();
        if (e.getBairro() != null) end += " - " + e.getBairro();
        if (e.getCidade() != null) end += "\n" + e.getCidade();
        if (e.getUf() != null) end += "/" + e.getUf();
        return end;
    }
}