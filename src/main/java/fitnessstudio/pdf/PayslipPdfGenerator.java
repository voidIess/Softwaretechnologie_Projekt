package fitnessstudio.pdf;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.util.Map;

public class PayslipPdfGenerator implements PdfGenerator {

	private PayslipPdfGenerator() {}

	public static Document generatePdf(Map<String, Object> payslip, Document d) {

		float[] columnWidth = {200f, 200f};
		Table table1 = new Table(columnWidth);
		Table table2 = new Table(columnWidth);
		Table table3 = new Table(columnWidth);
		table1.setMarginTop(30f);
		table2.setMarginTop(30f);
		table3.setMarginTop(30f);

		table1.addCell("Arbeitgeber");
		table1.addCell("Fitnessstudio e.V.");

		table2.addCell("Arbeitnehmer");
		table2.addCell(payslip.get("firstName").toString() + " " + payslip.get("lastName").toString());

		table2.addCell("ID");
		table2.addCell(payslip.get("id").toString());

		Money salary = (Money) payslip.get("salary");
		table3.addCell("Bruttogehalt");
		table3.addCell(salary.toString());

		double incomeTax = 12.4;
		table3.addCell("Steuer");
		table3.addCell(incomeTax + "%");

		Paragraph netSalary = new Paragraph(salary.multiply((100-incomeTax)/100).with(Monetary.getDefaultRounding()).toString());
		table3.addCell("Nettogehalt");
		table3.addCell(netSalary.setBold());

		d.add(table1);
		d.add(table2);
		d.add(table3);

		return d;

	}

}
