package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class RosterEntry {

	@Id @GeneratedValue
	private long rosterEntryId;

	@OneToOne
	private Staff staff;
	private StaffRole role;

	RosterEntry () {}

	RosterEntry (Staff staff, StaffRole role) {
		this.staff = staff;
		this.role = role;
	}

	public long getRosterEntryId() {
		return rosterEntryId;
	}

	public Staff getStaff() {
		return staff;
	}

	public StaffRole getRole() {
		return role;
	}
}
