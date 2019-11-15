package fitnessstudio.member;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CreditAccount {
	@Lob
	private Money credit;

	public CreditAccount() {
		this.credit = Money.of(0, "EUR");
	}

	public MonetaryAmount payIn(Money amount) {
		return credit.add(amount);
	}

	public Money payOut(Money amount) {
		if (credit.isLessThan(amount)) {
			return credit.add(credit.negate());
		}
		return credit.add(amount.negate());
	}

	public Money getCredit() {
		return this.credit;
	}


}
