package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
public class RosterEntry implements Comparable<RosterEntry> {

	public static final long NONE = -1;

	@Id
	@GeneratedValue
	private long rosterEntryId;
	private StaffRole role;
	private long training;

	@OneToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
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
		if (role.equals(StaffRole.COUNTER)) {
			Assert.isTrue(training == NONE, "Der Mitarbeiter hat zu dieser Zeit einen Termin.");
		}
		this.role = role;
	}

	public boolean isTrainer() {
		return role == StaffRole.TRAINER;
	}

	public Staff getStaff() {
		return staff;
	}

	public String roleToString() {
		return RosterDataConverter.roleToString(role);
	}

	@Override
	public String toString() {
		return staff.getLastName() + ", " + staff.getFirstName() + " " + staff.getStaffId();
	}

	@Override    // Um im Dienstplan die Einträge nach den Aufgaben zu sortieren
	public int compareTo(RosterEntry rosterEntry) {
		if (rosterEntry.getRole().equals(StaffRole.COUNTER)) {
			return 1;
		} else {
			return -1;
		}
	}

	public void setTraining(long training) {
		Assert.isTrue(role.equals(StaffRole.TRAINER), "Der Mitarbeiter muss als Trainer arbeiten!");
		this.training = training;
	}

	public long getTraining() {
		return training;
	}
}
