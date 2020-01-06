package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import fitnessstudio.staff.StaffRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class RosterManagement {

	private final RosterRepository rosterRepository;
	private final StaffManagement staffManagement;
	private static final Logger LOG = LoggerFactory.getLogger(RosterManagement.class);

	RosterManagement(RosterRepository rosterRepository, StaffManagement staffs) {
		Assert.notNull(rosterRepository, "Das RosterRepository darf nicht null sein!");
		Assert.notNull(staffs, "Das StaffManagement darf nicht null sein!");
		this.staffManagement = staffs;
		this.rosterRepository = rosterRepository;
	}

	public void deleteAllEntriesFromStaff(long staffId) {
		Staff staff = staffManagement.findById(staffId).orElse(null);
		if (staff == null) {
			return;
		}
		for (Roster roster : rosterRepository.findAll()) {
			for (TableRow row : roster.getRows()) {
				for (Slot slot : row.getSlots()) {
					deleteByStaff(slot, staffId);
				}
			}
			saveRoster(roster);
		}
	}

	private void deleteByStaff (Slot slot, long staffId){
		List<Integer> positions = new ArrayList<>();
		int i = 0;
		for (RosterEntry rosterEntry : slot.getEntries()) {
			if (rosterEntry.getStaff().getStaffId() == staffId) {
				positions.add(i);
			}
			i++;
		}
		for (int index : positions) {
			slot.getEntries().remove(index);
		}
	}

	private boolean checkData(Staff staff, Roster roster, RosterEntryForm form, Errors errors) {
		if (staff == null) {
			errors.rejectValue("roster.error.staff", "Dieser Staff existiert nicht!");
			return false ;
		}

		if (roster == null) {
			errors.rejectValue("week", "Für diese Woche gibt es keinen Roster.");
			return false ;
		}

		if (form.getTimes().isEmpty()) {
			errors.reject("time", "Bitte wähle mindestens eine Zeit aus!");
			return false ;
		}
		return true;
	}

	public void createEntry(RosterEntryForm form, long training, Errors errors) {
		Assert.notNull(form, "RosterForm darf nicht 'null' sein.");
		Assert.notNull(form.getTimes(), "Keine Liste gefunden.");
		Assert.notNull(form.getWeek(), "Keine Woche angegeben!");

		Staff staff = staffManagement.findById(form.getStaff()).orElse(null);
		Roster roster = getRosterByWeek(form.getWeek());
		StaffRole role = RosterDataConverter.stringToRole(form.getRole());
		int day = form.getDay();

		if (!checkData(staff, roster, form, errors )){
			return;
		}

		for (String time : form.getTimes()) {
			for (int i = 0; i < Roster.AMOUNT_ROWS; i++) {
				if (roster.getRows().get(i).toString().equals(time)) {
					try {
						RosterEntry rosterEntry = new RosterEntry(role, staff);
						roster.addEntry(i, day, rosterEntry);
						if (isTraining(training, role)) {
							rosterEntry.setTraining(training);
						}

					} catch (Exception e) {
						errors.reject("times", "Der Mitarbeiter arbeitet von " + time + " schon.");
					}
				}
			}
		}

		saveRoster(roster);
	}

	private boolean isTraining(long training, StaffRole role){
		return training != RosterEntry.NONE && role.equals(StaffRole.TRAINER);
	}

	public RosterEntry getRosterEntryById(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		Assert.isTrue(shift >= 0 && shift < Roster.AMOUNT_ROWS && day >= 0 && day < 7, "Dieser Slot existiert nicht!");
		for (RosterEntry rosterEntry : roster.getRows().get(shift).getSlots().get(day).getEntries()) {
			if (rosterEntry.getRosterEntryId() == id){
				return rosterEntry;
			}
		}
		return null;
	}

	public List<Integer> getNextWeeks() {
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

	public Roster getRosterByWeek(int week) {
		return rosterRepository.findByWeek(week).orElse(null);
	}

	public void editEntry(RosterEntryForm form, long id, Errors errors) {
		Roster roster = getRosterByWeek(form.getWeek());
		RosterEntry entry;
		Assert.notNull(roster, "Es gibt keinen Dienstplan für diese Woche!");
		String time = form.getTimes().iterator().next();
		for (int i = 0; i < roster.getRows().size(); i++) {
			if (roster.getRows().get(i).toString().equals(time)) {
				try {
					entry = getRosterEntryById(form.getWeek(), i, form.getDay(), id);
					entry.setRole(RosterDataConverter.stringToRole(form.getRole()));

				} catch (Exception e) {
					errors.reject("Edit", "Der Mitarbeiter hat zu dieser Zeit einen Termin.");
				}
			}
		}

		saveRoster(roster);
	}

	public void deleteEntryByTraining(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Keinen Roster gefunden.");
		List<RosterEntry> entries = roster.getRows().get(shift).getSlots().get(day).getEntries();
		for (RosterEntry entry : entries) {
			if (entry.getTraining() == id) {
				entries.remove(entry);
				break;
			}
		}
		saveRoster(roster);
	}

	public void deleteEntry(int week, int shift, int day, long id) {
		Roster roster = getRosterByWeek(week);
		Assert.notNull(roster, "Es gibt keinen Roster für diese Woche!");
		RosterEntry rosterEntry = getRosterEntryById(week, shift, day, id);
		Assert.notNull(rosterEntry, "Keinen RosterEntry gefunden.");
		try {
			roster.deleteEntry(shift, day, id);
		} catch (Exception ignore) {
			LOG.info("Eintrag konnte nicht gelöscht werden.");
		}
		saveRoster(roster);
	}

	public List<String> getTimes() {
		Roster roster = rosterRepository.findAll().iterator().next();
		if (roster == null) {
			return new ArrayList<>();
		}
		List<String> times = new ArrayList<>();
		for (TableRow row : roster.getRows()) {
			times.add(row.toString());
		}
		return times;
	}

	public int getTimeIndex(String time) {
		List<String> times = getTimes();
		for (int i = 0; i < getTimes().size(); i++) {
			if (times.get(i).equals(time)){
				return i;
			}
		}
		return -1;
	}

	public boolean isFree(RosterEntryForm form) {
		int shift = getTimeIndex(form.getTimes().get(0));
		return !getRosterByWeek(form.getWeek()).getRows().get(shift).getSlots()
				.get(form.getDay()).isTaken(staffManagement.findById(form.getStaff())
				.orElse(null));
	}


}
