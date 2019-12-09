package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@Component
public class RosterDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(RosterDataInitializer.class);
	private final StaffManagement staffs;
	private final RosterManagement rosters;
	private final UserAccountManager userAccounts;
	private String STAFFROLE = "STAFF";

	RosterDataInitializer(StaffManagement staffs, RosterManagement rosterManagement, UserAccountManager userAccounts) {
		this.staffs = staffs;
		this.rosters = rosterManagement;
		this.userAccounts = userAccounts;
	}

	@Override
	public void initialize() {
		Staff staff = new Staff(userAccounts.create("staff", Password.UnencryptedPassword.of("123"), Role.of(STAFFROLE)), "Markus", "Wieland", Money.of(100, "EUR"));
		LOG.info("Create Staff (username: staff, passwort: 123)");

		Staff staff2 = new Staff(userAccounts.create("Obi", Password.UnencryptedPassword.of("123"), Role.of(STAFFROLE)), "Obi", "Babobi", Money.of(10000, "EUR"));
		LOG.info("Create Staff (username: obi, passwort: 123)");

		staffs.saveStaff(staff);
		staffs.saveStaff(staff2);
		for (int i = 0; i < 6; i++) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.WEEK_OF_YEAR, i);
			int week = c.get(Calendar.WEEK_OF_YEAR);
			Roster roster = new Roster(week);
			rosters.saveRoster(roster);
			LOG.info("Roster für Woche: " + roster.getWeek());
			LOG.info("Roster ID: " + roster.getRosterId());

			List<String> times = new ArrayList<>();
			times.add(roster.getRows().get(1).toString());
			RosterEntryForm form = new RosterEntryForm(
				staff.getStaffId(),
				RosterDataConverter.roleToString(StaffRole.TRAINER),
				1,
				times,
				c.get(Calendar.WEEK_OF_YEAR)
			);
			rosters.createEntry(form, -1, null);

		}


	}
}
