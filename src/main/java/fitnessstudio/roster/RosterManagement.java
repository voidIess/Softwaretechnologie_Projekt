package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class RosterManagement {

	private final RosterRepository rosterRepository;
	private final StaffManagement staffManagement;

	RosterManagement (RosterRepository rosterRepository, StaffManagement staffs) {
		Assert.notNull(rosterRepository, "Das RosterRepository darf nicht null sein!");
		Assert.notNull(staffs,"Das StaffManagement darf nicht null sein!");
		this.staffManagement = staffs;
		this.rosterRepository = rosterRepository;
	}

	public void createRosterEntry (RosterEntryForm form, Errors errors) {
		Assert.notNull(form, "RosterForm darf nicht 'null' sein.");
		Assert.notNull(form.getTimes(), "Keine Liste gefunden.");
		Assert.notNull(form.getWeek(), "Keine Woche angegeben!");

		Staff staff = staffManagement.findById(form.getStaff()).orElse(null);


		if (staff == null) {
			errors.rejectValue("roster.error.staff", "Dieser Staff existiert nicht!");
			return;
		}

		Roster roster = getRosterByWeek(form.getWeek());
		if (roster == null) {
			errors.rejectValue("week", "Für diese Woche gibt es keinen Roster.");
			return;
		}

		StaffRole role = RosterDataConverter.stringToRole(form.getRole());
		int day = 0;
		try {
			day = form.getDay();
		} catch (Exception e) {
			errors.rejectValue("roster.error.staff", "Fehler bei Eingabe des Tages!");
			return;
		}

		if (form.getTimes().isEmpty()) {
			errors.rejectValue("roster.error.time", "Bitte wähle mindestens eine Zeit aus!");
			return;
		}

		for (String time : form.getTimes()) {
			for (int i = 0;i<Roster.AMOUNT_ROWS;i++){
				if (roster.getRows().get(i).toString().equals(time)) {
					try {
						RosterEntry rosterEntry = new RosterEntry(role,staff);
						roster.addEntry(i,day, rosterEntry);
					} catch (Exception e) {
						errors.reject("times", "Der Mitarbeiter arbeiten von " + time + " schon.");
					}
				}
			}
		}

		saveRoster(roster);
	}

	public RosterEntry getRosterEntryById (int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		Assert.isTrue(shift >= 0 && shift < Roster.AMOUNT_ROWS && day >= 0 && day <7, "Dieser Slot existiert nicht!");
		for (RosterEntry rosterEntry : roster.getRows().get(shift).getSlots().get(day).getEntries()) {
			if (rosterEntry.getRosterEntryId() == id) return rosterEntry;
		}
		return null;
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

	public void editEntry (RosterEntryForm form, long id) {
		Roster roster = getRosterByWeek(form.getWeek());
		RosterEntry entry;
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		String time = form.getTimes().iterator().next();
		for (int i = 0; i < roster.getRows().size(); i++) {
			if (roster.getRows().get(i).toString().equals(time)) {
				try {
					entry = getRosterEntryById(form.getWeek(),i,form.getDay(), id);
					entry.setRole(RosterDataConverter.stringToRole(form.getRole()));
				} catch (Exception e) {
					System.out.println("Error.");
				}
			}
		}

		saveRoster(roster);
	}

	public void deleteEntry (int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Roster für diese Woche!");
		RosterEntry rosterEntry = getRosterEntryById(week, shift, day, id);
		Assert.notNull(rosterEntry, "Keinen RosterEntry gefunden.");
		roster.getRows().get(shift).getSlots().get(day).getEntries().remove(rosterEntry);
		saveRoster(roster);
	}

}
