package fitnessstudio.statistics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

/**
 * A repository interface to manage {@link Revenue} instances.
 *
 * @version 1.0
 * @author Lea Haeusler
 */
public interface RevenueRepository extends CrudRepository<Revenue, Long> {

	/**
	 * Returns the {@link Revenue} caused by the contract costs of the given member if existent.
	 *
	 * @param member	ID of the member
	 * @return {@link Revenue} caused by the contract costs of the given member
	 */
	Optional<Revenue> findByMember(long member);

	/**
	 * Overwrote {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<Revenue> findAll();

}
