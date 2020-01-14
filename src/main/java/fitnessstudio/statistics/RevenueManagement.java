package fitnessstudio.statistics;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractManagement;
import org.javamoney.moneta.Money;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Management class to interact with the {@link RevenueRepository}
 *
 * @version 1.0
 * @author Lea Haeusler
 */
@Service
@Transactional
class RevenueManagement {

	private final RevenueRepository revenues;
	private final ContractManagement contracts;

	/**
	 * Creates a new {@link RevenueManagement} instance with then given {@link RevenueRepository} and {@link ContractManagement}.
	 *
	 * @param revenues		must not be {@literal null}
	 * @param contracts		must not be {@literal null}
	 */
	RevenueManagement(RevenueRepository revenues, ContractManagement contracts) {
		Assert.notNull(revenues, "RevenueRepository must not be null!");
		Assert.notNull(contracts, "ContractManagement must not be null!");
		this.revenues = revenues;
		this.contracts = contracts;
	}

	/**
	 * Adds a new income through a members contract to the {@link RevenueRepository}
	 *
	 * @param member		ID of the member
	 * @param contract		ID of the Contract
	 */
	void addRevenue(long member, long contract) {
		if (revenues.findByMember(member).isEmpty()) {
			revenues.save(new Revenue(member, contract));
		}
	}

	/**
	 * Deletes the income through a members contract in the {@link RevenueRepository}
	 *
	 * @param member		ID of the member
	 */
	void deleteRevenue(long member) {
		revenues.findByMember(member).ifPresent(revenues::delete);
	}

	/**
	 * Returns all {@link Revenue}s saved in the {@link RevenueRepository}.
	 *
	 * @return all {@link Revenue}s
	 */
	Streamable<Revenue> findAll() {
		return revenues.findAll();
	}

	/**
	 * Returns income through all member contracts of one month.
	 *
	 * @return income of one month
	 */
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
