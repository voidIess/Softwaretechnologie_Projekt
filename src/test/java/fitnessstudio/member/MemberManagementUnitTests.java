package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Integration tests for {@link fitnessstudio.member.MemberManagement}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberManagementUnitTests {

	@Autowired
	private MemberManagement management;

	@Autowired
	private MemberRepository members;

	@Autowired
	private ContractRepository contracts;

	private Long contractId;
	private UserAccount userAccount;
	private Long memberId;

	@BeforeAll
	void setUp() {
		if (contracts.findAll().isEmpty()) {
			contracts.save(new Contract("Name", "Description", Money.of(100, "EUR"), 100));
		}
		contractId = contracts.findAll().toList().get(0).getContractId();
	}

	@Test
	@Order(1)
	void testCreateMember() {
		RegistrationForm form = new RegistrationForm("FirstName", "LastName", "UserName",
			"Password", "0123456789012345678912", "0123456789", contractId, "");

		Member member = management.createMember(form, null);
		userAccount = member.getUserAccount();
		memberId = member.getMemberId();


		assertThat(members.findById(memberId)).isNotEmpty();
	}

	@Test
	@Order(2)
	void testFindAllUnauthorized() {
		assertThat(management.findAllUnauthorized().contains(members.findById(memberId).get())).isTrue();
	}

	@Test
	@Order(3)
	void testAuthorizeMember() {
		management.authorizeMember(members.findById(memberId).get().getMemberId());
		assertThat(members.findById(memberId).get().getUserAccount().isEnabled()).isTrue();
	}

	@Test
	@Order(4)
	void testFindAllAuthorized() {
		assertThat(management.findAllAuthorized().contains(members.findById(memberId).get())).isTrue();
	}

	@Test
	@Order(5)
	void testFindAll() {
		assertThat(management.findAll().toList().contains(members.findById(memberId).get())).isTrue();
	}

	@Test
	@Order(6)
	void testFindByUserAccount() {
		assertThat(management.findByUserAccount(userAccount)).isNotEmpty();
	}

	@Test
	@Order(7)
	void testTrainFree() {
		Member member = members.findById(memberId).get();
		assertThat(member.isFreeTrained()).isFalse();
		management.trainFree(member);

		assertThat(members.findById(memberId).get().isFreeTrained()).isTrue();
	}

	@Test
	@Order(8)
	void testPayIn() {
		Member member = members.findById(memberId).get();
		Money oldAmount = member.getCredit();
		Money amount = Money.of(10, "EUR");

		management.memberPayIn(member, amount);
		assertThat(members.findById(memberId).get().getCredit()).isEqualTo(oldAmount.add(amount));

	}

	@Test
	@Order(40)
	void testDeleteMember() {
		assertThat(members.findById(memberId)).isNotEmpty();
		management.deleteMember(memberId);
		assertThat(members.findById(memberId)).isEmpty();
	}




}
