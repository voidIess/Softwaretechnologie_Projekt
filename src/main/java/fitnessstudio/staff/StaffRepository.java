package fitnessstudio.staff;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface StaffRepository extends CrudRepository<Staff, Long> {

}
