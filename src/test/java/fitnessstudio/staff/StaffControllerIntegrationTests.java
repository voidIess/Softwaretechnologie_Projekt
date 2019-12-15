package fitnessstudio.staff;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void preventsPublicAccessToStaffs() throws Exception {
		mockMvc.perform(get("/staffs"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void staffsIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/staffs"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("staffs"));
	}

	@Test
	void preventsPublicAccessToStaffDetail() throws Exception {
		mockMvc.perform(get("/staff/{id}", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void staffDetailIsAccessibleForStaff() throws Exception {
		mockMvc.perform(get("/staffDetail"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("staff"));
	}

	@Test
	void preventsPublicAccessToNewStaff() throws Exception {
		mockMvc.perform(get("/newStaff"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void newStaffIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/newStaff"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("form"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	void preventPublicAccessForPrintPdfPayslip() throws Exception {
		mockMvc.perform(post("/printPdfPayslip"))
				.andExpect(status().isForbidden());
	}


}
