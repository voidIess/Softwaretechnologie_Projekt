package fitnessstudio.roster;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RosterManagement {

	public void saveRoster (Roster roster) {

	}

	RosterManagement (RosterRepository rosterRepository) {
		Roster roster = new Roster(1);
		rosterRepository.save(roster);
	}



}
