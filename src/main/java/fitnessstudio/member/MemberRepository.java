package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

/**
 * A repository interface to manage {@link Member} instances.
 *
 * @author Bill Kippe
 * @version 1.0
 */
public interface MemberRepository extends CrudRepository<Member, Long> {

	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<Member> findAll();

	/**
	 * Returns the {@link Member} related to the given user account if existent.
	 *
	 * @param userAccount user account of member
	 * @return {@link Member} related to the given user account
	 */
	Optional<Member> findByUserAccount(UserAccount userAccount);

	/**
	 * Returns all members who subscribed the given contract.
	 *
	 * @param contract membership contract
	 * @return all members with given contract
	 */
	Streamable<Member> findByContract(Contract contract);
}
