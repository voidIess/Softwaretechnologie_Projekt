package fitnessstudio.statistics;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Class which represents the income through one member contract per month.
 *
 * @author Lea Haeusler
 */
@Entity
public class Revenue {

	@Id
	@GeneratedValue
	private long id;

	/**
	 * Member who has a running {@link fitnessstudio.contract.Contract}
	 */
	private long member;
	/**
	 * Contract of the {@link fitnessstudio.member.Member}
	 */
	private long contract;

	public Revenue() {}

	/**
	 * Creates a new {@link Revenue} instance with the given member and contract
	 *
	 * @param memberId		ID of the member
	 * @param contractId	ID of the contract
	 */
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
