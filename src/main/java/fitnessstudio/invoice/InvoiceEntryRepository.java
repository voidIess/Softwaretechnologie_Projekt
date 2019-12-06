package fitnessstudio.invoice;

import fitnessstudio.member.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface InvoiceEntryRepository extends CrudRepository<InvoiceEntry, Long> {
	Streamable<InvoiceEntry> findAllByMember(Member member);
}
