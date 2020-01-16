package fitnessstudio.pdf;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import fitnessstudio.contract.Contract;
import fitnessstudio.invoice.InvoiceEntry;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
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
	UserAccountManager accounts;

	@Autowired
	PdfView pdfView;

	@BeforeAll
	void setUp() throws IOException {
		httpServletResponse.setHeader("Content-Disposition", "test");
		PdfWriter pdfWriter = new PdfWriter(httpServletResponse.getOutputStream());
		PdfDocument pdf = new PdfDocument(pdfWriter);
		document = new Document(pdf);
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

	@Test
	void testGeneratePdfInvoice() {
		Member member = new Member();
		member.setContract(new Contract("name", "description", Money.of(0, "EUR"), 3));
		java.util.List<InvoiceEntry> entries = new LinkedList<>();

		Map<String, Object> map = new HashMap<>();
		map.put("type", "invoice");
		map.put("member", member);
		map.put("endDate", LocalDate.now());
		map.put("endCredit", Money.of(0, "EUR"));
		map.put("startDate", LocalDate.now());
		map.put("startCredit", Money.of(100, "EUR"));
		map.put("invoiceEntries", entries);

		assertThat(PdfGenerator.generatePdf(map, document)).isNotNull();

	}

	@Test
	void testGeneratePdfPayslip() {
		UserAccount account = accounts.create("pdfTestStaff", Password.UnencryptedPassword.of("123"), "pdfTestStaff@email.de", Role.of("STAFF"));
		Staff staff = new Staff(account, "firstName", "lastName", Money.of(0, "EUR"));

		Map<String, Object> map = new HashMap<>();
		map.put("type", "payslip");
		map.put("staff", staff);

		assertThat(PdfGenerator.generatePdf(map, document)).isNotNull();

	}

	@Test
	void testPdfView() {
		assertThrows(NullPointerException.class, () -> {
			pdfView.renderMergedOutputModel(Map.of("type", "payslip"), httpServletRequest, httpServletResponse);
		});
	}

	@AfterAll
	void cleanUp() {
		document.close();
	}

}
