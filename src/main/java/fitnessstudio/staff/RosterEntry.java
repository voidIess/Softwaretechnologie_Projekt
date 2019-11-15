package fitnessstudio.staff;

import com.mysema.commons.lang.Assert;
import org.salespointframework.catalog.Product;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class RosterEntry {
	@Id @GeneratedValue (strategy = GenerationType.IDENTITY)
	private long rosterEntryId;
	@OneToOne(targetEntity=Staff.class, cascade = {CascadeType.ALL})
	private  Staff staff;
	private  LocalDateTime startTime;
	private  LocalDateTime endTime;
	private  int duration;
	private  StaffRole role;

	RosterEntry(){}

	RosterEntry(StaffRole role, Staff staff, LocalDateTime startTime, int duration) {
		Assert.notNull(staff, "Diesen Staff gibt es nicht.");
		Assert.notNull(startTime, "Gib eine Startzeit ein");
		Assert.isTrue(duration>0 && duration <= 120, "Eine Schicht muss mindestens 1 Minute lang sein und darf maximal 120 Minuten gehen");

		this.endTime = startTime.plusMinutes(duration);
		this.staff = staff;
		this.startTime = startTime;
		this.duration = duration;
		this.role = role;
	}

	public String getRole(){
		if (role == StaffRole.COUNTER){
			return "Thekenkraft";
		} else {
			return "Trainer";
		}
	}

	public long getRosterId() {
		return rosterEntryId;
	}

	public Staff getStaff() {
		return staff;
	}

	public String getStartTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		return startTime.getDayOfWeek() + startTime.format(formatter);
	}

	public String getEndTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		return startTime.getDayOfWeek() + startTime.format(formatter);
	}

	public int getDuration() {
		return duration;
	}
}
