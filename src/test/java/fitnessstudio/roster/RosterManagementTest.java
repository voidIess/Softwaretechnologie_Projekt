package fitnessstudio.roster;


import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.hibernate.Hibernate;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * UnitTest Rostermanagement
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RosterManagementTest {

	@Autowired
	private RosterManagement rosterManagement;

	@Autowired
	private StaffRepository staffManagement;

	@Autowired
	private UserAccountManager userAccounts;

	private RosterEntryForm rosterEntryCounter;
	private RosterEntryForm rosterEntryTrainer;
	private RosterEntryForm rosterEntryCounterOtherStaff;
	private RosterEntryForm rosterEntryError1;
	private RosterEntryForm rosterEntryError2;
	private RosterEntryForm rosterEntryError3;
	private RosterEntryForm rosterEntryErrorNoStaff;
	private RosterEntryForm rosterEntryErrorNoRoster;

	private Staff staffTrainer;
	private Staff staffCounter;
	private List<String> times;
	private Roster roster;

	private int day = 0;
	private int week;

	@BeforeAll
	void setup() {

		staffTrainer = new Staff(userAccounts.create("rmTestStaff", Password.UnencryptedPassword.of("123"), "rmTestStaff@email.de", Role.of("STAFF")), "Markus", "Wieland", Money.of(100, "EUR"));
		staffCounter = new Staff(userAccounts.create("rmTestStaff2", Password.UnencryptedPassword.of("123"), "rmTestStaff22@email.de", Role.of("STAFF")), "Markus", "Wieland", Money.of(100, "EUR"));
		staffManagement.save(staffTrainer);
		staffManagement.save(staffCounter);

		week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

		roster = rosterManagement.getRosterByWeek(week);
		assertThat(roster != null).isTrue();
		Hibernate.initialize(roster.getRows());
		for (TableRow tableRow : roster.getRows()) {
			Hibernate.initialize(tableRow.getSlots());
			for (Slot slot : tableRow.getSlots()) {
				Hibernate.initialize(slot.getEntries());
			}
		}
		times = rosterManagement.getTimes();

		List<String> timeList = new ArrayList<>();
		timeList.add(times.get(0));

		rosterEntryCounter = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			timeList,
			week
		);
		rosterEntryTrainer = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.TRAINER),
			day,
			timeList,
			week
		);
		rosterEntryCounterOtherStaff = new RosterEntryForm(
			staffTrainer.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			4,
			timeList,
			week
		);
		rosterEntryError1 = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			timeList,
			null
		);
		rosterEntryError2 = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			new ArrayList<>(),
			week
		);
		rosterEntryError3 = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			null,
			week
		);
		rosterEntryErrorNoRoster = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			times,
			-1
		);
		rosterEntryErrorNoStaff = new RosterEntryForm(
			-100L,
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			day,
			times,
			week
		);
	}

	/**
	 * U-3-15
	 */
	@Test
	@Order(1)
	void testGetTimes() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		assertThat(times.size() == Roster.AMOUNT_ROWS).isTrue();
		for (int i = 0; i < Roster.AMOUNT_ROWS; i++) {
			LocalDateTime timeObj = Roster.STARTTIME.plusMinutes(Roster.DURATION * i);
			String time = timeObj.format(formatter) + "-" + timeObj.plusMinutes(Roster.DURATION).format(formatter);
			assertThat(times.get(i).equals(time)).isTrue();
		}
	}

	/**
	 * U-3-16
	 */
	@Test
	@Order(2)
	void testGetTimeIndex() {
		int index = 1;
		String time = times.get(index);
		assertThat(rosterManagement.getTimeIndex(time) == index).isTrue();
	}

	/**
	 * U-3-17
	 */
	@Test
	@Order(3)
	void testCreateEntry() {

		int before = roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounter.getTimes().get(0))).getSlots().get(rosterEntryCounter.getDay()).getEntries().size();
		rosterManagement.createEntry(rosterEntryCounter, RosterEntry.NONE, null);
		rosterManagement.saveRoster(roster);
		roster = rosterManagement.getRosterByWeek(rosterEntryCounter.getWeek());
		assertThat(roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounter.getTimes().get(0))).getSlots().get(rosterEntryCounter.getDay()).getEntries().size() == before + 1).isTrue();

		try {
			rosterManagement.createEntry(rosterEntryErrorNoRoster, RosterEntry.NONE, null);
			fail("Dieser Roster existiert nicht.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			rosterManagement.createEntry(rosterEntryErrorNoStaff, RosterEntry.NONE, null);
			fail("Dieser Staff exisitiert nicht!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			rosterManagement.createEntry(rosterEntryCounter, RosterEntry.NONE, null);
			fail("Der Staff arbeitet schon");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		assertThat(roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounter.getTimes().get(0))).getSlots().get(rosterEntryCounter.getDay()).getEntries().size() == 1).isTrue();

		try {
			rosterManagement.createEntry(null, RosterEntry.NONE, null);
			fail("Der RosterEntry darf nicht 'null' sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			rosterManagement.createEntry(rosterEntryError1, RosterEntry.NONE, null);
			fail("Die Woche darf nicht 'null' sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			rosterManagement.createEntry(rosterEntryError2, RosterEntry.NONE, null);
			fail("Der Liste darf nicht leer sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			rosterManagement.createEntry(rosterEntryError3, RosterEntry.NONE, null);
			fail("Die Liste darf nicht 'null' sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * U-3-18
	 */
	@Test
	@Order(4)
	void testIsFree() {
		assertThat(rosterManagement.isFree(rosterEntryCounterOtherStaff)).isTrue();
		rosterManagement.createEntry(rosterEntryCounterOtherStaff, -1, null);
		roster = rosterManagement.getRosterByWeek(rosterEntryCounterOtherStaff.getWeek());
		assertThat(roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounterOtherStaff.getTimes().get(0))).getSlots().get(rosterEntryCounterOtherStaff.getDay()).getEntries().size() == 1).isTrue();
		Staff staff = roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounterOtherStaff.getTimes().get(0))).getSlots().get(rosterEntryCounterOtherStaff.getDay()).getEntries().get(0).getStaff();
		assertThat(roster.getRows().get(rosterManagement.getTimeIndex(rosterEntryCounterOtherStaff.getTimes().get(0))).getSlots().get(rosterEntryCounterOtherStaff.getDay()).getEntries().get(0).getStaff().getStaffId() == staffTrainer.getStaffId()).isTrue();
		assertThat(rosterManagement.isFree(rosterEntryCounterOtherStaff)).isFalse();
	}

	/**
	 * U-3-19
	 */
	@Test
	@Order(5)
	void testGetNextWeeks() {
		List<Integer> list = rosterManagement.getNextWeeks();
		List<Integer> compare = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.WEEK_OF_YEAR, i);
			compare.add(c.get(Calendar.WEEK_OF_YEAR));
		}

		for (int i = 0; i < list.size(); i++) {
			System.out.println(compare.get(i));
			System.out.println(list.get(i));
			assertThat(compare.get(i).equals(list.get(i))).isTrue();
		}
	}
}
