package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class RosterManagement {

	private final RosterRepository rosterRepository;
	private final StaffRepository staffs;
	private final SlotRepository slots;
	private final RosterEntryRepository rosterEntryRepository;

	RosterManagement(RosterRepository rosterRepository, SlotRepository slots,StaffRepository staffRepository, RosterEntryRepository rosterEntryRepository) {
		Assert.notNull(rosterRepository, "RosterRepository darf nicht 'null' sein.");
		Assert.notNull(staffRepository, "StaffRepository darf nicht 'null' sein.");
		this.rosterRepository = rosterRepository;
		this.staffs = staffRepository;
		this.rosterEntryRepository = rosterEntryRepository;
		this.slots = slots;
	}

	public void createRosterEntry(RosterEntryForm rosterEntryForm, Errors result) {

		Staff staff = staffs.findById(Long.valueOf(rosterEntryForm.getStaff())).orElse(null);
		if (staff == null) {
			result.reject("roster.error.staff", "Dieser Staff existiert nicht.");
			return;
		}

		Roster roster = RosterManager.getRoster();
		StaffRole role;

		if (rosterEntryForm.getRole().equals("Trainer"))
			role = StaffRole.TRAINER;
		else role = StaffRole.COUNTER;

		int day = Integer.parseInt(rosterEntryForm.getDay());

		if(rosterEntryForm.getTimes().size() == 0) {
			result.reject("roster.error.time", "Bitte wähle mindestens eine Zeit aus.");
			return;
		}

		for (String time : rosterEntryForm.getTimes()) {
			for (int i = 0; i < roster.getRows().size(); i++) {
				if (roster.getRows().get(i).getTime().equals(time)) {
					try {
						RosterEntry rosterEntry = new RosterEntry(role, staff);
						roster.addEntry(i, day, rosterEntry);
					} catch (Exception e) {
						result.reject("times", "Der Mitarbeiter arbeitet um " + time + " schon.");
					}
				}
			}
		}
	}

	public void editRosterEntry (RosterEntryForm form, Errors result){
		RosterEntry rosterEntry = RosterManager.getEntryById(Long.parseLong(form.getDay()));
		rosterEntry.setRole(RosterManager.getRoleByString(form.getRole()));
		RosterManager.saveRosterEntry(rosterEntry);
	}

	public void createRosterEntry(int day, int shift, RosterEntry rosterEntry) {
		Roster roster = RosterManager.getRoster();
		roster.addEntry(day, shift, rosterEntry);
		rosterRepository.save(roster);
	}

	public void deleteRosterEntry(Long slotId, Long id) {
		Slot slot = RosterManager.getSlotById(slotId);
		Assert.notNull(slot, "Es gibt keinen Slot mit dieser ID");

		Optional<RosterEntry> roster = rosterEntryRepository.findById(id);
		RosterEntry rosterEntry1 = roster.orElse(null);
		if (rosterEntry1 != null)
			slot.deleteEntry(rosterEntry1);
		RosterManager.saveSlot(slot);
	}
}
