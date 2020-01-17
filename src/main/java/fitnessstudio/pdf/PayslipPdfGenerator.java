package fitnessstudio.pdf;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import fitnessstudio.staff.Staff;
import org.javamoney.moneta.Money;

import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of the general PDF generator to create a {@link Staff}s payslip.
 *
 * @version 1.0
 * @author Lea Haeusler
 */
public class PayslipPdfGenerator implements PdfGenerator {

	private PayslipPdfGenerator() {}

	/**
	 * Generates and returns the payslip PDF document of the last month.
	 *
	 * @param payslip	needs keys type and staff
	 * @param d			document
	 * @return payslip PDF document
	 */
	public static Document generatePdf(Map<String, Object> payslip, Document d) {

		MonetaryAmountFormat moneyFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
		UnitValue[] columnWidths = new UnitValue[2];
		Arrays.fill(columnWidths, new UnitValue(UnitValue.PERCENT, 50));
		Staff staff = (Staff) payslip.get("staff");

		Table table1 = new Table(columnWidths);
		Table table2 = new Table(columnWidths);
		Table table3 = new Table(columnWidths);

		table1.addCell("Arbeitgeber");
		table1.addCell("Fitnessstudio e.V.");

		table2.addCell("Arbeitnehmer");
		table2.addCell(staff.getFirstName() + " " + staff.getLastName());

		table2.addCell("ID");
		table2.addCell(Long.toString(staff.getStaffId()));

		Money salary = staff.getSalary();
		table3.addCell("Bruttogehalt");
		table3.addCell(moneyFormat.format(salary));

		double incomeTax = 12.4;
		table3.addCell("Steuer");
		table3.addCell(incomeTax + "%");

		Paragraph netSalary = new Paragraph(moneyFormat.format(salary.subtract(salary.multiply(incomeTax/100f))));
		table3.addCell("Nettogehalt");
		table3.addCell(netSalary.setBold());

		for (Table table : new Table[]{table1, table2, table3}) {
			table.setMarginTop(30f);
			table.setWidth(new UnitValue(UnitValue.PERCENT, 100));
			d.add(table);
		}

		return d;

	}

}
