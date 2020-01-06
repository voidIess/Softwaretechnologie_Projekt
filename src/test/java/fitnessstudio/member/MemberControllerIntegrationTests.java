package fitnessstudio.member;

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
 * Integration tests for {@link fitnessstudio.member.MemberController}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void preventsPublicAccessForMembers() throws Exception {
		mockMvc.perform(get("/admin/members"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventsPublicAccessForAuthorizedMembers() throws Exception {
		mockMvc.perform(get("/admin/authorizeMember"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventPublicAccessForMemberPayIn() throws Exception {
		mockMvc.perform(post("/member/payin"))
			.andExpect(status().isForbidden());
	}

	@Test
	void preventPublicAccessForMemberDelete() throws Exception {
		mockMvc.perform(get("/member/delete/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));

	}

	@Test
	void preventPublicAccessForMemberAuthorize() throws Exception {
		mockMvc.perform(get("/member/authorize/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));

	}

	@Test
	void preventPublicAccessForMemberCheckIn() throws Exception {
		mockMvc.perform(get("/checkin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventPublicAccessForMemberCheckOut() throws Exception {
		mockMvc.perform(get("/member/checkout/{id}", "1"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventPublicAccessForPrintPdfInvoice() throws Exception {
		mockMvc.perform(post("/printPdfInvoice"))
				.andExpect(status().isForbidden());
	}

	@Test
	void registerIsAccessibleForPublic() throws Exception {
		mockMvc.perform(get("/register"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("form"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void membersIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/admin/members"))
			.andExpect(status().isOk()) //
			.andExpect(model().attributeExists("memberList"))
			.andExpect(model().attributeExists("unauthorizedMember"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void authorizeMemberIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/admin/authorizeMember"))
			.andExpect(status().isOk()) //
			.andExpect(model().attributeExists("unauthorizedMember"));
	}

}
