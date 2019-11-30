package fitnessstudio.member;

import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class CreditAccount {
	String iban;
	String bic;
	@Lob
	private Money credit;

	public CreditAccount() {
		this.credit = Money.of(0, "EUR");
	}

	public CreditAccount(String iban, String bic) {
		this();
		this.iban = iban;
		this.bic = bic;
	}

	public void payIn(Money amount) {
		credit = credit.add(amount);
	}

	public Money payOut(Money amount) {
		if (credit.isLessThan(amount)) {
			Money oldCredit = credit;
			credit = Money.of(0, "EUR");
			return oldCredit;
		}
		credit = credit.add(amount.negate());
		return credit;
	}

	public Money getCredit() {
		return this.credit;
	}

	public String getIban() {
		return iban;
	}

	public String getBic() {
		return bic;
	}
}
