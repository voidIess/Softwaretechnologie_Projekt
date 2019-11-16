package fitnessstudio.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class PayslipPdfGenerator implements PdfGenerator {

	private PayslipPdfGenerator() {}

	public static Document generatePdf(Map<String, Object> payslip, Document d) throws IOException {

		Paragraph title = new Paragraph("Gehaltsabrechnung " + LocalDate.now().toString());
		title.setFont(PdfFontFactory.createFont(FontConstants.COURIER_BOLD));
		title.setFontSize(18f);
		title.setBold();
		d.add(title);

		float[] columnWidth = {200f, 200f};
		Table table = new Table(columnWidth);
		table.setFont(PdfFontFactory.createFont(FontConstants.COURIER));
		title.setFontSize(12f);
		table.setMarginTop(20f);

		table.addCell("Arbeitgeber");
		table.addCell("Fitnessstudio e.V.");

		table.addCell("Arbeitnehmer");
		table.addCell(payslip.get("firstName").toString() + " " + payslip.get("lastName").toString());

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
