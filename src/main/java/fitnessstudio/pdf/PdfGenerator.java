package fitnessstudio.pdf;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.time.LocalDate;
import java.util.Map;

public interface PdfGenerator {

	static Document generatePdf(Map<String, Object> map, Document d) {

		String type;
		if(map.get("type").equals("payslip")){
			type = "Gehaltsabrechnung";
		} else {
			type = "Rechnung";
		}

		Paragraph title = new Paragraph(type);
		title.setFontSize(22f);
		title.setBold();
		d.add(title);

		LocalDate dateOfLastMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth());
		String month = getGermanMonth(dateOfLastMonth.getMonthValue());
		Paragraph date = new Paragraph(month + " " + dateOfLastMonth.getYear());
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
			throw new IllegalArgumentException("Got wrong monthValue");
		}

		String[] months = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August",
				"September", "Oktober", "November", "Dezember"};

		return months[num-1];
	}
}
