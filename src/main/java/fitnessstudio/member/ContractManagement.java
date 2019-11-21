package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.List;

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

		var name = form.getName();
		var description = form.getDescription();
		var price = Money.of(form.getPrice(), "EUR");
		var duration = form.getDuration();

		return contracts.save(new Contract(name, description, price, duration));
	}

	public List<Contract> getAllContracts() {
		return contracts.findAll().toList();
	}
}
