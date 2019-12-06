package fitnessstudio.invoice;

import fitnessstudio.member.Member;
import org.javamoney.moneta.Money;

import javax.persistence.*;

@Entity
public class InvoiceEntry {

	@Id
	@GeneratedValue
	private long invoiceEntryId;

	@OneToOne
	private Member member;

	private InvoiceType type;

	@Lob
	private Money amount;

	private String description;

	public InvoiceEntry(Member member, InvoiceType type, Money amount, String description) {
		this.member = member;
		this.type = type;
		this.amount = amount;
		this.description = description;
	}

	public long getInvoiceEntryId() {
		return invoiceEntryId;
	}

	public Member getMember() {
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
}
