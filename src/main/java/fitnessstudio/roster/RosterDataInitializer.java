package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
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
	private final RosterRepository rosterRepository;
	private final UserAccountManager userAccounts;


	RosterDataInitializer(RosterRepository rosterRepository, StaffManagement staffs, RosterManagement rosterManagement, UserAccountManager userAccounts) {
		Assert.notNull(rosterManagement, "RosterManagement must not be 'null'");
		Assert.notNull(rosterRepository, "RosterRepository must not be 'null'");
		Assert.notNull(staffs, "StaffManagement must not be 'null'");
		Assert.notNull(userAccounts, "UserAccountManager must not be 'null'");
		this.staffs = staffs;
		this.rosters = rosterManagement;
		this.userAccounts = userAccounts;
		this.rosterRepository = rosterRepository;
	}

	@Override
	public void initialize() {
		String staffrole = "STAFF";
		String usernameStaff = "staff";
		String usernameObi = "Obi";

		if (userAccounts.findByUsername("staff").isPresent()) {
			usernameStaff = "mar14511";
		}
		if (userAccounts.findByUsername("obi").isPresent()) {
			usernameObi = "mar14512";
		}

		Staff staff = new Staff(userAccounts.create(usernameStaff, Password.UnencryptedPassword.of("123"),"markus@email.de", Role.of(staffrole)), "Markus", "Wieland", Money.of(100, "EUR"));
		String logStaff = "Create Staff (username: "+usernameStaff + ", passwort: 123)";
		LOG.info(logStaff);

		Staff staff2 = new Staff(userAccounts.create(usernameObi, Password.UnencryptedPassword.of("123"), "obi@mehralsbaumarkt.de", Role.of(staffrole)), "Obi", "Babobi", Money.of(10000, "EUR"));
		String logObi = "Create Staff (username: "+usernameObi + ", passwort: 123)";
		LOG.info(logObi);

		staffs.saveStaff(staff);
		staffs.saveStaff(staff2);

		for (int i = 0; i < 6; i++) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.WEEK_OF_YEAR, i);
			int week = c.get(Calendar.WEEK_OF_YEAR);
			if(!rosterRepository.findByWeek(week).isPresent()) {
				Roster roster = new Roster(week);
				rosters.saveRoster(roster);
				String logRoster = "Roster für Woche: " + roster.getWeek();
				LOG.info(logRoster);
				String logEntry = "Roster ID: " + roster.getRosterId();
				LOG.info(logEntry);

				List<String> times = new ArrayList<>();
				times.add(roster.getRows().get(1).toString());
				RosterEntryForm form = new RosterEntryForm(
					staff.getStaffId(),
					RosterDataConverter.roleToString(StaffRole.TRAINER),
					1,
					times,
					c.get(Calendar.WEEK_OF_YEAR)
				);
				rosters.createEntry(form, RosterEntry.NONE, null);
			}
		}
	}
}
