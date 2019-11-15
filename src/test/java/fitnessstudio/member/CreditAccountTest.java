package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;

import static org.assertj.core.api.Assertions.assertThat;

class CreditAccountTest {

	private CreditAccount creditAccount;

	@BeforeEach
	void setUp() {
		creditAccount = new CreditAccount();
	}

	@Test
	@Order(1)
	void creditIsZeroAfterCreating() {
		assertThat(creditAccount.getCredit().isZero()).isTrue();
	}

	@Test
	@Order(2)
	void payOutShouldBeZeroWhenCreditZero() {
		Money amount = Money.of(100, "EUR");
		assertThat(creditAccount.payOut(amount).isZero()).isTrue();
	}


	@Test
	@Order(3)
	void payInTest() {
		Money amount = Money.of(50, "EUR");
		MonetaryAmount oldCredit = creditAccount.getCredit();

		creditAccount.payIn(amount);
		assertThat(creditAccount.getCredit().isEqualTo(oldCredit.add(amount)));
	}

	@Test
	@Order(4)
	void payOutTest() {
		Money amount = Money.of(10, "EUR");
		MonetaryAmount oldCredit = creditAccount.getCredit();

		creditAccount.payOut(amount);
		assertThat(creditAccount.getCredit().isEqualTo(oldCredit.add(amount.negate())));
	}

	@Test
	@Order(5)
	void creditShouldBeZeroWhenPayOutToBig() {
		Money amount = Money.of(100, "EUR");
		creditAccount.payOut(amount);
		assertThat(creditAccount.getCredit().isZero()).isTrue();
	}

}