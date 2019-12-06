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
import java.util.LinkedList;
import java.util.List;

@Service
public class RosterManagement {

	private final RosterRepository rosterRepository;

	RosterManagement (RosterRepository rosterRepository, UserAccountManager userAccounts, StaffRepository staffs) {

		this.rosterRepository = rosterRepository;

		Roster roster = new Roster(1);
		rosterRepository.save(roster);

		//Staff staff = new Staff(userAccounts.create("staff", Password.UnencryptedPassword.of("123"), Role.of("STAFF")),"Markus", "Wieland", Money.of(100, "EUR"));
		//staffs.save(staff);
		//RosterEntry rosterEntry = new RosterEntry(StaffRole.TRAINER, staff);
		//roster.addEntry(1,1,rosterEntry);

		//rosterRepository.save(roster);

		List<String> strings = RosterDataConverter.getWeekDatesByWeek(1);

	}

	public void createRosterEntry (RosterEntryForm form) {

	}

	public List<Integer> getNextWeeks () {
		Iterable<Roster> rosters = rosterRepository.findAll();
		List<Integer> weeks = new LinkedList<>();
		for (Roster r : rosters) {
			weeks.add(r.getWeek());
		}
		return weeks;
	}

	public void saveRoster(Roster roster) {
		rosterRepository.save(roster);
	}

	public Roster getRosterByWeek (int week) {
		return rosterRepository.findByWeek(week).orElse(null);
	}






}
