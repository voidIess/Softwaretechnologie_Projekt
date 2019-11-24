package fitnessstudio.roster;

import fitnessstudio.member.CreditAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RosterTest {

	private Roster roster;

	@BeforeEach
	void setUp() {
		roster = RosterManager.getRoster();
	}

	@Test
	void doesInitWork() {
		assertThat(roster == null).isTrue();
	}

}
