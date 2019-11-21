package fitnessstudio.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import fitnessstudio.studio.Studio;
import fitnessstudio.studio.StudioRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class InvoicePdfGenerator implements PdfGenerator {

	private InvoicePdfGenerator() {}

	public static Document generatePdf(Map<String, Object> invoice, com.itextpdf.layout.Document d) throws IOException {

		float[] columnWidth = {200f, 200f};
		Table table = new Table(columnWidth);
		table.setMarginTop(30f);

		Paragraph p = new Paragraph("Kundeninformation");
		p.setBold();
		d.add(p);
		d.add(new Paragraph("Name: " + invoice.get("firstName") + " " + invoice.get("lastName")));
		d.add(new Paragraph("ID: " + invoice.get("id")));

		table.addCell("Monatlicher Beitrag");
		table.addCell("Es gibt noch keine Verträge");

		d.add(table);

		return d;
	}

}
