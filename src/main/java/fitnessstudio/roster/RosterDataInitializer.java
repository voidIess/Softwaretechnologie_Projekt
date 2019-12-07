package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffDataInitlializer;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class RosterDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(RosterDataInitializer.class);

	private final StaffRepository staffs;
	private final UserAccountManager userAccounts;
	private final RosterManagement rosterManagement;
	private final String STAFF_ROLE = "STAFF";

	RosterDataInitializer(RosterManagement rosterManagement, StaffRepository staffs, UserAccountManager userAccounts){
		this.rosterManagement = rosterManagement;
		this.userAccounts = userAccounts;
		this.staffs = staffs;
	}

	@Override
	public void initialize() {

		Staff staff = new Staff(userAccounts.create("staff", Password.UnencryptedPassword.of("123"), Role.of(STAFF_ROLE)),"Markus", "Wieland", Money.of(100, "EUR"));
		LOG.info("Create Staff (username: staff, passwort: 123)");

		Staff staff2 = new Staff(userAccounts.create("Obi", Password.UnencryptedPassword.of("123"), Role.of(STAFF_ROLE)),"Obi", "Babobi", Money.of(10000, "EUR"));
		LOG.info("Create Staff (username: obi, passwort: 123)");

		Staff staff3 = new Staff(userAccounts.create("aßmann", Password.UnencryptedPassword.of("123"), Role.of(STAFF_ROLE)),"Uwe", "Aßmann", Money.of(10000, "EUR"));
		LOG.info("Create Staff (username: aßmann, passwort: 123)");

		staffs.save(staff);
		staffs.save(staff2);
		staffs.save(staff3);

		int current_week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);


		for (int i = 0; i<6;i++) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.WEEK_OF_YEAR,i);
			int week = c.get(Calendar.WEEK_OF_YEAR);
			Roster roster = new Roster(week);
			RosterManager.saveRoster(roster);
			LOG.info("Roster für Woche: "+roster.getWeek());
			LOG.info("Roster ID: "+roster.getId());
		}

		rosterManagement.createRosterEntry(1,1,current_week,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(1,1,current_week,new RosterEntry(StaffRole.TRAINER, staff2));
		rosterManagement.createRosterEntry(1,2,current_week,new RosterEntry(StaffRole.COUNTER, staff2));
		rosterManagement.createRosterEntry(3,1,current_week,new RosterEntry(StaffRole.COUNTER, staff2));
		rosterManagement.createRosterEntry(3,3,current_week,new RosterEntry(StaffRole.COUNTER, staff3));
		rosterManagement.createRosterEntry(3,2,current_week,new RosterEntry(StaffRole.TRAINER, staff3));
		rosterManagement.createRosterEntry(3,4,current_week,new RosterEntry(StaffRole.TRAINER, staff2));
		rosterManagement.createRosterEntry(3,5,current_week,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(3,6,current_week,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(3,0,current_week,new RosterEntry(StaffRole.TRAINER, staff2));

	}
}
