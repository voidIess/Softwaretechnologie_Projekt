package fitnessstudio.staff;

import fitnessstudio.member.Member;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RosterRepository extends CrudRepository<RosterEntry, Long> {
	Iterable<RosterEntry> findByRole(StaffRole role, Sort sort);

	@Override
	Streamable<RosterEntry> findAll();

	default Iterable<RosterEntry> findByType(StaffRole role) {
		return findByRole(role, Sort.by("rosterEntryId").descending());
	}
}
