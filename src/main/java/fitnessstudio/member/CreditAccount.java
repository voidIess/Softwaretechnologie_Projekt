package fitnessstudio.member;

import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CreditAccount {
	@Lob
	private Money credit;

	public CreditAccount() {
		this.credit = Money.of(0, "EUR");
	}

	public void payIn(Money amount) {
		credit = credit.add(amount);
	}

	public Money payOut(Money amount) {
		if (credit.isLessThan(amount)) {
			credit = credit.add(credit.negate());
			return credit;
		}
		credit = credit.add(amount.negate());
		return credit;
	}

	public Money getCredit() {
		return this.credit;
	}


}
