package fitnessstudio.training;

import org.springframework.data.repository.CrudRepository;

import javax.validation.constraints.NotEmpty;

public interface TrainingRepository extends CrudRepository<Training, Long> {



}
