package fitnessstudio.invoice;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class InvoiceManagement {

	private InvoiceEntryRepository invoiceEntries;

	public InvoiceManagement(InvoiceEntryRepository invoiceEntries){
		this.invoiceEntries = invoiceEntries;
	}

	public InvoiceEntry createInvoiceEntry(InvoiceEvent event){
		InvoiceEntry invoiceEntry = new InvoiceEntry(event.getMember(), event.getType(),
			event.getAmount(), event.getDescription());

		return invoiceEntries.save(invoiceEntry);
	}
}
