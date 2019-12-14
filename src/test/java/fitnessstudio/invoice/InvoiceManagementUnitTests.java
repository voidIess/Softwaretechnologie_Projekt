package fitnessstudio.invoice;

import fitnessstudio.member.Member;
import fitnessstudio.member.MemberRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceManagementUnitTests {

	@Autowired
	private InvoiceManagement invoiceManagement;

	@Autowired
	private InvoiceEntryRepository invoiceEntryRepository;

	@Autowired
	private MemberRepository members;

	private Long member;
	private InvoiceEntry entry;

	@BeforeAll
	void setUp() {
		if (members.findAll().isEmpty()) {
			members.save(new Member());
		}
		member = members.findAll().toList().get(0).getMemberId();
	}


	@Test
	@Order(1)
	void createInvoiceEntry() {
		Money amount = Money.of(10, "EUR");

		InvoiceEvent event = new InvoiceEvent(this, member, InvoiceType.DEPOSIT,
			amount, "Test InvoiceEvent");
		entry = invoiceManagement.createInvoiceEntry(event);

		assertThat(invoiceEntryRepository.findAllByMember(member)).contains(entry);
	}

	@Test
	@Order(2)
	void getAllInvoicesForMember() {
		assertThat(invoiceManagement.getAllInvoicesForMember(member)).contains(entry);
	}

	@Test
	@Order(3)
	void getAllInvoiceForMemberOfLastMonth() {
		assertThat(invoiceManagement.getAllInvoiceForMemberOfLastMonth(member).contains(entry)).isFalse();

		LocalDate date = LocalDate.now().minusDays(31);
		entry.setCreated(date);
		invoiceEntryRepository.save(entry);

		assertThat(invoiceManagement.getAllInvoiceForMemberOfLastMonth(member)).contains(entry);
	}

	@Test
	@Order(4)
	void getAllEntriesForMemberBefore() {
		assertThat(invoiceManagement.getAllEntriesForMemberBefore(member,
			LocalDate.now().plusDays(100))).contains(entry);
		assertThat(invoiceManagement.getAllEntriesForMemberBefore(member,
			LocalDate.now().minusDays(100)).contains(entry)).isFalse();
	}
}