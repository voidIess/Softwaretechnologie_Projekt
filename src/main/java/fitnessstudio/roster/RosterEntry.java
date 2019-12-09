package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import fitnessstudio.training.Training;

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

	//@OneToOne
	private long training;

	@OneToOne
	private Staff staff;

	RosterEntry() {
		this.training = -1;
	}

	RosterEntry(StaffRole role, Staff staff) {
		this();
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
		if (role.equals(StaffRole.COUNTER))
			Assert.isTrue(training == -1, "Der Mitarbeiter hat zu dieser Zeit einen Termin.");
		this.role = role;
	}

	public boolean isTrainer() {
		return role == StaffRole.TRAINER;
	}

	public Staff getStaff() {
		return staff;
	}

	public String roleToString () {
		return RosterDataConverter.roleToString(role);
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

	public void setTraining (long training) {
		Assert.isTrue(role.equals(StaffRole.TRAINER), "Der Mitarbeiter muss als Trainer arbeiten!");
		this.training = training;
	}

	public long getTraining () {
		return training;
	}

}
