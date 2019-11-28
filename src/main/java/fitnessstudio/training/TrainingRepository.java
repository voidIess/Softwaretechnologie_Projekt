package fitnessstudio.training;

import fitnessstudio.member.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface TrainingRepository extends CrudRepository<Training, Long> {

		Streamable<Training> findAllByMember(Member member);

		Streamable<Training> findAll();

}
