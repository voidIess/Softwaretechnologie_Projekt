package fitnessstudio.invoice;

import org.javamoney.moneta.Money;
import org.springframework.context.ApplicationEvent;

public class InvoiceEvent extends ApplicationEvent {
	private long member;
	private InvoiceType type;
	private Money amount;
	private String description;


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
