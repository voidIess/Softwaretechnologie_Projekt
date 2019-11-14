package fitnessstudio.staff;

import com.mysema.commons.lang.Assert;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Roster {
	@Id
	@GeneratedValue
	private long rosterId;
	private int week;
	@OneToMany(targetEntity=RosterEntry.class, mappedBy="rosterEntryId")
	private List<RosterEntry> entries;

	Roster(int week) {
		Assert.notNull(week, "Die Woche darf nicht leer sein!");

		this.week = week;
		this.entries = new ArrayList<>();
	}

	Roster(){}

	public void addEntry(RosterEntry rosterEntry){
		entries.add(rosterEntry);
	}

	public void removeEntry(RosterEntry rosterEntry){
		entries.remove(rosterEntry);
	}
}
