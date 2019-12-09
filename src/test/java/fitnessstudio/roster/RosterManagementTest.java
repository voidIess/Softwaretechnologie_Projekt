package fitnessstudio.roster;


import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.hibernate.Hibernate;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RosterManagementTest {

	@Autowired
	private RosterManagement rosterManagement;

	@Autowired
	private UserAccountManager userAccounts;

	private RosterEntryForm rosterEntryCounter, rosterEntryTrainer, rosterEntryOtherStaff;
	private Staff staffTrainer;
	private Staff staffCounter;
	private List<String> times;
	private Roster roster;

	@BeforeAll
	void setup() {

		staffTrainer = new Staff(userAccounts.create("rmTestStaff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staffCounter = new Staff(userAccounts.create("rmTestStaff2", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));

		times = new ArrayList<>();

		/*times.add("06:00-12:00");

		rosterEntryCounter = new RosterEntryForm(
			staffCounter.getStaffId(),
			RosterDataConverter.roleToString(StaffRole.COUNTER),
			1,
			times,
			Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
		);*/

	}

	@Test
	@Order(1)
	void testGetTimes () {
		roster = rosterManagement.getRosterByWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		assertThat(roster != null).isTrue();
		Hibernate.initialize(roster.getRows());
		times = rosterManagement.getTimes();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		assertThat(times.size() == Roster.AMOUNT_ROWS).isTrue();
		for (int i = 0; i<Roster.AMOUNT_ROWS;i++) {
			LocalDateTime timeObj = Roster.STARTTIME.plusMinutes(Roster.DURATION*i);
			String time = timeObj.format(formatter) + "-" + timeObj.plusMinutes(Roster.DURATION).format(formatter);
			assertThat(times.get(i).equals(time)).isTrue();
		}
	}

}
