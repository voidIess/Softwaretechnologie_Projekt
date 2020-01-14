package fitnessstudio.staff;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

/**
 * Repository fuer die Mitarbeiter
 */
public interface StaffRepository extends CrudRepository<Staff, Long> {

	Optional<Staff> findByUserAccount(UserAccount userAccount);
	Optional<Staff> findById(long staffId);

	Streamable<Staff> findAll();

}
