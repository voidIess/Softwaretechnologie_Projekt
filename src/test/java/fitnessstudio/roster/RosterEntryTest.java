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

/**
 * Test fuer RosterEntry
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RosterEntryTest {

	private Staff staff;
	private Staff staff2;

	@Autowired
	private UserAccountManager userAccounts;

	private RosterEntry rosterEntryTrainer;
	private RosterEntry rosterEntryCounter;
	private RosterEntry rosterEntryTrainerWithTrainig;

	@BeforeAll
	void setup () {
		staff = new Staff(userAccounts.create("rosterEntryTestStaff", Password.UnencryptedPassword.of("123"), "rosterEntryTestStaff@email.de", Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staff2 = new Staff(userAccounts.create("rosterEntryTestStaff2", Password.UnencryptedPassword.of("123"), "rosterEntryTestStaff2@email.de", Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntryTrainer = new RosterEntry(StaffRole.TRAINER,staff);
		rosterEntryCounter = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntryTrainerWithTrainig = new RosterEntry(StaffRole.TRAINER,staff);

	}

	/**
	 * U-3-20
	 */
	@Test
	@Order(1)
	void constructorTest () {
		try{
			RosterEntry rosterEntry = new RosterEntry(StaffRole.TRAINER, null);
			fail("Der Staff darf nicht null sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * U-3-21
	 */
	@Test
	@Order(2)
	void compareToTest(){
		assertThat(rosterEntryTrainer.compareTo(rosterEntryCounter) > 0).isTrue();
		assertThat(rosterEntryCounter.compareTo(rosterEntryTrainer) < 0).isTrue();
	}

	/**
	 * U-3-22
	 */
	@Test
	@Order(3)
	void toStringTest () {
		assertThat(rosterEntryTrainer.toString().equals(rosterEntryTrainer.getStaff().getLastName()
		+ ", " + rosterEntryTrainer.getStaff().getFirstName()
		+ " " + rosterEntryTrainer.getStaff().getStaffId())).isTrue();
	}

	/**
	 * U-3-23
	 */
	@Test
	@Order(4)
	void isTrainerTest () {
		assertThat(rosterEntryTrainer.isTrainer()).isTrue();
		assertThat(rosterEntryCounter.isTrainer()).isFalse();
	}

	//TODO die annotation für die Order

	/**
	 * U-3-24
	 */
	@Test
	@Order(5)
	void testSetTraining () {
		try {
			rosterEntryCounter.setTraining(123);
			fail("Der RosterEntry hat den Typ Counter!");
		} catch (Exception ignore) {
			assertThat(rosterEntryCounter.getTraining() == RosterEntry.NONE).isTrue();
		}

	}

	/**
	 * U-3-25
	 */
	@Test
	@Order(6)
	void testSetRole() {
		rosterEntryTrainerWithTrainig.setTraining(123);
		try {
			rosterEntryTrainerWithTrainig.setRole(StaffRole.COUNTER);
			fail("Der Mitarbeiter hat zu dieser Zeit einen Termin!");
		} catch(Exception ignore) {
			assertThat(rosterEntryTrainerWithTrainig.getRole().equals(StaffRole.TRAINER)).isTrue();
		}
		rosterEntryTrainer.setRole(StaffRole.COUNTER);
		assertThat(rosterEntryTrainer.getRole().equals(StaffRole.COUNTER)).isTrue();
		rosterEntryTrainer.setRole(StaffRole.TRAINER);
		assertThat(rosterEntryTrainer.getRole().equals(StaffRole.TRAINER)).isTrue();
	}

	/**
	 * U-3-26
	 */
	@Test
	@Order(7)
	void roleToString () {
		assertThat(rosterEntryTrainer.roleToString().equals(RosterDataConverter.roleToString(StaffRole.TRAINER))).isTrue();
	}

}
