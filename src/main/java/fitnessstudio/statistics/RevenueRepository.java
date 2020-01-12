package fitnessstudio.statistics;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface RevenueRepository extends CrudRepository<Revenue, Long> {

	Optional<Revenue> findByMember(long member);

	@Override
	Streamable<Revenue> findAll();

}
