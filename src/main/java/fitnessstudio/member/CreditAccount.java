package fitnessstudio.member;

import org.javamoney.moneta.Money;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * Represents the online credit account of a {@link Member}.
 *
 * @version 1.0
 * @author Bill Kippe
 */
@Embeddable
public class CreditAccount {
	private String iban;
	private String bic;

	/**
	 * credit balance in euro
	 */
	@Lob
	private Money credit;

	public CreditAccount() {
		this.credit = Money.of(0, "EUR");
	}

	/**
	 * Creates a new {@link CreditAccount} instance with the given IBAN and BIC.
	 *
	 * @param iban	IBAN of the member
	 * @param bic	BIC of the member
	 */
	public CreditAccount(String iban, String bic) {
		this();
		this.iban = iban;
		this.bic = bic;
	}

	/**
	 * Changes the IBAN and BIC of the {@link CreditAccount} to the given values.
	 *
	 * @param iban	IBAN of the member
	 * @param bic	BIC of the member
	 */
	public void update(String iban, String bic){
		this.iban = iban;
		this.bic = bic;
	}

	/**
	 * Loads the given amount of euro to the members {@link CreditAccount}.
	 * @param amount money to pay in in euro
	 */
	void payIn(Money amount) {
		credit = credit.add(amount);
	}

	/**
	 * Withdraws the given amount of euro from the members {@link CreditAccount}.
	 *
	 * @param amount money to withdraw in euro
	 * @return new credit balance
	 */
	Money payOut(Money amount) {
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
