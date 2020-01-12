package fitnessstudio.statistics;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Revenue {

	@Id
	@GeneratedValue
	private long id;

	private long member;
	private long contract;

	public Revenue() {}

	Revenue(long memberId, long contractId) {
		member = memberId;
		contract = contractId;
	}

	public long getId() {
		return id;
	}

	public long getMember() {
		return member;
	}

	public long getContract() {
		return contract;
	}
}
