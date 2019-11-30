package fitnessstudio.contract;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for {@link fitnessstudio.contract.ContractController}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void preventsPublicAccessForContractCreate() throws Exception {
		mockMvc.perform(get("/admin/contract/create"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void preventStaffAccessForContractCreate() throws Exception {
		mockMvc.perform(get("/admin/contract/create"))
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void contractCreateIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/admin/contract/create"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("form"));
	}

	@Test
	void preventsPublicAccessForContracts() throws Exception {
		mockMvc.perform(get("/admin/contracts"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void contractsIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/admin/contracts"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("contractList"));
	}

	@Test
	void preventsPublicAccessForContractDelete() throws Exception {
		mockMvc.perform(get("/admin/contract/delete/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void preventStaffAccessForContractDelete() throws Exception {
		mockMvc.perform(get("/admin/contract/delete/{id}", "1"))
			.andExpect(status().isForbidden());
	}

	@Test
	void preventsPublicAccessForContractDetail() throws Exception {
		mockMvc.perform(get("/admin/contract/detail/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void preventStaffAccessForContractDetail() throws Exception {
		mockMvc.perform(get("/admin/contract/detail/{id}", "1"))
			.andExpect(status().isForbidden());
	}

	@Test
	void contractInformationIsAccessibleForPublic() throws Exception {
		mockMvc.perform(get("/contract_information"))
			.andExpect(status().isOk()) //
			.andExpect(model().attributeExists("contractList"));
	}
}
