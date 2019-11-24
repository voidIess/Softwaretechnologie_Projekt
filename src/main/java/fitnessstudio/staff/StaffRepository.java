package fitnessstudio.staff;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StaffRepository extends CrudRepository<Staff, Long> {

	Optional<Staff> findByUserAccount(UserAccount userAccount);
	Optional<Staff> findById(long staffId);

}
