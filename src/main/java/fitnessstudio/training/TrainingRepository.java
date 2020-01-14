package fitnessstudio.training;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * A repository interface to manage {@link Training} instances.
 *
 * @author Bill Kippe
 * @version 1.0
 */
public interface TrainingRepository extends CrudRepository<Training, Long> {

	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	Streamable<Training> findAll();

}
