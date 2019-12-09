package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Roster {

	@Id
	@GeneratedValue
	private long rosterId;
	private int week;

	public static final int AMOUNT_ROWS = 8;
	public static final int DURATION = 120;
	public static final LocalDateTime STARTTIME = LocalDateTime.of(
		2000,
		1,
		1,
		6,
		0);

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
	private List<TableRow> rows;

	private Roster() {
		this.rows = new ArrayList<>();
	}

	public Roster(int week) {
		this();
		Assert.isTrue(week > 0 && week < 53, "Diese Kalenderwoche existiert nicht! (1-52)");
		this.week = week;
		initialize();
	}

	private void initialize() {
		rows.clear();
		for (int i = 0; i < AMOUNT_ROWS; i++) {
			rows.add(new TableRow(STARTTIME.plusMinutes(i * (long) DURATION), i));
		}
	}

	public void addEntry(int shift, int day, RosterEntry rosterEntry) {
		Assert.isTrue(shift >= 0 && shift < rows.size(), "Diese Schichtnummer existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser Tag exisitiert nicht.");
		Assert.notNull(rosterEntry, "Der RosterEntry darf nicht null sein!");
		Slot slot = rows.get(shift).getSlots().get(day);
		Assert.isFalse(slot.isTaken(rosterEntry.getStaff()), "Der Mitarbeiter arbeitet zu dieser Zeit schon.");
		slot.getEntries().add(rosterEntry);
	}

	public void deleteEntry(int shift, int day, long rosterEntryId) {
		Assert.isTrue(shift >= 0 && shift < rows.size(), "Diese Schicht existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser Tag exisitiert nicht.");
		Slot slot = rows.get(shift).getSlots().get(day);
		Assert.isTrue(slot.deleteEntry(rosterEntryId), "Der Eintrag konnte nicht gelöscht werden. Gehört zu diesem Eintrag ein Training?");

	}

	/* ========================================================================
	* Getter
	 ========================================================================*/

	public long getRosterId() {
		return rosterId;
	}

	public int getWeek() {
		return week;
	}

	public List<TableRow> getRows() {
		return rows;
	}
}

@Entity
class TableRow {

	@Id
	@GeneratedValue
	private long rowId;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Slot> slots;

	TableRow() {
		this.slots = new ArrayList<>();
	}

	TableRow(LocalDateTime start, int shiftNo) {
		this();
		Assert.notNull(start, "Keine Startzeit angegeben!");
		Assert.isTrue(shiftNo >= 0 && shiftNo < Roster.AMOUNT_ROWS, "Diese Schichtnummer existiert nicht!");
		this.startTime = start;
		this.endTime = start.plusMinutes(Roster.DURATION);
		initialize(shiftNo);
	}

	private void initialize(int shiftNo) {
		slots.clear();
		for (int i = 0; i < 7; i++) {
			slots.add(new Slot(shiftNo, i));
		}
	}

	/* ========================================================================
	* Getter
	 ========================================================================*/

	public long getRowId() {
		return rowId;
	}

	public List<Slot> getSlots() {
		return slots;
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return startTime.format(formatter) + "-" + endTime.format(formatter);
	}

}

@Entity
class Slot {

	@Id
	@GeneratedValue
	private long slotId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RosterEntry> entries;

	private int[] coordinates;

	Slot() {
		this.entries = new ArrayList<>();
		this.coordinates = new int[2];
	}

	Slot(int shift, int day) {
		this();
		Assert.isTrue(shift >= 0 && shift < Roster.AMOUNT_ROWS, "Diese Schicht existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser tag existiert nicht!");
		this.coordinates[0] = shift;
		this.coordinates[1] = day;
	}

	public long getSlotId() {
		return slotId;
	}

	public int[] getCoordinates() {
		return coordinates;
	}

	public List<RosterEntry> getEntries() {
		entries.sort(RosterEntry::compareTo);
		return entries;
	}

	public boolean isTaken(Staff staff) {
		for (RosterEntry rosterEntry : entries) {
			if (rosterEntry.getStaff() == staff) return true;
		}
		return false;
	}

	public boolean deleteEntry(long id) {
		for (RosterEntry rosterEntry : entries) {
			if (rosterEntry.getRosterEntryId() == id) {
				if (rosterEntry.getTraining() == -1) {
					entries.remove(rosterEntry);
					return true;
				} else return false;
			}
		}
		return false;
	}
}
