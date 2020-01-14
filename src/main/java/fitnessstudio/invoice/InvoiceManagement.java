package fitnessstudio.invoice;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of business logic related to {@link InvoiceEntry}s.
 *
 * @author Bill Kippe
 * @author Lea Häusler
 * @version 1.0
 */
@Service
@Transactional
public class InvoiceManagement {

	private final InvoiceEntryRepository invoiceEntries;

	/**
	 * Creates a new {@link InvoiceManagement} with the given {@link InvoiceEntryRepository}.
	 *
	 * @param invoiceEntries must not be {@literal null}.
	 */
	public InvoiceManagement(InvoiceEntryRepository invoiceEntries) {
		Assert.notNull(invoiceEntries, "InvoiceEntryRepository must not be null!");

		this.invoiceEntries = invoiceEntries;
	}

	/**
	 * Creates a new {@link InvoiceEntry} with the information given in {@link InvoiceEvent}.
	 *
	 * @param event must not be {@literal null}.
	 * @return the new {@link InvoiceEntry} instance.
	 */
	public InvoiceEntry createInvoiceEntry(InvoiceEvent event) {
		Assert.notNull(event, "InvoiceEvent must not be null");

		InvoiceEntry invoiceEntry = new InvoiceEntry(event.getMember(), event.getType(),
			event.getAmount(), event.getDescription());

		return invoiceEntries.save(invoiceEntry);
	}

	/**
	 * Method to get all {@link InvoiceEntry}s of this {@link fitnessstudio.member.Member}.
	 *
	 * @param member	ID of {@link fitnessstudio.member.Member}
	 * @return new {@link List} of all related {@link InvoiceEntry}s.
	 */
	public List<InvoiceEntry> getAllInvoicesForMember(Long member) {
		return invoiceEntries.findAllByMember(member).toList();
	}

	/**
	 * Method to get all {@link InvoiceEntry}s of last month.
	 *
	 * @param member 	ID of {@link fitnessstudio.member.Member}
	 * @return new {@link List} of all related {@link InvoiceEntry}s.
	 */
	public List<InvoiceEntry> getAllInvoiceForMemberOfLastMonth(Long member) {
		LocalDate now = LocalDate.now();
		int month = now.getMonthValue();
		int year = now.getYear();

		if (month == 1) {
			year--;
			month = 13;
		}

		int finalMonth = month - 1;
		int finalYear = year;

		return invoiceEntries.findAllByMember(member)
			.filter(invoiceEntry -> invoiceEntry.getCreated().getMonthValue() == finalMonth
				&& invoiceEntry.getCreated().getYear() == finalYear)
			.toList();
	}

	/**
	 * Method to get all {@link InvoiceEntry}s of this {@link LocalDate}.
	 *
	 * @param date must not be {@literal null}.
	 * @return new {@link List} of all related {@link InvoiceEntry}s.
	 */
	public List<InvoiceEntry> getAllInvoicesOfDate(LocalDate date) {
		Assert.notNull(date, "Date must not be null.");

		return invoiceEntries.findAll().filter(invoiceEntry -> invoiceEntry.getCreated().equals(date)).toList();
	}

	/**
	 * Method to get all {@link InvoiceEntry}s for {@link fitnessstudio.member.Member} before the {@link LocalDate}.
	 *
	 * @see LocalDate
	 * @param member	ID of {@link fitnessstudio.member.Member}
	 * @param date must not be {@literal null}.
	 * @return new {@link List} of all related {@link InvoiceEntry}s.
	 */
	public List<InvoiceEntry> getAllEntriesForMemberBefore(Long member, LocalDate date) {
		Assert.notNull(date, "Date must not be null!");

		return invoiceEntries.findAllByMember(member).filter(invoiceEntry ->
			!invoiceEntry.getCreated().isAfter(date)).toList();
	}
}
