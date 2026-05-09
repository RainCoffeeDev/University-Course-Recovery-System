package AcademicReport;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 *
 * @author Runa Yamada
 */
public class ReportPDFWriter {

    // Represents one row of course data
    public static class CourseLine {
        public final String code;
        public final String title;
        public final int credit;
        public final String grade;
        public final double point;

        public CourseLine(String code, String title, int credit, String grade, double point) {
            this.code = code;
            this.title = title;
            this.credit = credit;
            this.grade = grade;
            this.point = point;
        }
    }

    // Creates a PDF and returns the file path (throws exception on failure)
    public static String writePDF(String studentId,
                                  String studentName,
                                  String program,
                                  String semester,
                                  List<CourseLine> lines,
                                  double cgpa) throws Exception {

        String fileName = studentId + "_report.pdf";
        File file = new File(fileName);

        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        // Font settings
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font infoLabelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font tableBodyFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        // Title section
        Paragraph title = new Paragraph("Academic Performance Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(new Paragraph(" ")); // blank line

        // Student information block
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);

        infoTable.addCell(makeNoBorderCell("Student Name: " + studentName, infoLabelFont));
        infoTable.addCell(makeNoBorderCell("Student ID: " + studentId, infoLabelFont));
        infoTable.addCell(makeNoBorderCell("Program: " + program, infoLabelFont));
        infoTable.addCell(makeNoBorderCell(semester, infoLabelFont));

        doc.add(infoTable);
        doc.add(Chunk.NEWLINE);

        // Course list table
        float[] widths = {2.0f, 4.5f, 1.2f, 1.2f, 1.4f};
        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table header
        addHeaderCell(table, "Course Code", tableHeaderFont);
        addHeaderCell(table, "Course Title", tableHeaderFont);
        addHeaderCell(table, "Credit", tableHeaderFont);
        addHeaderCell(table, "Grade", tableHeaderFont);
        addHeaderCell(table, "Grade Point", tableHeaderFont);

        // Course rows
        for (CourseLine c : lines) {
            table.addCell(makeBodyCell(c.code, tableBodyFont));
            table.addCell(makeBodyCell(c.title, tableBodyFont));
            table.addCell(makeBodyCell(String.valueOf(c.credit), tableBodyFont));
            table.addCell(makeBodyCell(c.grade, tableBodyFont));
            table.addCell(makeBodyCell(String.format("%.2f", c.point), tableBodyFont));
        }

        doc.add(table);

        // CGPA section
        Paragraph cgpaPara = new Paragraph(
                "Cumulative GPA (CGPA): " + String.format("%.2f", cgpa),
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );
        cgpaPara.setSpacingBefore(10f);
        doc.add(cgpaPara);

        doc.close();

        // Auto-open PDF after creation
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignore) {
            //PDF is created even if auto-open fails
        }

        return file.getAbsolutePath();
    }

    // Helper: cell with no border
    private static PdfPCell makeNoBorderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3f);
        return cell;
    }

    // Helper: table header cell
    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(230, 230, 230));
        cell.setPadding(5f);
        table.addCell(cell);
    }

    // Helper: normal body cell
    private static PdfPCell makeBodyCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4f);
        return cell;
    }
}