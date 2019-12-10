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
public class RosterTest {

	private Staff staff, staff2;
	private RosterEntry rosterEntryTrainer, rosterEntryCounter, rosterEntryTrainerOtherStaff;
	private Roster roster;

	@Autowired
	private UserAccountManager userAccounts;

	@BeforeAll
	void setup(){
		staff = new Staff(userAccounts.create("rosterTestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staff2 = new Staff(userAccounts.create("rosterTestStaff2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntryTrainer = new RosterEntry(StaffRole.TRAINER,staff);
		rosterEntryCounter = new RosterEntry(StaffRole.COUNTER, staff);
		rosterEntryTrainerOtherStaff = new RosterEntry(StaffRole.TRAINER, staff2);
	}

	@Test
	@Order(1)
	void constructorTest () {
		try {
			roster = new Roster(-1);
			fail("Die Woche darf nicht negativ sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster = new Roster(0);
			fail("Die Woche muss größer sein als 0!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster = new Roster(53);
			fail("Die Woche muss kleiner sein als 53!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		roster = new Roster(1);
		assertThat(roster.getRows().size()==Roster.AMOUNT_ROWS).isTrue();

	}

	@Test
	@Order(2)
	void addEntry () {
		roster = new Roster(1);
		try {
			roster.addEntry(-1,1,rosterEntryTrainer);
			fail("Die Schicht darf nicht negativ sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.addEntry(Roster.AMOUNT_ROWS,1,rosterEntryTrainer);
			fail("Die Schicht darf nicht größer sein als die AMOUNT_ROWS");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.addEntry(2,-1,rosterEntryTrainer);
			fail("Der Tag darf nicht negativ sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.addEntry(2,7,rosterEntryTrainer);
			fail("Der Tag muss kleiner als 7 sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.addEntry(2,2, null);
			fail("Der RosterEntry darf nicht null sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		roster.addEntry(2,2,rosterEntryCounter);
		assertThat(roster.getRows().get(2).getSlots().get(2).getEntries().size() == 1 && roster.getRows().get(2).getSlots().get(2).getEntries().contains(rosterEntryCounter)).isTrue();

		try {
			roster.addEntry(2,2,rosterEntryTrainer);
			fail("Der Mitarbeiter sollte zu der Zeit bereits arbeiten!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test
	@Order(3)
	void testDelete() {
		assertThat(roster.getRows().get(2).getSlots().get(2).getEntries().contains(rosterEntryCounter)).isTrue();
		roster.deleteEntry(2,2,rosterEntryCounter.getRosterEntryId());
		assertThat(roster.getRows().get(2).getSlots().get(2).getEntries().contains(rosterEntryCounter)).isFalse();
		try {
			roster.deleteEntry(2,2,-1);
			fail("Dieser RosterEntry existiert nicht.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.deleteEntry(2,7,rosterEntryCounter.getRosterEntryId());
			fail("Der Tag darf nicht größer sein als 7.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.deleteEntry(Roster.AMOUNT_ROWS,2,rosterEntryCounter.getRosterEntryId());
			fail("Die Schicht existiert nicht!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			roster.deleteEntry(2,2,rosterEntryTrainer.getRosterEntryId());
			fail("Der RosterEntry befindet sich nicht in der Liste.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
