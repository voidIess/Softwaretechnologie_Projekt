package fitnessstudio.statistics;

import fitnessstudio.contract.Contract;
import fitnessstudio.invoice.InvoiceEvent;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.member.Member;
import fitnessstudio.member.MemberRepository;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import org.assertj.core.data.Offset;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RevenueUnitTests {

	@Autowired
	private RevenueRepository repository;

	@Autowired
	private StatisticManagement statistics;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private UserAccountManager userAccounts;

	@Autowired
	private StaffManagement staffManagement;

	@Autowired
	private MemberRepository members;

	private Member testMember;
	private Contract contract;
	private Money articlePrice = Money.of(5, "EUR");
	private double expenditure = 0;
	private double revenue = 0;
	private final Offset<Double> PRECISION = Offset.offset(0.01);

	@BeforeAll
	void setUp() {
		testMember = new Member();
		contract = new Contract("cname", "description", Money.of(100, "EUR"), 40);

		for (Staff staff : staffManagement.getAllStaffs()) {
			expenditure += staff.getSalary().getNumberStripped().doubleValue();
			System.out.println("out "+staff.getSalary().getNumberStripped().doubleValue());
		}

		for (Member member : members.findAll()) {
			if (member.getContract() != null) {
				revenue += member.getContract().getPrice().getNumberStripped().doubleValue();
			}
		}
	}

	@Test
	@Order(1)
	void testCreateRevenue() {
		long size = repository.count();
		statistics.addRevenue(testMember.getMemberId(), contract.getContractId());
		assertThat(repository.count()).isEqualTo(size+1);
	}

	@Test
	@Order(2)
	void testFindAll() {
		assertThat(statistics.findAllRevenues().isEmpty()).isFalse();
	}

	@Test
	@Order(4)
	void testGetSellingEarningsOfDate() {
		double startValue = statistics.getSellingEarningsOfDate(LocalDate.now()).doubleValueExact();
		double nextValue = startValue + articlePrice.getNumber().doubleValueExact();

		applicationEventPublisher.publishEvent(new InvoiceEvent(this, testMember.getMemberId(),
				InvoiceType.CASHPAYMENT, articlePrice, "description"));

		assertThat(statistics.getSellingEarningsOfDate(LocalDate.now()).doubleValueExact()).isCloseTo(nextValue, PRECISION);
	}

	@Test
	@Order(5)
	void testGetSellingEarningsOfThisWeek() {
		assertThat(statistics.getSellingEarningsOfThisWeek().length).isEqualTo(7);
		assertThat(statistics.getSellingEarningsOfThisWeek()[LocalDate.now().getDayOfWeek().getValue()-1]
				.compareTo(statistics.getSellingEarningsOfDate(LocalDate.now()))).isZero();
	}

	@Test
	@Order(6)
	void testDeleteRevenue() {
		long size = repository.count();
		statistics.deleteRevenue(testMember.getMemberId());
		assertThat(repository.count() == size-1).isTrue();
		assertThat(repository.findByMember(testMember.getMemberId()).isEmpty()).isTrue();
	}

	@Test
	@Order(7)
	void testGetPercentageExpenditure() {
		double percentageExpenditure = expenditure / (expenditure + revenue) * 100;
		assertThat(statistics.getPercentageExpenditure()).isCloseTo(percentageExpenditure, PRECISION);
	}

	@Test
	@Order(8)
	void testGetPercentageRevenue() {
		double percentageRevenue = revenue / (revenue + expenditure) * 100;
		assertThat(statistics.getPercentageRevenue()).isCloseTo(percentageRevenue, PRECISION);
	}


}
