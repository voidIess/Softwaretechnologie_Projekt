package fitnessstudio.contract;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link fitnessstudio.contract.ContractController}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ContractManagement contractManagement;


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

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void getContractsController() throws Exception {
		mockMvc.perform(get("/admin/contracts"))
				.andExpect(model().attributeExists("contractList"))
				.andExpect(status().isOk())
				.andExpect(view().name("contract/contracts"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void getDeleteContractController() throws Exception {
		Contract contract = contractManagement.getAllContracts().iterator().next();
		mockMvc.perform(get("/admin/contract/delete/" + contract.getContractId()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/contracts"));
	}

	@Test
	void postCreateNewContractController() throws Exception {
		mockMvc.perform(post("/admin/contract/create?name=Student&description=abcd123&price=10&duration=300")
				.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/contracts"));
	}

	@Test
	void postEditContractController() throws Exception {
		Contract contract = contractManagement.getAllContracts().iterator().next();
		mockMvc.perform(post("/admin/contract/detail/" + contract.getContractId()
				+ "?name=VIP&description=vip&price=100&duration=360")
				.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/contracts"));
	}


	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void getDetailController() throws Exception {
		Contract contract = contractManagement.getAllContracts().iterator().next();
		mockMvc.perform(get("/admin/contract/detail/" + contract.getContractId()))
				.andExpect(model().attributeExists("contract"))
				.andExpect(model().attributeExists("form"))
				.andExpect(status().isOk())
				.andExpect(view().name("contract/contractDetail"));
	}

	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void getContractInfoController() throws Exception {
		Contract contract = contractManagement.getAllContracts().iterator().next();
		mockMvc.perform(get("/admin/contract/delete/" + contract.getContractId()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/contracts"));
	}
}
