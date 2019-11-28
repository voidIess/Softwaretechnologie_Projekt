package fitnessstudio.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PdfGenerator {

	public static Document generatePdf(Map<String, Object> map, Document d) throws IOException {

		String type;
		if(map.get("type").equals("payslip")){
			type = "Gehaltsabrechnung";
		} else {
			type = "Rechnung";
		}

		d.setFont(PdfFontFactory.createFont(FontConstants.HELVETICA));

		Paragraph title = new Paragraph(type);
		title.setFontSize(22f);
		title.setBold();
		d.add(title);

		String month = PdfGenerator.getGermanMonth(LocalDate.now().getMonthValue());
		String year = Integer.toString(LocalDate.now().getYear());
		Paragraph date = new Paragraph(month + " " + year);
		date.setTextAlignment(TextAlignment.RIGHT);
		date.setBold();
		d.add(date);

		if(type.equals("Gehaltsabrechnung")){
			return PayslipPdfGenerator.generatePdf(map,d);
		} else {
			return InvoicePdfGenerator.generatePdf(map,d);
		}

	}

	static String getGermanMonth(int num) {

		if(num < 1 || num > 12) {
			throw new IllegalArgumentException();
		}

		String[] months = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
		return months[num-1];
	}
}
