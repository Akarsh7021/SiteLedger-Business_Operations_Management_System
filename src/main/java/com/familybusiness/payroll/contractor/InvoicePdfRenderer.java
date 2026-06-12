package com.familybusiness.payroll.contractor;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoicePdfRenderer {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public byte[] render(WorkSite workSite, BigDecimal subtotal, BigDecimal gst, BigDecimal total) {
        StringBuilder content = new StringBuilder();
        drawInvoice(content, workSite, subtotal, gst, total);
        return buildPdf(content.toString());
    }

    private void drawInvoice(StringBuilder content, WorkSite workSite, BigDecimal subtotal, BigDecimal gst, BigDecimal total) {
        rect(content, 72, 725, 468, 32, "0.279 0.463 0.769 rg", true);
        textWhite(content, "NH HYGIENES SERVICES LIMITED", 182, 735, 18, true);
        textBlueItalic(content, "Residential and Commercial Cleaning Services", 220, 710, 10);

        text(content, "Invoice No." + workSite.getInvoiceNumber(), 74, 680, 11, false);
        text(content, "Date: " + workSite.getInvoiceDate().format(DATE_FORMATTER), 380, 680, 11, false);

        text(content, "To,", 74, 650, 11, false);
        int y = 635;
        for (String line : splitLines(workSite.getInvoiceBillingAddress(), 52)) {
            text(content, line, 74, y, 11, false);
            y -= 14;
        }

        int tableTop = 585;
        int rowHeight = 36;
        int bodyHeight = 185;
        int subtotalHeight = 30;
        int gstHeight = 30;
        int totalHeight = 34;
        int tableLeft = 74;
        int srWidth = 48;
        int particularsWidth = 360;
        int priceWidth = 55;
        int tableWidth = srWidth + particularsWidth + priceWidth;

        line(content, tableLeft, tableTop, tableLeft + tableWidth, tableTop);
        line(content, tableLeft, tableTop - rowHeight, tableLeft + tableWidth, tableTop - rowHeight);
        line(content, tableLeft, tableTop - rowHeight - bodyHeight, tableLeft + tableWidth, tableTop - rowHeight - bodyHeight);
        line(content, tableLeft, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight, tableLeft + tableWidth, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight);
        line(content, tableLeft, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight, tableLeft + tableWidth, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight);

        line(content, tableLeft, tableTop, tableLeft, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight);
        line(content, tableLeft + srWidth, tableTop, tableLeft + srWidth, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight);
        line(content, tableLeft + srWidth + particularsWidth, tableTop, tableLeft + srWidth + particularsWidth, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight);
        line(content, tableLeft + tableWidth, tableTop, tableLeft + tableWidth, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - totalHeight);

        text(content, "Sr. No.", tableLeft + 8, tableTop - 25, 10, true);
        text(content, "Particulars", tableLeft + srWidth + 155, tableTop - 25, 10, true);
        text(content, "Price", tableLeft + srWidth + particularsWidth + 15, tableTop - 25, 10, true);

        List<String> particulars = new ArrayList<>();
        particulars.add(workSite.getServiceTypeDisplayName() + "\nJob Site: " + workSite.getLocation());
        for (InvoiceItem item : workSite.getInvoiceItems()) {
            particulars.add(item.getDescription());
        }

        int detailY = tableTop - rowHeight - 70;
        int srNo = 1;
        for (String particular : particulars) {
            text(content, String.valueOf(srNo), tableLeft + 8, detailY, 10, false);
            srNo++;
            for (String line : splitLines(particular, 56)) {
                text(content, line, tableLeft + srWidth + 8, detailY, 10, false);
                detailY -= 13;
            }
            detailY -= 4;
        }

        int priceY = tableTop - rowHeight - 92;
        text(content, money(workSite.getQuotedAmount()), tableLeft + srWidth + particularsWidth + 7, priceY, 10, false);
        priceY -= 17;
        for (InvoiceItem item : workSite.getInvoiceItems()) {
            text(content, money(item.getPrice()), tableLeft + srWidth + particularsWidth + 7, priceY, 10, false);
            priceY -= 17;
        }

        text(content, money(subtotal), tableLeft + srWidth + particularsWidth + 7, tableTop - rowHeight - bodyHeight - 18, 10, false);
        text(content, "Add : GST (5%)", tableLeft + srWidth + 8, tableTop - rowHeight - bodyHeight - subtotalHeight - 18, 10, false);
        text(content, money(gst), tableLeft + srWidth + particularsWidth + 7, tableTop - rowHeight - bodyHeight - subtotalHeight - 18, 10, false);
        text(content, "Total", tableLeft + srWidth + 8, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - 22, 10, false);
        text(content, money(total), tableLeft + srWidth + particularsWidth + 7, tableTop - rowHeight - bodyHeight - subtotalHeight - gstHeight - 22, 10, false);

        int footerY = 245;
        underlinedText(content, "GST No.772220810 RT0001", 74, footerY, 10, true);
        underlinedText(content, "PAYMENT DETAILS:", 74, footerY - 28, 10, true);
        text(content, "Cheque Payable: NH Hygiene Services Ltd.", 74, footerY - 55, 10, false);
        text(content, "E-transfer: guptapravin00021@gmail.com", 74, footerY - 82, 10, false);
        text(content, "EFT - Transit #:11650 Inst#:002 Acct.#:0117013", 74, footerY - 109, 10, false);
        underlinedText(content, "For NH Hygiene Services Ltd.", 74, footerY - 136, 10, true);
        underlinedText(content, "Pravin Gupta", 74, footerY - 163, 10, true);
        text(content, "13475 96 Avenue Surrey - BC V3V 1Y8", 214, 42, 10, false);
        text(content, "Cell No.778-682-1279 Email id : info@nhhygieneservices.ca www.nhhygieneservices.ca", 112, 26, 9, false);
    }

    private byte[] buildPdf(String stream) {
        List<String> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>",
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R /F2 5 0 R /F3 6 0 R >> >> /Contents 7 0 R >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Oblique >>",
                "<< /Length " + stream.getBytes(StandardCharsets.ISO_8859_1).length + " >>\nstream\n" + stream + "endstream"
        );

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length);
            pdf.append(i + 1).append(" 0 obj\n").append(objects.get(i)).append("\nendobj\n");
        }

        int xrefOffset = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length;
        pdf.append("xref\n0 ").append(objects.size() + 1).append("\n");
        pdf.append("0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        pdf.append("startxref\n").append(xrefOffset).append("\n%%EOF");
        return pdf.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private void text(StringBuilder content, String value, int x, int y, int size, boolean bold) {
        textWithFont(content, value, x, y, size, bold ? "F2" : "F1");
    }

    private void textWithFont(StringBuilder content, String value, int x, int y, int size, String font) {
        content.append("BT /").append(font).append(" ").append(size).append(" Tf ")
                .append(x).append(" ").append(y).append(" Td (").append(escape(value)).append(") Tj ET\n");
    }

    private void textWhite(StringBuilder content, String value, int x, int y, int size, boolean bold) {
        content.append("1 1 1 rg\n");
        text(content, value, x, y, size, bold);
        content.append("0 0 0 rg\n");
    }

    private void textBlueItalic(StringBuilder content, String value, int x, int y, int size) {
        content.append("0.279 0.463 0.769 rg\n");
        textWithFont(content, value, x, y, size, "F3");
        content.append("0 0 0 rg\n");
    }

    private void underlinedText(StringBuilder content, String value, int x, int y, int size, boolean bold) {
        text(content, value, x, y, size, bold);
        int underlineWidth = Math.max(20, value.length() * size / 2);
        line(content, x, y - 3, x + underlineWidth, y - 3);
    }

    private void line(StringBuilder content, int x1, int y1, int x2, int y2) {
        content.append(x1).append(" ").append(y1).append(" m ").append(x2).append(" ").append(y2).append(" l S\n");
    }

    private void rect(StringBuilder content, int x, int y, int width, int height, String color, boolean fill) {
        content.append(color).append("\n")
                .append(x).append(" ").append(y).append(" ").append(width).append(" ").append(height).append(" re ")
                .append(fill ? "f" : "S").append("\n0 0 0 rg\n");
    }

    private List<String> splitLines(String value, int maxLength) {
        String clean = value == null ? "" : value.replace("\r", "");
        List<String> lines = new ArrayList<>();
        for (String paragraph : clean.split("\n")) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                if (line.length() + word.length() + 1 > maxLength) {
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
                if (!line.isEmpty()) {
                    line.append(" ");
                }
                line.append(word);
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private String money(BigDecimal value) {
        return value.setScale(2).toPlainString();
    }

    private String escape(String value) {
        return (value == null ? "" : value)
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace('\u2013', '-')
                .replace('\u2014', '-');
    }
}
