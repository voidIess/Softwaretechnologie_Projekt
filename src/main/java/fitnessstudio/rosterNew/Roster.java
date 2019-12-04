package fitnessstudio.rosterNew;

import com.mysema.commons.lang.Assert;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Roster {

	@Id @GeneratedValue
	private long rosterId;						// ID des Rosters
	private int week;							// Gibt die Wochennummer an, für die der Dienstplan sein soll

	@ElementCollection
	private List<TableRow> rows;				// Es gibt x Reihen pro Tag, beliebig anpassbar wenn man die Schichten verändern möchte.

	public static final int AMOUNT_ROWS = 8;	// Die Anzahl der Reihen
	public static final int DURATION = 120;	// Die Dauer einer Schicht
	public static final LocalDateTime STARTTIME = LocalDateTime.of(	// Beginn der ersten Schicht
		2000,								// 01.01.2000, 06:00, das Datum ist dabei egal
		1,
		1,
		6,
		0);

	Roster(){}

	Roster(int week){
		Assert.isTrue(week > 0 && week < 53, "Falsches Format für die Kalenderwoche!");
		this.week = week;
		this.rows = new LinkedList<>();

	}

	public long getRosterId() {
		return rosterId;
	}

	public void setRosterId(long rosterId) {
		this.rosterId = rosterId;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public List<TableRow> getRows() {
		return rows;
	}

	public void setRows(List<TableRow> rows) {
		this.rows = rows;
	}

	public static int getAmountRows() {
		return AMOUNT_ROWS;
	}

	public static int getDURATION() {
		return DURATION;
	}

	public static LocalDateTime getSTARTTIME() {
		return STARTTIME;
	}
}

@Embeddable
class TableRow {

	@Embedded
	private List<Long> slots = new LinkedList<>();

	public List<Long> getSlots() {
		return slots;
	}

	public void setSlots(List<Long> slots) {
		this.slots = slots;
	}
}

@Embeddable
class Slot {

}
