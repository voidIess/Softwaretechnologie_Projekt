package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Markus.
 * Klasse des wirklichen Diensplanobjekts.
 * **/
@Entity
public class Roster {

	@Id
	@GeneratedValue
	private long rosterId;
	private int week;

	/**
	 * Beschreibt die Anzahl der Schichten.
	 */
	public static final int AMOUNT_ROWS = 8;
	/**Beschreibt die Dauer einer Schicht (in Minuten)
	 *
	 */
	public static final int DURATION = 120;
	/**
	 * Startzeitpunkt, auf den die Duration addiert wird.
	 */
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

	/**
	 * Konstruktor der Klasse Roster
	 * @param week Beschreibt die Kalenderwoche des Rosters.
	 */
	public Roster(int week) {
		this();
		Assert.isTrue(week > 0 && week < 53, "Diese Kalenderwoche existiert nicht! (1-52)");
		this.week = week;
		initialize();
	}

	/**
	 * Initialisiert eine Anzahl von TableRows
	 */
	private void initialize() {
		rows.clear();
		for (int i = 0; i < AMOUNT_ROWS; i++) {
			rows.add(new TableRow(STARTTIME.plusMinutes(i * (long) DURATION), i));
		}
	}

	/** Fügt einen Eintrag dem Dienstplan hinzu.
	 * @param shift Beschreibt die ausgewählte Schicht (Äquivalent zur Zeile)
	 * @param day Beschreibt den ausgewählten Tag (Äquivalent zur Spalte)
	 * @param rosterEntry Der Eintrag der an die Koordinaten shift, day eingetragen werden soll.
	 */
	public void addEntry(int shift, int day, RosterEntry rosterEntry) {
		Assert.isTrue(shift >= 0 && shift < rows.size(), "Diese Schichtnummer existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser Tag exisitiert nicht.");
		Assert.notNull(rosterEntry, "Der RosterEntry darf nicht null sein!");
		Slot slot = rows.get(shift).getSlots().get(day);
		Assert.isFalse(slot.isTaken(rosterEntry.getStaff()), "Der Mitarbeiter arbeitet zu dieser Zeit schon.");
		slot.getEntries().add(rosterEntry);
	}

	/**
	 * Löscht einen Eintrag aus dem Dienstplan
	 * @param shift Beschreibt die ausgewählte Schicht (äquivalent zur Zeile)
	 * @param day Beschreibt den aktuell ausgewählten Tag (äquivalent zur Spalte)
	 * @param rosterEntryId Die ID des Dienstplaneintrags, der gelöscht werden soll. Die befindet sich an den Koordinaten (shift, day).
	 */
	public void deleteEntry(int shift, int day, long rosterEntryId) {
		Assert.isTrue(shift >= 0 && shift < rows.size(), "Diese Schicht existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser Tag exisitiert nicht.");
		Slot slot = rows.get(shift).getSlots().get(day);
		Assert.isTrue(slot.deleteEntry(rosterEntryId),
			"Der Eintrag konnte nicht gelöscht werden. Gehört zu diesem Eintrag ein Training?");

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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
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

	/**
	 * Überprüft ob ein Mitarbeiter zu dieser Zeit bereits arbeitet.
	 * @param staff Der Mitarbeiter, der geprüft werden soll
	 * @return Gibt zurück, ob der Mitarbeiter verfügbar ist.
	 */
	public boolean isTaken(Staff staff) {
		for (RosterEntry rosterEntry : entries) {
			if (rosterEntry.getStaff().getStaffId() == staff.getStaffId()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Löscht einen Eintrag aus der Liste der Einträge des Slots.
	 * @param id Die ID des Dienstplaneintrags, der gelöscht werden soll.
	 * @return Gibt zurück, ob der Eintrag erfolgreich gelöscht wurde.
	 */
	public boolean deleteEntry(long id) {
		for (RosterEntry rosterEntry : entries) {
			if (rosterEntry.getRosterEntryId() == id) {
				if (rosterEntry.getTraining() == RosterEntry.NONE) {
					entries.remove(rosterEntry);
					return true;
				} else{
					return false;
				}
			}
		}
		return false;
	}
}
