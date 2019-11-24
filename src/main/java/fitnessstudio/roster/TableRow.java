package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import fitnessstudio.studio.StudioService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Entity
public class TableRow {

	/*
	Diese Klasse repäsentiert einen Zeile in dem Stundenplan
	 */

	@Id @GeneratedValue
	private long tableRowId;
	private LocalDateTime startTime, endTime;

	@OneToMany
	private List<Slot> slots;						// Es gibt in jeder Reihe für jeden Tag einen Slot (=7)

	TableRow(){}

	public long getTableRowId() {
		return tableRowId;
	}

	TableRow(LocalDateTime startTime, int duration){
		Assert.notNull(startTime, "Du musst einen Startzeit angeben.");
		Assert.isTrue(duration>0, "Die Dauer einer Schicht muss größer als 0 Minuten sein.");
		this.startTime = startTime;
		this.endTime = startTime.plusMinutes(duration);
		this.slots = new LinkedList<>();
		init();
	}

	private void init(){
		Assert.notNull(slots, "Die Liste der Slots darf nicht 'null' sein.");
		for (int i = 0; i<7; i++) {
			Slot slot = new Slot();
			RosterManager.saveSlot(slot);
			slots.add(slot);
		}
	}

	public List<Slot> getSlots(){
		return slots;
	}

	public void addEntry (int day, RosterEntry rosterEntry) {
		if (day >= slots.size()) throw new IllegalArgumentException("Die Nummer der Schicht existiert nicht.");
		Slot slot = slots.get(day);
		slot.addEntry(rosterEntry);
		RosterManager.saveSlot(slot);
	}

	public String getTime(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return startTime.format(formatter) + "-" + endTime.format(formatter);
	}

}

@Entity
class Slot {

	/*
	Diese Klasse repräsentiert eine Zelle in der Tabelle. In einer Zelle einer Tabelle können mehrere Leute Dienst haben
	 */

	@Id @GeneratedValue
	private long slotId;

	@OneToMany(cascade=CascadeType.REMOVE)
	private List<RosterEntry> rosterEntries; 	// Alle Leute die in dieser Zeit Dienst haben

	Slot(){
		this.rosterEntries = new LinkedList<>();
	}

	public void addEntry(RosterEntry rosterEntry){
		Assert.isFalse(isTaken(rosterEntry.getStaff()),"Der Staff arbeitet5 zu dieser Zeit bereits.");
		rosterEntries.add(rosterEntry);
		RosterManager.saveRosterEntry(rosterEntry);
	}

	public long getSlotId() {
		return slotId;
	}

	public List<RosterEntry> getRosterEntries (){
		Collections.sort(rosterEntries, new Comparator<>() {
			@Override
			public int compare(RosterEntry p1, RosterEntry p2) {
				return p1.compareTo(p2); // Ascending
			}
		});
		return rosterEntries;
	}

	public void deleteEntry(RosterEntry rosterEntry){
		Assert.notNull(rosterEntry, "Kein RosterEntry gefunden");
		rosterEntries.remove(rosterEntry);
	}

	private boolean isTaken(Staff staff) {
		for (RosterEntry rosterEntry : rosterEntries){
			if (rosterEntry.getStaff() == staff) return true;
		}
		return false;
	}

	@Override
	public String toString(){
		String shifts = "";
		for (int i = 0; i<rosterEntries.size();i++){
			if (i == rosterEntries.size()-1) shifts += rosterEntries.toString();
			shifts += rosterEntries.toString() + ",\n";
		}
		return shifts;
	}
}
