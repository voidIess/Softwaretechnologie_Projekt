package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRepository;
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
public class RosterEntryTest {


	@Autowired
	private UserAccountManager userAccountManager;

	private Staff staff;
	private RosterEntry rosterEntryCounter, rosterEntryTrainer;


	@BeforeAll
	void setUpEntries() {
		staff = new Staff(userAccountManager.create("TestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntryCounter = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntryTrainer = new RosterEntry(StaffRole.TRAINER, staff);
	}

	@Test
	void isTrainerTest(){
		assertThat(rosterEntryCounter.isTrainer()).isFalse();
		assertThat(rosterEntryTrainer.isTrainer()).isTrue();
	}

	@Test
	void compareToRoles () {
		assertThat(rosterEntryCounter.compareTo(rosterEntryTrainer) < 0).isTrue();
		assertThat(rosterEntryTrainer.compareTo(rosterEntryCounter) > 0).isTrue();
	}

	@Test
	void testToString () {
		assertThat(rosterEntryTrainer.toString().equals("Wieland, Markus " + staff.getStaffId()));
	}

	@Test
	void testRoleToString () {
		assertThat(rosterEntryCounter.roleToString().equals("Thekenkraft")).isTrue();
		assertThat(rosterEntryTrainer.roleToString().equals("Trainer")).isTrue();
	}

	@AfterAll
	void clear (){
		userAccountManager.delete(staff.getUserAccount());
		userAccountManager.delete(staff2.getUserAccount());
	}

}