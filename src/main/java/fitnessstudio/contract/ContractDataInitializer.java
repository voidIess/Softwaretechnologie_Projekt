package fitnessstudio.contract;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Initializes default contracts, when none are already persisted.
 * <ul>
 *     <li>Default contract "Gold"</li>
 * </ul>
 */
@Component
@Order(10)
public class ContractDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(ContractDataInitializer.class);

	private final ContractRepository contractRepository;

	/**
	 * Creates a new {@link ContractDataInitializer} with the given {@link ContractRepository}
	 *
	 * @param contractRepository must not be {@literal null}
	 */
	ContractDataInitializer(ContractRepository contractRepository) {
		Assert.notNull(contractRepository, "ContractRepository must not be null");

		this.contractRepository = contractRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see org.salespointframework.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {
		if (contractRepository.findAll().isEmpty()) {
			LOG.info("Saving simple testing contract.");
			contractRepository.save(new Contract("Gold Plus", "Eintritt für alles",
				Money.of(20, "EUR"), 600));
		}

	}
}
