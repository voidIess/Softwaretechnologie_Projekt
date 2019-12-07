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
public class SlotTest {

	private Staff staff, staff2;
	private RosterEntry rosterEntry, rosterEntry2;
	private Slot slot;

	@Autowired
	private UserAccountManager userAccounts;

	@BeforeAll
	void setup () {
		staff = new Staff(userAccounts.create("slotTestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staff2 = new Staff(userAccounts.create("slotTestStaff2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		rosterEntry = new RosterEntry(StaffRole.TRAINER,staff);
		rosterEntry2 = new RosterEntry(StaffRole.COUNTER, staff);
	}

	@Test
	@Order(1)
	void constructorTest () {
		try {
			slot = new Slot(-1, 0);
			fail("Die Schicht darf nicht negativ sein!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			slot = new Slot (Roster.AMOUNT_ROWS,1);
			fail("Diese Schicht existiert nicht.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			slot = new Slot (1, -1);
			fail("Der Tag darf nicht negativ sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			slot = new Slot (1, 7);
			fail("Der Tag muss kleiner als 7 sein! Informatiker fangen nämlich bei Null an zu zählen du Depp!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Slot slot = new Slot (1,1);

	}

	@Test
	@Order(2)
	void testDeleted () {
		slot = new Slot (1,1);
		assertThat(slot.getEntries().size() == 0).isTrue();
		slot.getEntries().add(rosterEntry);
		assertThat(slot.getEntries().size() == 1).isTrue();
		assertThat(slot.deleteEntry(rosterEntry.getRosterEntryId())).isTrue();
		assertThat(slot.getEntries().size() == 0).isTrue();
		assertThat(slot.deleteEntry(rosterEntry.getRosterEntryId())).isFalse();

		slot.getEntries().add(rosterEntry);
		assertThat(slot.deleteEntry(-1)).isFalse();
		assertThat(slot.getEntries().size()==1).isTrue();
	}

	@Test
	@Order(3)
	void isTaken () {
		slot = new Slot(1,1);
		slot.getEntries().add(rosterEntry);
		assertThat(slot.isTaken(staff)).isTrue();
		assertThat(slot.isTaken(staff2)).isFalse();
	}

	@Test
	void testSortedList () {
		slot = new Slot(1,1);
		slot.getEntries().add(rosterEntry);
		slot.getEntries().add(rosterEntry2);

		assertThat(slot.getEntries().get(0).equals(rosterEntry2)).isTrue();

		slot = new Slot(1,1);
		slot.getEntries().add(rosterEntry2);
		slot.getEntries().add(rosterEntry);

		assertThat(slot.getEntries().get(0).equals(rosterEntry2)).isTrue();
	}

	@Test
	void testCoordinates () {
		int shift = 1;
		int day = 3;
		slot = new Slot(shift, day);
		assertThat(slot.getCoordinates()[0]==shift).isTrue();
		assertThat(slot.getCoordinates()[1] == day).isTrue();
	}


}
