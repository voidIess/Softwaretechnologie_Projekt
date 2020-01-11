package fitnessstudio.contract;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of business logic related to {@link Contract}s.
 *
 * @author Bill Kippe
 * @version 1.0
 */
@Service
@Transactional
public class ContractManagement {

	private final ContractRepository contracts;

	/**
	 * Creates a new {@link ContractManagement} with then given {@link ContractRepository}.
	 *
	 * @param contracts must not be {@literal null}.
	 */
	public ContractManagement(ContractRepository contracts) {
		Assert.notNull(contracts, "ContractRepository must not be null");

		this.contracts = contracts;
	}

	/**
	 * Creates a new {@link Contract} using the information given in the {@link ContractForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return the new {@link Contract} instance.
	 */
	public Contract createContract(ContractForm form) {
		Assert.notNull(form, "ContractForm must not be null");

		String name = form.getName();
		String description = form.getDescription();
		Money price = Money.of(form.getPrice(), "EUR");
		int duration = form.getDuration();

		return contracts.save(new Contract(name, description, price, duration));
	}

	/**
	 * Updates the given {@link Contract} using its ID and the given information in {@link ContractForm}.
	 *
	 * @param contractId ID of {@link Contract}
	 * @param form must not {@literal null}.
	 */
	public void editContract(Long contractId, ContractForm form) {
		Assert.notNull(form, "ContractForm must not be null");

		Optional<Contract> optionalContract = findById(contractId);
		if (optionalContract.isPresent()) {
			Contract contract = optionalContract.get();
			contract.update(form.getName(), form.getDescription(), Money.of(form.getPrice(),
				"EUR"), form.getDuration());
		}
	}

	/**
	 * Deletes the given {@link Contract} using its ID.
	 *
	 * @param contractId ID of {@link Contract}
	 */
	public void deleteContract(Long contractId) {
		Optional<Contract> contractOptional = findById(contractId);
		contractOptional.ifPresent(contracts::delete);
	}

	/**
	 * Returns all {@link Contract}s currently available in the system.
	 *
	 * @return all {@link Contract} entities.
	 */
	public List<Contract> getAllContracts() {
		return contracts.findAll().toList();
	}

	/**
	 * Returns {@link Contract} with given ID.
	 *
	 * @return found {@link Contract} in system, could be empty when not available.
 	 */
	public Optional<Contract> findById(Long contractId) {
		return contracts.findById(contractId);
	}
}
