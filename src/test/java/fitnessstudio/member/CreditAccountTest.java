package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditAccountTest {

	private CreditAccount creditAccount;
	private String currency = "EUR";

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
		Money amount = Money.of(100, currency);
		assertThat(creditAccount.payOut(amount).isZero()).isTrue();
	}


	@Test
	@Order(3)
	void payInTest() {
		creditAccount.payIn(Money.of(50, currency));
		assertThat(creditAccount.getCredit().isEqualTo(Money.of(50, currency))).isTrue();
	}

	@Test
	@Order(4)
	void payOutTest() {
		creditAccount.payIn(Money.of(40, currency));
		creditAccount.payOut(Money.of(30, currency));
		assertThat(creditAccount.getCredit().isEqualTo(Money.of(10, currency))).isTrue();
	}

	@Test
	@Order(5)
	void creditShouldBeZeroWhenPayOutToBig() {
		Money amount = Money.of(40, currency);
		creditAccount.payIn(amount);
		assertThat(creditAccount.payOut(Money.of(100, currency)).isEqualTo(amount)).isTrue();
	}

}