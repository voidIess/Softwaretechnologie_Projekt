package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class RosterEntry implements Comparable<RosterEntry> {

	@Id
	@GeneratedValue
	private long rosterEntryId;
	private StaffRole role;

	@OneToOne
	private Staff staff;

	RosterEntry() {
	}

	RosterEntry(StaffRole role, Staff staff) {
		Assert.notNull(staff, "Der Mitarbeiter darf nicht null sein!");
		this.role = role;
		this.staff = staff;
	}

	public long getRosterEntryId() {
		return rosterEntryId;
	}

	public StaffRole getRole() {
		return role;
	}

	public void setRole(StaffRole role) {
		this.role = role;
	}

	public boolean isTrainer() {
		return role == StaffRole.TRAINER;
	}

	public Staff getStaff() {
		return staff;
	}

	@Override
	public String toString() {
		return staff.getLastName() + ", " + staff.getFirstName() + " " + staff.getStaffId();
	}

	@Override    // Um im Dienstplan die Einträge nach den Aufgaben zu sortieren
	public int compareTo(RosterEntry rosterEntry) {
		if (rosterEntry.getRole().equals(StaffRole.COUNTER)) return 1;
		else return -1;
	}

}
