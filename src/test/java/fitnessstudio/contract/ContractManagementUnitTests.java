package fitnessstudio.contract;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link fitnessstudio.contract.ContractManagement}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContractManagementUnitTests {

	@Autowired
	private ContractRepository contracts;

	@Autowired
	private ContractManagement management;

	private Long contractId;

	@Test
	@Order(1)
	void testCreateContract() {
		ContractForm form = new ContractForm("Golden +", "Access to everything", 50.00, 200);

		contractId = management.createContract(form).getContractId();
		assertThat(contracts.findById(contractId)).isNotEmpty();
	}

	@Test
	@Order(2)
	void testFindById() {
		assertThat(management.findById(contractId).isPresent()).isTrue();
	}

	@Test
	@Order(3)
	void testFindAll() {
		assertThat(management.getAllContracts().size()).isEqualTo(1);
	}

	@Test
	@Order(4)
	void testEditContract() {
		contractId = management.getAllContracts().get(0).getContractId();

		String newName = "newName";
		String newDescription = "newDescription";
		Double newPrice = 100.00;
		int newDuration = 999;

		ContractForm newForm = new ContractForm(newName, newDescription, newPrice, newDuration);
		management.editContract(contractId, newForm);


		Contract newContract = management.findById(contractId).get();

		assertThat(newContract.getName()).isEqualTo(newName);
		assertThat(newContract.getDescription()).isEqualTo(newDescription);
		assertThat(newContract.getPrice()).isEqualTo(Money.of(newPrice, "EUR"));
		assertThat(newContract.getDuration()).isEqualTo(newDuration);
	}

	@Test
	@Order(5)
	void testDeleteContract() {
		contractId = management.getAllContracts().get(0).getContractId();

		management.deleteContract(contractId);
		assertThat(contracts.findById(contractId).isEmpty()).isTrue();
	}
}
