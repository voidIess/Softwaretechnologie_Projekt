package fitnessstudio.invoice;

import fitnessstudio.member.Member;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class InvoiceManagement {

	private final InvoiceEntryRepository invoiceEntries;

	public InvoiceManagement(InvoiceEntryRepository invoiceEntries){
		this.invoiceEntries = invoiceEntries;
	}

	public InvoiceEntry createInvoiceEntry(InvoiceEvent event){
		InvoiceEntry invoiceEntry = new InvoiceEntry(event.getMember(), event.getType(),
			event.getAmount(), event.getDescription());

		return invoiceEntries.save(invoiceEntry);
	}

	public List<InvoiceEntry> getAllInvoicesForMember(Member member){
		return invoiceEntries.findAllByMember(member).toList();
	}
}
