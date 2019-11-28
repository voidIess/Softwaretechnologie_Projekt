package fitnessstudio.roster;

import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

	public static Roster getRoster() {
		return rosterRepository.findAll().iterator().next();
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
