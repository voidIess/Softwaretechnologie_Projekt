package fitnessstudio.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Line;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class PayslipPdfGenerator implements PdfGenerator {

	private PayslipPdfGenerator() {}

	public static Document generatePdf(Map<String, Object> payslip, Document d) throws IOException {

		d.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));

		Paragraph title = new Paragraph("Gehaltsabrechnung");
		title.setFontSize(22f);
		title.setBold();
		d.add(title);

		String month = PdfGenerator.getGermanMonth(LocalDate.now().getMonthValue());
		String year = Integer.toString(LocalDate.now().getYear());
		Paragraph date = new Paragraph(month + " " + year);
		date.setTextAlignment(TextAlignment.RIGHT);
		date.setBold();
		d.add(date);

		float[] columnWidth = {200f, 200f};
		Table table = new Table(columnWidth);
		table.setMarginTop(30f);

		table.addCell("Arbeitgeber");
		table.addCell("Fitnessstudio e.V.");

		table.addCell("Arbeitnehmer");
		table.addCell(payslip.get("firstName").toString() + " " + payslip.get("lastName").toString());

		table.addCell("ID");
		table.addCell(payslip.get("id").toString());

		Money salary = (Money) payslip.get("salary");
		table.addCell("Bruttogehalt");
		table.addCell(salary.toString());

		table.addCell("Steuer");
		table.addCell("25%");

		table.addCell("Nettogehalt");
		table.addCell(salary.multiply(0.75).with(Monetary.getDefaultRounding()).toString()).setBold();

		d.add(table);

		return d;

	}

}
