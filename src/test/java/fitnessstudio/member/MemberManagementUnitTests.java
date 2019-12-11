package fitnessstudio.member;

import fitnessstudio.contract.Contract;
import fitnessstudio.contract.ContractRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
		RegistrationForm form = new RegistrationForm("FirstName", "LastName","email@email67e3838.de", "UserName",
			"Password", "0123456789012345678912", "0123456789", contractId, "");

		Member member = management.createMember(form, null);
		userAccount = member.getUserAccount();
		memberId = member.getMemberId();


		assertThat(members.findById(memberId)).isNotEmpty();
		assertThat(member.getContract()).isNotNull();
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
		Member member = members.findById(memberId).get();

		assertThat(member.getUserAccount().isEnabled()).isTrue();
	}

	@Test
	@Order(4)
	void testFindAllAuthorized() {
		assertThat(management.findAllAuthorized("").contains(members.findById(memberId).get())).isTrue();
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

		management.memberPayIn(member.getMemberId(), amount);
		assertThat(members.findById(memberId).get().getCredit()).isEqualTo(oldAmount.add(amount));

	}

	@Test
	@Order(9)
	void testPause(){
		Member member = members.findById(memberId).get();
		LocalDate endDate = member.getEndDate();
		Money oldCredit = member.getCredit();
		management.pauseMembership(member);

		assertThat(member.isPaused()).isTrue();
		assertThat(member.getLastPause()).isEqualTo(LocalDate.now());
		member.setLastPause(LocalDate.now().minusDays(33));
		members.save(member);

		assertThat(member.getEndDate()).isEqualTo(endDate.plusDays(31));
		assertThat(member.getCredit()).isEqualTo(oldCredit.add(member.getContract().getPrice()));

	}

	@Test
	@Order(10)
	void testCheckMembershipsUnPause(){
		management.checkMemberships();

		Member member = members.findById(memberId).get();
		assertThat(member.isPaused()).isFalse();
	}

	@Test
	@Order(11)
	void testCannotPauseAgain() {
		Member member = members.findById(memberId).get();

		management.pauseMembership(member);
		assertThat(member.isPaused()).isFalse();
	}

	@Test
	@Order(15)
	void testCheckMembershipsDisableExpired() {
		Member member = members.findById(memberId).get();
		member.setEndDate(LocalDate.now());
		members.save(member);

		management.checkMemberships();
		assertThat(members.findById(memberId).get().getUserAccount().isEnabled()).isFalse();
	}

	@Test
	@Order(16)
	void testGetCreditOfDate() {
		Member member = members.findById(memberId).get();
		Money oldAmount = member.getCredit();
		assertThat(management.getMemberCreditOfDate(member, LocalDate.now()).equals(oldAmount)).isTrue();
	}

	@Test
	@Order(40)
	void testDeleteMember() {
		assertThat(members.findById(memberId)).isNotEmpty();
		management.deleteMember(memberId);
		assertThat(members.findById(memberId)).isEmpty();
	}


}
