package fitnessstudio.statistics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

/**
 * A repository interface to manage {@link Attendance} instances.
 *
 * @author Lea Haeusler
 */
public interface AttendanceRepository extends CrudRepository<Attendance, LocalDate> {

	/**
	 * Overwrote {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<Attendance> findAll();

}
