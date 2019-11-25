package fitnessstudio.roster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RosterTest {

	private Roster roster;

	@BeforeEach
	void setUp() {
		roster = new Roster(1);
	}

	@Test
	void testInitRoster(){
		assertThat(roster.getRows().size()==roster.AMOUNT_ROWS).isTrue();
		for (TableRow tableRow : roster.getRows()){
			assertThat(tableRow.getSlots().size() == 7).isTrue();
		}
	}

}
