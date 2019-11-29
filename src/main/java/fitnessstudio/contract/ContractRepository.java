package fitnessstudio.contract;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface ContractRepository extends CrudRepository<Contract, Long> {

	@Override
	Streamable<Contract> findAll();
}
