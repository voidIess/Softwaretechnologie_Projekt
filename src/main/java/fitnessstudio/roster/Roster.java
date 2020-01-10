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
 **/
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
	/**
	 * Beschreibt die Dauer einer Schicht (in Minuten)
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

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<TableRow> rows;

	private Roster() {
		this.rows = new ArrayList<>();
	}

	/**
	 * Konstruktor der Klasse Roster
	 *
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

	/**
	 * Fügt einen Eintrag dem Dienstplan hinzu.
	 *
	 * @param shift       Beschreibt die ausgewählte Schicht (Äquivalent zur Zeile)
	 * @param day         Beschreibt den ausgewählten Tag (Äquivalent zur Spalte)
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
	 *
	 * @param shift         Beschreibt die ausgewählte Schicht (äquivalent zur Zeile)
	 * @param day           Beschreibt den aktuell ausgewählten Tag (äquivalent zur Spalte)
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

	/**
	 * @return ID des Dienstplans
	 */
	public long getRosterId() {
		return rosterId;
	}

	/**
	 * @return Kalenderwoche des Dienstplans
	 */
	public int getWeek() {
		return week;
	}

	/**
	 * @return Reihe des Dienstplans
	 */
	public List<TableRow> getRows() {
		return rows;
	}
}


/**
 * Klasse einer Zeile des Dienstplans
 */
@Entity
class TableRow {

	@Id
	@GeneratedValue
	private long rowId;

	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Slot> slots;

	private TableRow() {
		this.slots = new ArrayList<>();
	}

	/**
	 * Konstruktor der Klasse TableRow
	 *
	 * @param start   Startzeit der Schicht, die zu der Zeile gehört.
	 * @param shiftNo Schichtnummer (nicht die ID). Die erste Zeile ist die 0, danach +1
	 */
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

	/**
	 * @return Datenbank ID der TableRow
	 */
	public long getRowId() {
		return rowId;
	}

	/**
	 * @return Liste aller Slots der Reihe (7 Stücke)
	 */
	public List<Slot> getSlots() {
		return slots;
	}

	/**
	 * @return Startzeit im Format hh:mm bis Endzeit im Format hh:mm. Die Differenz zwischen der End -und
	 * Startzeit ist die in der Klasse Roster beschriebene DURATION
	 */
	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return startTime.format(formatter) + "-" + endTime.format(formatter);
	}

}

/**
 * Klasse einer Spalte einer TableRow. Jede Zeile hat 7 Spalten. Die Anzahl ist zwar identisch, jedoch sind die Slots
 * der anderen Zeilen nicht gleich der einer anderen Zeilen. Es sind immer andere eigenständige Objekte.
 */
@Entity
class Slot {

	@Id
	@GeneratedValue
	private long slotId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<RosterEntry> entries;

	private int[] coordinates;

	private Slot() {
		this.entries = new ArrayList<>();
		this.coordinates = new int[2];
	}

	/**
	 * Konstruktor der Klasse Slot
	 * @param shift Die dazugehörige Zeile. (y-Koordinate)
	 * @param day Die dem Tag entsprechende Zeile. (x-Koordinate)
	 */
	Slot(int shift, int day) {
		this();
		Assert.isTrue(shift >= 0 && shift < Roster.AMOUNT_ROWS, "Diese Schicht existiert nicht!");
		Assert.isTrue(day >= 0 && day < 7, "Dieser tag existiert nicht!");
		this.coordinates[0] = shift;
		this.coordinates[1] = day;
	}

	/** ID des Slots
	 * @return Datenbank ID des Slots
	 */
	public long getSlotId() {
		return slotId;
	}

	/** Koordinaten des Slots im Dienstplan
	 * @return Koordinaten im Dienstplan des Slots. [0] ist die Zeile, [1] der Tag.
	 */
	public int[] getCoordinates() {
		return coordinates;
	}

	/**
	 * Liste aller Einträge zu dieser Zeit
	 * @return Liste aller Einträge zu dieser Zeit. Jeder Mitarbeiter kann nur einmal pro Slot eingetragen sein.
	 */
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
			if (rosterEntry.getStaff().getStaffId() == staff.getStaffId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Löscht einen Eintrag aus der Liste der Einträge des Slots.
	 *
	 * @param id Die ID des Dienstplaneintrags, der gelöscht werden soll.
	 * @return Gibt zurück, ob der Eintrag erfolgreich gelöscht wurde.
	 */
	public boolean deleteEntry(long id) {
		for (RosterEntry rosterEntry : entries) {
			if (rosterEntry.getRosterEntryId() == id) {
				if (rosterEntry.getTraining() == RosterEntry.NONE) {
					entries.remove(rosterEntry);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
