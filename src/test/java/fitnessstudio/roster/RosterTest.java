package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class RosterTest {

	private Roster roster;

	private static int SHIFT = 1;
	private static int DAY = 2;

	@Autowired
	private UserAccountManager userAccounts;

	private RosterManager rosterManager;

	@Mock
	private SlotRepository slotRepository;

	@BeforeEach
	void setUp() {
		roster = new Roster(1);
		rosterManager = rosterManager.getInstance();
		rosterManager.setSlotRepository(slotRepository);
	}

	@Test
	void testInitRoster(){
		assertThat(roster.getRows().size()==roster.AMOUNT_ROWS).isTrue();
		for (TableRow tableRow : roster.getRows()){
			assertThat(tableRow.getSlots().size() == 7).isTrue();
		}
	}

}
