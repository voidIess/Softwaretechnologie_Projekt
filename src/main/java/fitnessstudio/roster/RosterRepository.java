package fitnessstudio.roster;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


/**
 * @author Markus
 * Repository zum speichern von Dienstplaenen
 */
public interface RosterRepository extends CrudRepository<Roster, Long> {

	/**
	 * Returned alle Dienstplan anhand einer Kalenderwoche
	 * @param week Entsprechende Kalenderwoche
	 * @return Dienstplan als Optional
	 */
	Optional<Roster> findByWeek (int week);

}
