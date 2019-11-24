package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RosterEntry implements Comparable<RosterEntry> {

	@Id @GeneratedValue
	private long rosterEntryId;
	private StaffRole role;

	@OneToOne(cascade = CascadeType.REMOVE)
	private Staff staff;

	RosterEntry(){}

	RosterEntry(StaffRole role, Staff staff){
		this.role = role;
		this.staff = staff;
	}

	public long getRosterEntryId() {
		return rosterEntryId;
	}

	public void setRosterEntryId(long rosterEntryId) {
		this.rosterEntryId = rosterEntryId;
	}

	public StaffRole getRole() {
		return role;
	}

	public void setRole(StaffRole role) {
		this.role = role;
	}

	public boolean isTrainer(){
		return  role == StaffRole.TRAINER;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public String roleToString () {
		if (role == StaffRole.COUNTER){
			return "Thekenkraft";
		} else {
			return "Trainer";
		}
	} 	// Konvertiert die Aufgabe des Staffs in einen String mit deutscher Übersetzung

	@Override
	public String toString(){
		return staff.getLastName() + ", " + staff.getFirstName()+ " " + staff.getStaffId();
	}

	@Override	// Um im Dienstplan die Einträge nach den Aufgaben zu sortieren
	public int compareTo(RosterEntry rosterEntry){
		if (rosterEntry.getRole().equals(StaffRole.COUNTER)) return 1;
		else return -1;
	}
}
