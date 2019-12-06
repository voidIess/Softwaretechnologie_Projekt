package fitnessstudio.invoice;

import org.hibernate.annotations.CreationTimestamp;
import org.javamoney.moneta.Money;

import javax.persistence.*;
import java.time.LocalDateTime;

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
	private LocalDateTime created;

	public InvoiceEntry() {
	}

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

	public LocalDateTime getCreated() {
		return created;
	}
}
