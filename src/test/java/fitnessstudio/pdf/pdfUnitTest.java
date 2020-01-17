package fitnessstudio.pdf;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import fitnessstudio.contract.Contract;
import fitnessstudio.invoice.InvoiceEntry;
import fitnessstudio.invoice.InvoiceManagement;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.member.Member;
import fitnessstudio.staff.Staff;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class pdfUnitTest {

	@Autowired @NotNull
	HttpServletResponse httpServletResponse;

	@Autowired @NotNull
	HttpServletRequest httpServletRequest;

	Document document;

	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	InvoiceManagement invoiceManagement;

	@Autowired
	UserAccountManager accounts;

	@Autowired
	PdfView pdfView;

	private InvoicePdfGenerator invoice;
	private PayslipPdfGenerator payslip;
	Map<String, Object> invoiceMap;
	Map<String, Object> payslipMap;

	@BeforeAll
	void setUp() throws IOException {
		httpServletResponse.setHeader("Content-Disposition", "test");
		PdfWriter pdfWriter = new PdfWriter(httpServletResponse.getOutputStream());
		PdfDocument pdf = new PdfDocument(pdfWriter);
		document = new Document(pdf);
		Money money = Money.of(0, "EUR");

		Member member = new Member();
		member.setContract(new Contract("name", "description", money, 3));

		UserAccount account = accounts.create("pdfTestStaff", Password.UnencryptedPassword.of("123"), "pdfTestStaff@email.de", Role.of("STAFF"));
		Staff staff = new Staff(account, "firstName", "lastName", money);

		// create invoice data
		List<InvoiceEntry> entries = new LinkedList<>();
		entries.add(new InvoiceEntry(member.getMemberId(), InvoiceType.DEPOSIT, money, ""));
		entries.add(new InvoiceEntry(member.getMemberId(), InvoiceType.CASHPAYMENT, money, ""));
		for (InvoiceEntry entry : entries) {
			entry.setCreated(LocalDate.now());
		}
		invoiceMap = new HashMap<>();
		invoiceMap.put("type", "invoice");
		invoiceMap.put("member", member);
		invoiceMap.put("endDate", LocalDate.now());
		invoiceMap.put("endCredit", money);
		invoiceMap.put("startDate", LocalDate.now());
		invoiceMap.put("startCredit", money);
		invoiceMap.put("invoiceEntries", entries);

		//create payslip data
		payslipMap = new HashMap<>();
		payslipMap.put("type", "payslip");
		payslipMap.put("staff", staff);
	}

	@Test
	void testPdfGeneratorIsInterface() {
		assertThat(PdfGenerator.class.isAssignableFrom(InvoicePdfGenerator.class)).isTrue();
		assertThat(PdfGenerator.class.isAssignableFrom(PayslipPdfGenerator.class)).isTrue();
	}

	@Test
	void testGetGermanMonth() {
		String[] expectedMonths = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August",
				"September", "Oktober", "November", "Dezember"};
		for(int i = 0; i < 12; i++) {
			assertThat(PdfGenerator.getGermanMonth(i+1).equals(expectedMonths[i])).isTrue();
		}
	}

	/**
	 * U-5-02
	 */
	@Test
		void testGetGermanMonthException() {
			assertThrows(IllegalArgumentException.class, () -> {
				PdfGenerator.getGermanMonth(13);
			});
		}

		@Test
		void testGeneratePdfInvoice() {
			assertThat(PdfGenerator.generatePdf(invoiceMap, document)).isNotNull();
		}

		/**
		 * U-5-01
		 */
		@Test
		void testGeneratePdfPayslip() {
			assertThat(PdfGenerator.generatePdf(payslipMap, document)).isNotNull();

		}

		/**
		 * U-5-03
		 */
		@Test
		void testPdfViewException() {
			assertThrows(NullPointerException.class, () -> {
				pdfView.renderMergedOutputModel(Map.of("type", "payslip"), httpServletRequest, httpServletResponse);
			});
		}

		@Test
		void testPdfViewInvoice() {
			assertDoesNotThrow(() -> {
				pdfView.renderMergedOutputModel(invoiceMap, httpServletRequest, httpServletResponse);
			});
		}

		@Test
		void testPdfViewPayslip() {
			assertDoesNotThrow(() -> {
				pdfView.renderMergedOutputModel(payslipMap, httpServletRequest, httpServletResponse);
			});
		}

		@AfterAll
		void cleanUp() {
			document.close();
		}

	}
