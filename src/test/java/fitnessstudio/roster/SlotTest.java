package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SlotTest {


	@Autowired
	private UserAccountManager userAccountManager;

	private Staff staff, staff2;
	private Slot slot;
	private RosterEntry rosterEntryCounter, rosterEntryTrainer;

	@BeforeAll
	void setUp(){
		staff = new Staff(userAccountManager.create("TestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staff2 = new Staff(userAccountManager.create("TestStaff2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntryCounter = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntryTrainer = new RosterEntry(StaffRole.TRAINER, staff);
		slot = new Slot();

	}

	@Test
	void testIsTaken(){
		assertThat(slot.isTaken(staff)).isFalse();
		assertThat(slot.isTaken(staff2)).isFalse();
		//slot.addEntry(rosterEntryCounter);
		//assertThat(slot.isTaken(staff)).isTrue();
	}

	@AfterAll
	void clear (){
		userAccountManager.delete(staff.getUserAccount());
		userAccountManager.delete(staff2.getUserAccount());
	}


	//istaken
	//add and delete entry
	//ToString
	//getSortedList
}
