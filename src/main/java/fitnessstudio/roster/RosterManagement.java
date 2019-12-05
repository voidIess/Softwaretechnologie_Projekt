package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RosterManagement {

	public void saveRoster (Roster roster) {

	}

	RosterManagement (RosterRepository rosterRepository, UserAccountManager userAccounts, StaffRepository staffs) {
		Roster roster = new Roster(1);
		rosterRepository.save(roster);

		Staff staff = new Staff(userAccounts.create("staff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		staffs.save(staff);
		RosterEntry rosterEntry = new RosterEntry(staff, StaffRole.TRAINER);
		roster.addEntry(1,1,rosterEntry);

		rosterRepository.save(roster);



	}



}
