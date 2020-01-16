package fitnessstudio.studio;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Studio} instances.
 *
 */
public interface StudioRepository extends CrudRepository<Studio, Long> {
}
