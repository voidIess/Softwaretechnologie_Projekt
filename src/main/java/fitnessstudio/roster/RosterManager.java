package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static fitnessstudio.roster.Roster.DURATION;
import static fitnessstudio.roster.Roster.STARTTIME;

@Component
public class RosterManager {

	private static RosterManager rosterManager;
	private static RosterEntryRepository rosterEntryRepository;
	private static RosterRepository rosterRepository;
	private static TableRowRepository tableRowRepository;
	private static SlotRepository slotRepository;
	private static String[] GermanWeekDay = new String[]{"Sa.", "So.","Mo.", "Die.", "Mi.", "Do.", "Fr.", };

	private RosterManager(){}

	public static RosterManager getInstance(){
		if(rosterManager == null) {
			rosterManager = new RosterManager();
		}
		return rosterManager;
	}

	@Autowired
	public void setRosterEntryRepository(RosterEntryRepository rosterEntryRepository) {
		this.rosterEntryRepository = rosterEntryRepository;
	}

	@Autowired
	public void setRosterRepository(RosterRepository rosterRepository) {
		this.rosterRepository = rosterRepository;
	}

	@Autowired
	public void setTableRowRepository(TableRowRepository tableRowRepository) {
		this.tableRowRepository = tableRowRepository;
	}

	@Autowired
	public void setSlotRepository(SlotRepository slotRepository) {
		this.slotRepository = slotRepository;
	}

	public static void saveSlot(Slot slot){
		slotRepository.save(slot);
	}

	public  static void saveTableRow(TableRow tableRow){
		tableRowRepository.save(tableRow);
	}

	public  static void saveRoster (Roster roster) {
		rosterRepository.save(roster);
	}

	public  static void saveRosterEntry (RosterEntry rosterEntry){
		rosterEntryRepository.save(rosterEntry);
	}

	public static Slot getSlotById(Long id){
		return slotRepository.findById(id).orElse(null);
	}

	public static RosterEntry getEntryById(Long id){
		return rosterEntryRepository.findById(id).orElse(null);
	}

	public static TableRow getRowById(Long id){
		return tableRowRepository.findById(id).orElse(null);
	}

	public static Roster getRosterCurrentWeek() {
		return rosterRepository.findByWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)).orElse(null);
	}

	public static Roster getRosterByWeek(int week) {
		return rosterRepository.findByWeek(week).orElse(null);
	}

	public static List<Integer> getNextWeeks () {
		Iterable<Roster> rosters = rosterRepository.findAll();
		List<Integer> weeks = new LinkedList<>();
		for (Roster r : rosters) {
			weeks.add(r.getWeek());
		}
		return weeks;
	}

	public static List<String> getWeekDatesByWeek (int week) {
		List<String> dates = new LinkedList<>();
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.WEEK_OF_YEAR) <= week) { // Wenn derzeitige Woche größer als die gefordert ist (z.b. 52 und 1) -> Neues Jahr!
			c.set(Calendar.WEEK_OF_YEAR, week);
		} else {
			c.add(Calendar.YEAR,1);
			c.set(Calendar.WEEK_OF_YEAR, week);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

		int x = c.get(Calendar.DAY_OF_WEEK);


		for (int i = 2; i<7;i++){
			c.set(Calendar.DAY_OF_WEEK, i);
			dates.add(GermanWeekDay[i] + " " +dateFormat.format(c.getTime()));

		};

		c.set(Calendar.DAY_OF_WEEK, 0);
		dates.add(GermanWeekDay[0] +" " +dateFormat.format(c.getTime()));
		c.set(Calendar.DAY_OF_WEEK, 1);
		dates.add(GermanWeekDay[1] + " " +dateFormat.format(c.getTime()));
		return dates;
	}

	public static StaffRole getRoleByString(String role){
		if (role.equals("Trainer")) return StaffRole.TRAINER;
		else return StaffRole.COUNTER;
	}

	public static List<String> getRoles(){
		List<String> roles = new ArrayList<>();
		roles.add("Thekenkraft");
		roles.add("Trainer");
		return roles;
	}

}
