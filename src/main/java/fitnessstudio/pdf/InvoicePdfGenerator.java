package fitnessstudio.pdf;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import fitnessstudio.contract.Contract;
import fitnessstudio.invoice.InvoiceEntry;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.member.Member;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InvoicePdfGenerator implements PdfGenerator {

	private InvoicePdfGenerator() {}

	public static Document generatePdf(Map<String, Object> invoice, com.itextpdf.layout.Document d){

		Member member = (Member) invoice.get("member");
		List<InvoiceEntry> invoiceEntries = (List<InvoiceEntry>) invoice.get("invoiceEntries");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		MonetaryAmountFormat moneyFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);

		Paragraph p = new Paragraph("Kundeninformation");
		p.setBold();
		d.add(p);
		d.add(new Paragraph("Name: " + member.getFirstName() + " " + member.getLastName()));
		d.add(new Paragraph("ID: " + member.getMemberId()));

		Table table1 = new Table(2);
		table1.setWidth(new UnitValue(UnitValue.PERCENT, 100));
		table1.setMarginBottom(15f);

		d.add(new Paragraph("Monatlicher Beitrag").setBold());

		Contract contract = member.getContract();
		table1.addCell(contract.getName());
		table1.addCell(moneyFormat.format(contract.getPrice()));

		d.add(table1);

		Table table2 = new Table(3);
		table2.setWidth(new UnitValue(UnitValue.PERCENT, 100));
		table2.setMarginBottom(15f);
		table2.setMarginTop(15f);

		if(!invoiceEntries.isEmpty()) {
			d.add(new Paragraph("Umsatz Ihres Guthabenkontos").setBold());

			String startDate = ((LocalDate) invoice.get("startDate")).format(dateFormatter);
			String startCredit = moneyFormat.format((Money) invoice.get("startCredit"));
			d.add(new Paragraph("Kontostand vom " + startDate + ": " + startCredit));

			for (InvoiceEntry entry: invoiceEntries) {

				Money amount = entry.getAmount();
				if(entry.getType().equals(InvoiceType.WITHDRAW)) {
					amount = amount.negate();
				}

				table2.addCell(entry.getCreated().format(dateFormatter));
				table2.addCell(entry.getDescription());
				table2.addCell(moneyFormat.format(amount));
			}

			d.add(table2);

			String endDate = ((LocalDate) invoice.get("endDate")).format(dateFormatter);
			String endCredit = moneyFormat.format((Money) invoice.get("endCredit"));
			d.add(new Paragraph("Kontostand vom " + endDate + ": " + endCredit));

		}

		return d;
	}

}
