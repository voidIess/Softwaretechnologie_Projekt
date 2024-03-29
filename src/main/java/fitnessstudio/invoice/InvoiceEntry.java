package fitnessstudio.invoice;

import org.hibernate.annotations.CreationTimestamp;
import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an entry on invoice PDF for {@link fitnessstudio.member.Member}s
 *
 * @author Bill Kippe
 * @version 1.0
 */
@Entity
public class InvoiceEntry {

	@Id
	@GeneratedValue
	private long invoiceEntryId;

	private long member;

	private InvoiceType type;

	@Lob
	private Money amount;

	private String description;

	@CreationTimestamp
	private LocalDate created;

	public InvoiceEntry() {
	}

	/**
	 * Creates a new {@link InvoiceEntry} instance with given variables.
	 *
	 * @param member		member of {@link InvoiceEntry}
	 * @param type			type of {@link InvoiceEntry}
	 * @param amount		{@link javax.money.MonetaryAmount} of {@link InvoiceEntry}
	 * @param description	description of {@link InvoiceEntry}
	 */
	public InvoiceEntry(Long member, InvoiceType type, Money amount, String description) {
		this.member = member;
		this.type = type;
		this.amount = amount;
		this.description = description;
	}

	public long getInvoiceEntryId() {
		return invoiceEntryId;
	}

	public long getMember() {
		return member;
	}

	public InvoiceType getType() {
		return type;
	}

	public Money getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof InvoiceEntry)) {
			return false;
		}
		InvoiceEntry that = (InvoiceEntry) o;

		return getInvoiceEntryId() == that.getInvoiceEntryId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getInvoiceEntryId());
	}
}
