package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RosterEntryTest {

	private Staff staff;
	private Staff staff2;

	@Autowired
	private UserAccountManager userAccounts;

	private RosterEntry rosterEntryTrainer;
	private RosterEntry rosterEntryCounter;
	private RosterEntry rosterEntryTest;

	@BeforeAll
	void setup () {
		staff = new Staff(userAccounts.create("rosterEntryTestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staff2 = new Staff(userAccounts.create("rosterEntryTestStaff2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntryTrainer = new RosterEntry(StaffRole.TRAINER,staff);
		rosterEntryCounter = new RosterEntry(StaffRole.COUNTER, staff);
	}

	@Test
	@Order(1)
	void constructorTest () {
		try{
			rosterEntryTest = new RosterEntry(StaffRole.TRAINER, null);
			fail("Der Staff darf nicht null sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	@Order(2)
	void compareToTest(){
		assertThat(rosterEntryTrainer.compareTo(rosterEntryCounter) > 0).isTrue();
		assertThat(rosterEntryCounter.compareTo(rosterEntryTrainer) < 0).isTrue();
	}

	@Test
	@Order(3)
	void toStringTest () {
		assertThat(rosterEntryTrainer.toString().equals(rosterEntryTrainer.getStaff().getLastName()
		+ ", " + rosterEntryTrainer.getStaff().getFirstName()
		+ " " + rosterEntryTrainer.getStaff().getStaffId())).isTrue();
	}

	@Test
	@Order(4)
	void isTrainerTest () {
		assertThat(rosterEntryTrainer.isTrainer()).isTrue();
		assertThat(rosterEntryCounter.isTrainer()).isFalse();
	}



}
