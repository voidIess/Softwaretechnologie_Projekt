package fitnessstudio.roster;

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

	@OneToMany
	private List<TableRow> rows;				// Es gibt x Reihen pro Tag, beliebig anpassbar wenn man die Schichten verändern möchte.

	private static final int AMOUNT_ROWS = 8;	// Die Anzahl der Reihen
	private static final int DURATION = 120;	// Die Dauer einer Schicht
	private static final LocalDateTime STARTTIME = LocalDateTime.of(	// Beginn der ersten Schicht
		2000,								// 01.01.2000, 06:00, das Datum ist dabei egal
		1,
		1,
		6,
		0);

	Roster(){}

	Roster(int week){
		this.week = week;
		this.rows = new LinkedList<>();
		init();
	}

	public void addEntry (int shift, int day, RosterEntry rosterEntry) {
		TableRow tableRow =rows.get(shift);
		tableRow.addEntry(day, rosterEntry);
		RosterManager.saveTableRow(tableRow);
	}

	public long getId(){
		return rosterId;
	}

	// Hier werden die x Zeilen in der Tabelle angelegt
	private void init(){

		Assert.notNull(rows, "Die Liste der Schichten darf nicht 'null' sein.");
		for (int i = 0; i < AMOUNT_ROWS;i++){
			TableRow tableRow = new TableRow(STARTTIME.plusMinutes(i*DURATION),DURATION);
			RosterManager.saveTableRow(tableRow);
			rows.add(tableRow);
		}
	}

	public List<TableRow> getRows(){
		Assert.notNull(rows, "Die Liste der Schichten darf nicht 'null' sein.");
		return rows;
	}

	public int getWeek(){
		return week;
	}
}
