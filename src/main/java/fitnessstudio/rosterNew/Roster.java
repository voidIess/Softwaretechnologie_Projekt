package fitnessstudio.rosterNew;

import com.mysema.commons.lang.Assert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Roster {

	@Id @GeneratedValue
	private long rosterId;						// ID des Rosters
	private int week;							// Gibt die Wochennummer an, für die der Dienstplan sein soll

	@OneToMany (cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<TableRow> rows;				// Es gibt x Reihen pro Tag, beliebig anpassbar wenn man die Schichten verändern möchte.

	public static final int AMOUNT_ROWS = 8;	// Die Anzahl der Reihen
	public static final int DURATION = 120;	// Die Dauer einer Schicht
	public static final LocalDateTime STARTTIME = LocalDateTime.of(	// Beginn der ersten Schicht
		2000,								// 01.01.2000, 06:00, das Datum ist dabei egal
		1,
		1,
		6,
		0);

	Roster(){
		rows = new ArrayList<>();
	}

	public Roster(int week){
		this();
		Assert.isTrue(week > 0 && week < 53, "Falsches Format für die Kalenderwoche!");
		this.week = week;
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

	public void addRow(TableRow row){rows.add(row);}

	//public void setRows(List<TableRow> rows) {
	//	this.rows = rows;
	//}

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

@Entity
class TableRow {

	@Id
	@GeneratedValue
	private long rowId;

	@OneToMany (cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Slot> slots;
	private String test;

	TableRow(){
		slots = new ArrayList<>();
	}

	public TableRow(String test){
		this();
		this.test = test;
	}

	public String getTest() {
		return test;
	}

	public long getRowId() {
		return rowId;
	}

	public List<Slot> getSlots() {
		return slots;
	}

	public void addSlot(Slot slot) {slots.add(slot);}
}

@Entity
class Slot {

	@Id
	@GeneratedValue
	private long slotId;

	private String test;

	Slot() {}

	Slot(String test){
		this.test = test;
	}

	public String getTest() {
		return test;
	}

	public long getSlotId() {
		return slotId;
	}
}
