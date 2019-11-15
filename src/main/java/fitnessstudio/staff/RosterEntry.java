package fitnessstudio.staff;

import com.mysema.commons.lang.Assert;
import org.salespointframework.catalog.Product;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class RosterEntry {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private long rosterEntryId;
	@OneToOne(targetEntity=Staff.class, cascade = {CascadeType.ALL})
	private  Staff staff;
	private  LocalDateTime startTime;
	private  int duration;
	private  StaffRole role;

	RosterEntry(){}

	RosterEntry(StaffRole role, Staff staff, LocalDateTime startTime, int duration) {
		Assert.notNull(staff, "Diesen Staff gibt es nicht.");
		Assert.notNull(startTime, "Gib eine Startzeit ein");
		Assert.isTrue(duration>0 && duration <= 120, "Eine Schicht muss mindestens 1 Minute lang sein und darf maximal 120 Minuten gehen");

		this.staff = staff;
		this.startTime = startTime;
		this.duration = duration;
		this.role = role;
	}

	public StaffRole getRole(){
		return role;
	}

	public long getRosterId() {
		return rosterEntryId;
	}

	public Staff getStaff() {
		return staff;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public int getDuration() {
		return duration;
	}
}
