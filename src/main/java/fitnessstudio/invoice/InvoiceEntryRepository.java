package fitnessstudio.invoice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * A repository interface to manage {@link InvoiceEntry} instances.
 *
 * @author Bill Kippe
 * @version 1.0
 */
public interface InvoiceEntryRepository extends CrudRepository<InvoiceEntry, Long> {

	/**
	 * Method to get all {@link InvoiceEntry}s of {@link fitnessstudio.member.Member}.
	 *
	 * @param member of {@link InvoiceEntry}
	 * @return all {@link InvoiceEntry}s of {@link fitnessstudio.member.Member}
	 */
	Streamable<InvoiceEntry> findAllByMember(Long member);

	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<InvoiceEntry> findAll();
}
