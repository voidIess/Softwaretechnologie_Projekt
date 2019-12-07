package fitnessstudio.invoice;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InvoiceManagement {

	private final InvoiceEntryRepository invoiceEntries;

	public InvoiceManagement(InvoiceEntryRepository invoiceEntries) {
		this.invoiceEntries = invoiceEntries;
	}

	public InvoiceEntry createInvoiceEntry(InvoiceEvent event) {
		InvoiceEntry invoiceEntry = new InvoiceEntry(event.getMember(), event.getType(),
			event.getAmount(), event.getDescription());

		return invoiceEntries.save(invoiceEntry);
	}

	public List<InvoiceEntry> getAllInvoicesForMember(Long member) {
		return invoiceEntries.findAllByMember(member).toList();
	}

	public List<InvoiceEntry> getAllInvoiceForMemberOfLastMonth(Long member) {
		LocalDate now = LocalDate.now();
		int month = now.getMonthValue();
		int year = now.getYear();

		if (month == 1) {
			year--;
			month = 12;
		}

		int finalMonth = month-1;
		int finalYear = year;

		return invoiceEntries.findAllByMember(member)
			.filter(invoiceEntry -> invoiceEntry.getCreated().getMonthValue() == finalMonth
				&& invoiceEntry.getCreated().getYear() == finalYear)
			.toList();
	}
}
