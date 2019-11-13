package member;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface MemberRepository extends CrudRepository<Member, Long> {

	@Override
	Streamable<Member> findAll();
}
