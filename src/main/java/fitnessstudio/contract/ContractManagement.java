package fitnessstudio.contract;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractManagement {

	private final ContractRepository contracts;

	public ContractManagement(ContractRepository contracts) {
		Assert.notNull(contracts, "ContractRepository must not be null");

		this.contracts = contracts;
	}

	public Contract createContract(ContractForm form) {
		Assert.notNull(form, "ContractForm must not be null");

		String name = form.getName();
		String description = form.getDescription();
		Money price = Money.of(form.getPrice(), "EUR");
		int duration = form.getDuration();

		return contracts.save(new Contract(name, description, price, duration));
	}

	public void editContract(Long contractId, ContractForm form) {
		Assert.notNull(form, "ContractForm must not be null");

		Optional<Contract> optionalContract = findById(contractId);
		if (optionalContract.isPresent()) {
			Contract contract = optionalContract.get();
			contract.update(form.getName(), form.getDescription(), Money.of(form.getPrice(), "EUR"), form.getDuration());
		}
	}

	public void deleteContract(Long contractId) {
		Optional<Contract> contract = findById(contractId);
		contract.ifPresent(contracts::delete);
	}

	public List<Contract> getAllContracts() {
		return contracts.findAll().toList();
	}

	public Optional<Contract> findById(Long contractId) {
		return contracts.findById(contractId);
	}
}
