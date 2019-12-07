package fitnessstudio.roster;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RosterRepository extends CrudRepository<Roster, Long> {

<<<<<<< HEAD
	Optional<Roster> findByWeek(int week);
=======
	Optional<Roster> findByWeek (int week);

>>>>>>> feature/rosterNewBill
}
