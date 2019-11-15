package fitnessstudio.staff;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class RosterManagement {

	private final RosterRepository rosters;
	private final StaffRepository staffs;

	RosterManagement(RosterRepository rosters, StaffRepository staffs) {
		Assert.notNull(rosters, "RosterRepository must not be null!");
		Assert.notNull(staffs, "StaffRepository must not be null!");
		this.rosters = rosters;
		this.staffs = staffs;
	}

	public RosterEntry createRosterEntry(RosterEntryForm form, Errors result) {
		Assert.notNull(form, "Registration form must not be null");

		String date = form.getDate().replace("T", " ");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

		Optional<Staff> staff = staffs.findById(Long.valueOf(form.getStaffs()));

		Staff current_staff = staff.orElse(null);

		if (current_staff == null) {
			result.rejectValue("staffs", "Kein Staff mit dieser ID gefunden");
			return null;
		}

		int duration = Integer.parseInt(form.getDuration());

		StaffRole role;
		if(form.getRoles().toLowerCase().equals("trainer")){
			role = StaffRole.TRAINER;
		} else {
			role = StaffRole.COUNTER;
		}

		return rosters.save(new RosterEntry(role,current_staff,dateTime, duration));
		//RosterEntry(StaffRole role, Staff staff, LocalDateTime startTime, int duration)
	}
}
