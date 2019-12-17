package fitnessstudio.invoice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface InvoiceEntryRepository extends CrudRepository<InvoiceEntry, Long> {

	Streamable<InvoiceEntry> findAllByMember(Long member);

	@Override
	Streamable<InvoiceEntry> findAll();
}
