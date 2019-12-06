package fitnessstudio.invoice;

import fitnessstudio.member.Member;
import org.javamoney.moneta.Money;
import org.springframework.context.ApplicationEvent;

public class InvoiceEvent extends ApplicationEvent {
	private Member member;
	private InvoiceType type;
	private Money amount;
	private String description;


	public InvoiceEvent(Object source, Member member, InvoiceType type, Money amount, String description) {
		super(source);
		this.member = member;
		this.type = type;
		this.amount = amount;
		this.description = description;
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
