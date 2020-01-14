package fitnessstudio.invoice;

import org.javamoney.moneta.Money;
import org.springframework.context.ApplicationEvent;

/**
 * Represents an event published when {@link fitnessstudio.member.CreditAccount} of {@link fitnessstudio.member.Member}
 * changes.
 *
 * @author Bill Kippe
 */
public class InvoiceEvent extends ApplicationEvent {
	private long member;
	private InvoiceType type;
	private Money amount;
	private String description;

	/**
	 * Creates a new {@link InvoiceEvent} instance with given parameters.
	 *
	 * @param source of {@link InvoiceEvent}
	 * @param member of {@link InvoiceEvent}
	 * @param type of {@link InvoiceEvent}
	 * @param amount of {@link InvoiceEvent}
	 * @param description of {@link InvoiceEvent}
	 */
	public InvoiceEvent(Object source, Long member, InvoiceType type, Money amount, String description) {
		super(source);
		this.member = member;
		this.type = type;
		this.amount = amount;
		this.description = description;
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
}
