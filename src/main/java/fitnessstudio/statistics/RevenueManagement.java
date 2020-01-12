package fitnessstudio.statistics;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractManagement;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@Transactional
class RevenueManagement {

	private final RevenueRepository revenues;
	private final ContractManagement contracts;

	RevenueManagement(RevenueRepository revenues, ContractManagement contracts) {
		Assert.notNull(revenues, "RevenueRepository must not be null!");
		Assert.notNull(contracts, "ContractManagement must not be null!");
		this.revenues = revenues;
		this.contracts = contracts;
	}

	void addRevenue(long member, long contract) {
		if (revenues.findByMember(member).isEmpty()) {
			revenues.save(new Revenue(member, contract));
		}
	}

	Streamable<Revenue> findAll() {
		return revenues.findAll();
	}

	Money getMonthlyRevenue() {
		Money total = Money.of(0, "EUR");
		for (Revenue revenue : findAll()) {
			Optional<Contract> contract = contracts.findById(revenue.getContract());
			if (contract.isPresent()) {
				total = total.add(contract.get().getPrice());
			}
		}
		return total;
	}


}
