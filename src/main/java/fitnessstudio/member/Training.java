package fitnessstudio.member;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Training {

	@Id
	@GeneratedValue
	private long trainingId;
}
