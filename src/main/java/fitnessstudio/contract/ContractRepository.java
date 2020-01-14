package fitnessstudio.contract;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * A repository interface to manage {@link Contract} instances.
 *
 * @author Bill Kippe
 * @version 1.0
 */
public interface ContractRepository extends CrudRepository<Contract, Long> {

	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<Contract> findAll();
}
