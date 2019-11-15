package fitnessstudio.member;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

	@Override
	Streamable<Member> findAll();

	Optional<Member> findByUserAccount(UserAccount userAccount);
}
