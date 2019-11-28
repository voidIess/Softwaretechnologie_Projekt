package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContractManagement {

	private final ContractRepository contracts;
	private final UserAccountManager userAccounts;

	public ContractManagement(ContractRepository contracts, UserAccountManager userAccounts) {
		Assert.notNull(contracts, "ContractRepository must not be null");
		Assert.notNull(userAccounts, "UserAccountManager must not be null");

		this.contracts = contracts;
		this.userAccounts = userAccounts;
	}

	public Contract createContract(ContractForm form) {
		Assert.notNull(form, "ContractForm must not be null");

		String name = form.getName();
		String description = form.getDescription();
		Money price = Money.of(form.getPrice(), "EUR");
		int duration = form.getDuration();

		return contracts.save(new Contract(name, description, price, duration));
	}

	public void editContract(Long contractId, ContractForm form){
		Assert.notNull(contractId, "ContractId must not be null");
		Assert.notNull(form, "ContractForm must not be null");

		Optional<Contract> optionalContract = findById(contractId);
		if (optionalContract.isPresent()){
			Contract contract = optionalContract.get();
			contract.update(form.getName(), form.getDescription(), Money.of(form.getPrice(), "EUR"), form.getDuration());
		}
	}

	public void deleteContract(Long contractId){
		Assert.notNull(contractId, "ContractId must not be null");

		Optional<Contract> contract = findById(contractId);
		contract.ifPresent(contracts::delete);
	}

	public List<Contract> getAllContracts() {
		return contracts.findAll().toList();
	}

	public Optional<Contract> findById(Long contractId){
		Assert.notNull(contractId, "ContractId must not be null");

		return contracts.findById(contractId);
	}
}
