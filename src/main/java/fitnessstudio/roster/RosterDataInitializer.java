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
		Roster roster = new Roster(1);
		RosterManager.saveRoster(roster);
		LOG.info("Roster ID: "+roster.getId());

		rosterManagement.createRosterEntry(1,1,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(1,1,new RosterEntry(StaffRole.TRAINER, staff2));
		rosterManagement.createRosterEntry(1,2,new RosterEntry(StaffRole.COUNTER, staff2));
		rosterManagement.createRosterEntry(3,1,new RosterEntry(StaffRole.COUNTER, staff2));
		rosterManagement.createRosterEntry(3,3,new RosterEntry(StaffRole.COUNTER, staff3));
		rosterManagement.createRosterEntry(3,2,new RosterEntry(StaffRole.TRAINER, staff3));
		rosterManagement.createRosterEntry(3,4,new RosterEntry(StaffRole.TRAINER, staff2));
		rosterManagement.createRosterEntry(3,5,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(3,6,new RosterEntry(StaffRole.COUNTER, staff));
		rosterManagement.createRosterEntry(3,0,new RosterEntry(StaffRole.TRAINER, staff2));

	}
}
