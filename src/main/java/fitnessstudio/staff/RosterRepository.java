package fitnessstudio.staff;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RosterRepository extends CrudRepository<Roster, Long> {
	Streamable<Roster> findByWeek(LocalDate week, Sort sort);
}
