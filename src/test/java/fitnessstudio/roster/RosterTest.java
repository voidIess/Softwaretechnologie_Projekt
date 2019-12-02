package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class RosterTest {

	private Roster roster;

	private static int SHIFT = 1;
	private static int DAY = 2;
	private long SLOTID;

	@Autowired
	private UserAccountManager userAccounts;

	@Autowired
	private StaffRepository staffRepository;

	private Staff staff, staff2;
	private RosterEntry rosterEntry, rosterEntrySameStaff, rosterEntryOtherStaff;

	@Autowired
	private RosterManagement rosterManagement;

	@BeforeAll
	void setUpEntries(){
		staff = new Staff(userAccounts.create("TestStaff_Roster1", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staffRepository.save(staff);

		staff2 = new Staff(userAccounts.create("TestStaff_Roster2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staffRepository.save(staff2);

		rosterEntry = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntrySameStaff = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntryOtherStaff = new RosterEntry(StaffRole.COUNTER, staff2);


	}

	@BeforeEach
	void setup(){
		roster = new Roster(1);
	}

	@Test
	@Order(1)
	void testInitRoster(){
		assertThat(roster.getRows().size()==roster.AMOUNT_ROWS).isTrue();
		for (TableRow tableRow : roster.getRows()){
			assertThat(tableRow.getSlots().size() == 7).isTrue();
		}
	}
	
}
