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

    // --- PALETA DE CORES EASYFLOW ---
    private static final Color COR_PRIMARIA = new Color(37, 99, 235);   // Blue-600 (#2563EB)
    private static final Color COR_CABECALHO_TABELA = new Color(241, 245, 249); // Slate-100 (#F1F5F9)
    private static final Color COR_TEXTO_ESCURO = new Color(15, 23, 42); // Slate-900
    private static final Color COR_TEXTO_CINZA = new Color(100, 116, 139); // Slate-500
    private static final Color COR_BORDA = new Color(226, 232, 240); // Slate-200

    // --- FONTES ---
    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL, COR_PRIMARIA);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.NORMAL, COR_TEXTO_ESCURO);
    private static final Font FONT_CORPO = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, COR_TEXTO_ESCURO);
    private static final Font FONT_CORPO_PEQUENO = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, COR_TEXTO_CINZA);
    private static final Font FONT_CABECALHO_TABELA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.NORMAL, COR_TEXTO_ESCURO);

    public byte[] gerarOrcamentoPdf(Orcamento orcamento) {
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            ConfigsEmpresaDTO empresa = configsEmpresaService.getConfigs();

            // 1. CABEÇALHO (Logo + Dados da Empresa)
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 2});

            // Logo
            PdfPCell cellLogo = new PdfPCell();
            cellLogo.setBorder(Rectangle.NO_BORDER);
            try {
                if (empresa.getLogoUrl() != null && !empresa.getLogoUrl().isEmpty()) {
                    Image logo = Image.getInstance(new URL(empresa.getLogoUrl()));
                    logo.scaleToFit(120, 60);
                    cellLogo.addElement(logo);
                } else {
                    Paragraph p = new Paragraph("EasyFlow", FONT_TITULO);
                    cellLogo.addElement(p);
                }
            } catch (Exception e) {
                cellLogo.addElement(new Paragraph("EasyFlow", FONT_TITULO));
            }
            headerTable.addCell(cellLogo);

            // Dados da Empresa
            PdfPCell cellEmpresa = new PdfPCell();
            cellEmpresa.setBorder(Rectangle.NO_BORDER);
            cellEmpresa.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph pNomeEmpresa = new Paragraph(empresa.getNome() != null ? empresa.getNome() : "Minha Empresa", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, COR_TEXTO_ESCURO));
            pNomeEmpresa.setAlignment(Element.ALIGN_RIGHT);
            cellEmpresa.addElement(pNomeEmpresa);

            String endereco = formatarEndereco(empresa);
            Paragraph pEndereco = new Paragraph(endereco, FONT_CORPO_PEQUENO);
            pEndereco.setAlignment(Element.ALIGN_RIGHT);
            cellEmpresa.addElement(pEndereco);

            if (empresa.getCnpj() != null) {
                Paragraph pCnpj = new Paragraph("CNPJ: " + empresa.getCnpj(), FONT_CORPO_PEQUENO);
                pCnpj.setAlignment(Element.ALIGN_RIGHT);
                cellEmpresa.addElement(pCnpj);
            }
            headerTable.addCell(cellEmpresa);
            document.add(headerTable);

            document.add(new Paragraph("\n"));

            // 2. BARRA DE TÍTULO
            PdfPTable titleBar = new PdfPTable(2);
            titleBar.setWidthPercentage(100);
            titleBar.setSpacingBefore(10);
            titleBar.setSpacingAfter(10);

            PdfPCell cellTitle = new PdfPCell(new Phrase("ORÇAMENTO #" + orcamento.getId(), FONT_TITULO));
            cellTitle.setBorder(Rectangle.BOTTOM);
            cellTitle.setBorderColor(COR_BORDA);
            cellTitle.setPaddingBottom(10);
            titleBar.addCell(cellTitle);

            PdfPCell cellDates = new PdfPCell();
            cellDates.setBorder(Rectangle.BOTTOM);
            cellDates.setBorderColor(COR_BORDA);
            cellDates.setPaddingBottom(10);
            cellDates.setHorizontalAlignment(Element.ALIGN_RIGHT);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            Paragraph pDataCriacao = new Paragraph("Data: " + orcamento.getDataCriacao().format(fmt), FONT_CORPO);
            pDataCriacao.setAlignment(Element.ALIGN_RIGHT);
            cellDates.addElement(pDataCriacao);

            if (orcamento.getDataValidade() != null) {
                Paragraph pValidade = new Paragraph("Válido até: " + orcamento.getDataValidade().format(fmt),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL, new Color(220, 38, 38)));
                pValidade.setAlignment(Element.ALIGN_RIGHT);
                cellDates.addElement(pValidade);
            }
            titleBar.addCell(cellDates);
            document.add(titleBar);

            // 3. DADOS DO CLIENTE
            Paragraph lblCliente = new Paragraph("PREPARADO PARA:", FONT_CORPO_PEQUENO);
            lblCliente.setSpacingBefore(5);
            document.add(lblCliente);

            Paragraph nomeCliente = new Paragraph(orcamento.getCliente().getNome(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            document.add(nomeCliente);

            if(orcamento.getCliente().getEmail() != null) {
                document.add(new Paragraph(orcamento.getCliente().getEmail(), FONT_CORPO));
            }
            if(orcamento.getCliente().getCpf() != null) {
                document.add(new Paragraph("CPF: " + orcamento.getCliente().getCpf(), FONT_CORPO));
            }

            document.add(new Paragraph("\n"));

            // 4. TABELA DE ITENS (6 COLUNAS)
            PdfPTable itensTable = new PdfPTable(6); // Img, Desc, Qtd, Unit, Desc, Total
            itensTable.setWidthPercentage(100);
            // Larguras ajustadas: Imagem pequena (1), Descrição larga (4)
            itensTable.setWidths(new float[]{1, 4, 1, 2, 2, 2});
            itensTable.setHeaderRows(1);

            // Cabeçalho
            String[] headers = {"IMG", "DESCRIÇÃO", "QTD", "UNIT.", "DESC.", "TOTAL"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FONT_CABECALHO_TABELA));
                cell.setBackgroundColor(COR_CABECALHO_TABELA);
                cell.setPadding(8);
                cell.setBorderColor(COR_BORDA);
                // Alinhamento inteligente
                if (h.equals("IMG") || h.equals("QTD")) cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                else if (h.equals("DESCRIÇÃO")) cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                else cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                itensTable.addCell(cell);
            }

            // Linhas
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            for (ItemOrcamento item : orcamento.getItensOrcamento()) {

                // 1. Imagem
                PdfPCell imgCell = new PdfPCell();
                imgCell.setPadding(5);
                imgCell.setBorderColor(COR_BORDA);
                imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                try {
                    String imgUrl = item.getProduto().getImagemUrl();
                    if (imgUrl != null && !imgUrl.isEmpty()) {
                        Image prodImg = Image.getInstance(new URL(imgUrl));
                        prodImg.scaleToFit(30, 30);
                        imgCell.addElement(prodImg);
                    } else {
                        imgCell.addElement(new Phrase("-", FONT_CORPO_PEQUENO));
                    }
                } catch (Exception e) {
                    imgCell.addElement(new Phrase("Err", FONT_CORPO_PEQUENO));
                }
                itensTable.addCell(imgCell);

                // 2. Descrição
                addCell(itensTable, item.getProduto().getDescricao(), Element.ALIGN_LEFT);

                // 3. Qtd
                addCell(itensTable, String.valueOf(item.getQtd()), Element.ALIGN_CENTER);

                // 4. Preço Unit
                addCell(itensTable, nf.format(item.getPrecoUnitario()), Element.ALIGN_RIGHT);

                // 5. Desconto Item
                BigDecimal descItem = item.getDesconto() != null ? item.getDesconto() : BigDecimal.ZERO;
                addCell(itensTable, nf.format(descItem), Element.ALIGN_RIGHT);

                // 6. Subtotal Item
                BigDecimal subtotal = item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQtd()))
                        .subtract(descItem);
                addCell(itensTable, nf.format(subtotal), Element.ALIGN_RIGHT);
            }
            document.add(itensTable);

            // 5. TOTAIS
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(40);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(15);

            if (orcamento.getDesconto() != null && orcamento.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                PdfPCell lblDesc = new PdfPCell(new Phrase("Desconto Extra:", FONT_CORPO));
                lblDesc.setBorder(Rectangle.NO_BORDER);
                lblDesc.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalTable.addCell(lblDesc);

                PdfPCell valDesc = new PdfPCell(new Phrase("-" + nf.format(orcamento.getDesconto()), FONT_CORPO));
                valDesc.setBorder(Rectangle.NO_BORDER);
                valDesc.setHorizontalAlignment(Element.ALIGN_RIGHT);
                valDesc.setPaddingBottom(5);
                totalTable.addCell(valDesc);
            }

            PdfPCell lblTotal = new PdfPCell(new Phrase("TOTAL FINAL:", FONT_SUBTITULO));
            lblTotal.setBorder(Rectangle.TOP);
            lblTotal.setBorderColor(COR_BORDA);
            lblTotal.setPaddingTop(10);
            lblTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(lblTotal);

            PdfPCell valTotal = new PdfPCell(new Phrase(nf.format(orcamento.getTotal()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.NORMAL, COR_PRIMARIA)));
            valTotal.setBorder(Rectangle.TOP);
            valTotal.setBorderColor(COR_BORDA);
            valTotal.setPaddingTop(10);
            valTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(valTotal);

            document.add(totalTable);

            // 6. OBSERVAÇÕES (CORRIGIDO AQUI)
            if (orcamento.getObs() != null && !orcamento.getObs().isEmpty()) {
                // Removemos o \n e usamos espaçamento explícito
                Paragraph pObsTitulo = new Paragraph("Observações:", FONT_SUBTITULO);
                pObsTitulo.setSpacingBefore(20); // Separa dos totais
                pObsTitulo.setSpacingAfter(5);   // Separa da caixa de texto (EVITA SOBREPOSIÇÃO)
                document.add(pObsTitulo);

                PdfPTable obsTable = new PdfPTable(1);
                obsTable.setWidthPercentage(100);
                PdfPCell cellObs = new PdfPCell(new Phrase(orcamento.getObs(), FONT_CORPO));
                cellObs.setBackgroundColor(new Color(250, 250, 250));
                cellObs.setPadding(10);
                cellObs.setBorderColor(COR_BORDA);
                obsTable.addCell(cellObs);
                document.add(obsTable);
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }

        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_CORPO));
        cell.setPadding(8);
        cell.setBorderColor(COR_BORDA);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private String formatarEndereco(ConfigsEmpresaDTO e) {
        StringBuilder sb = new StringBuilder();
        if (e.getLogradouro() != null) sb.append(e.getLogradouro());
        if (e.getNumero() != null) sb.append(", ").append(e.getNumero());
        if (e.getBairro() != null) sb.append(" - ").append(e.getBairro());
        if (e.getCidade() != null) sb.append("\n").append(e.getCidade());
        if (e.getUf() != null) sb.append("/").append(e.getUf());
        if (e.getCep() != null) sb.append(" - CEP: ").append(e.getCep());
        return sb.toString();
    }
}