package fitnessstudio.member;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(10)
public class ContractDataInitializer implements DataInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(ContractDataInitializer.class);

	private final ContractManagement contractManagement;
	private final ContractRepository contractRepository;

	ContractDataInitializer(ContractManagement contractManagement, ContractRepository contractRepository){

		Assert.notNull(contractManagement, "ContractManagement must not be null");
		Assert.notNull(contractRepository, "ContractRepository must not be null");

		this.contractManagement = contractManagement;
		this.contractRepository = contractRepository;
	}

	@Override
	public void initialize() {
		if (contractRepository.findAll().isEmpty()){
			LOG.info("Saving simple testing contract.");
			contractRepository.save(new Contract("Test Contract", "Simple Gym Access", Money.of(20, "EUR"),600));
		}

	}
}
