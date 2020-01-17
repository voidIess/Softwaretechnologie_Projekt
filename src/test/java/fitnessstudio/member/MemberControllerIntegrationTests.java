package fitnessstudio.member;

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
 * Integration tests for {@link fitnessstudio.member.MemberController}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	MemberManagement memberManagement;

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
				.andExpect(model().attributeExists("unauthorizedMember"))
				.andExpect(view().name("member/members"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void authorizeMemberIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/admin/authorizeMember"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("unauthorizedMember"));
	}

	@Test
	void getRegisterFromFriendController() throws Exception {
		Member member = memberManagement.findAllAuthorized(String.valueOf(
				memberManagement.findAll().iterator().next().getMemberId())).iterator().next();
		mockMvc.perform(get("/register/" + member.getMemberId()))
				.andExpect(model().attributeExists("form"))
				.andExpect(model().attributeExists("error"))
				.andExpect(model().attributeExists("friendCode"))
				.andExpect(model().attributeExists("contractList"))
				.andExpect(status().isOk())
				.andExpect(view().name("register"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getDetailController() throws Exception {
		mockMvc.perform(get("/member/home"))
				.andExpect(model().attributeExists("member"))
				.andExpect(model().attributeExists("contractText"))
				.andExpect(status().isOk())
				.andExpect(view().name("member/memberDetail"));
	}

	@Test
	void getEditMemberController() throws Exception {
		mockMvc.perform(get("/member/edit").with(user("member").roles("MEMBER")))
				.andExpect(status().isOk())
				.andExpect(view().name("/member/editMember"));

	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void getCheckout() throws Exception {
		Member member = memberManagement.findAllAuthorized(String.valueOf(
				memberManagement.findAll().iterator().next().getMemberId())).iterator().next();
		mockMvc.perform(get("/member/checkout/" + member.getMemberId()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/checkin"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void getCheckIn() throws Exception {
		mockMvc.perform(get("/checkin"))
				.andExpect(model().attributeExists("attendants"))
				.andExpect(status().isOk())
				.andExpect(view().name("checkin"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void getDeleteMember() throws Exception {
		Member member = memberManagement.findAllAuthorized(String.valueOf(
				memberManagement.findAll().iterator().next().getMemberId())).iterator().next();
		mockMvc.perform(get("/member/delete/" + member.getMemberId()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/authorizeMember"));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void getAuthorize() throws Exception {
		Member member = memberManagement.findAllAuthorized(String.valueOf(
				memberManagement.findAll().iterator().next().getMemberId())).iterator().next();
		mockMvc.perform(get("/member/authorize/" + member.getMemberId()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/admin/authorizeMember"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getInvoices() throws Exception {
		mockMvc.perform(get("/member/invoices"))
				.andExpect(model().attributeExists("invoices"))
				.andExpect(status().is(200))
				.andExpect(view().name("member/memberInvoices"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getPause() throws Exception {
		mockMvc.perform(get("/member/pause"))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/member/home"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getShowPause() throws Exception {
		mockMvc.perform(get("/member/memberPause"))
				.andExpect(status().is(200))
				.andExpect(view().name("member/memberPause"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getInviteFriend() throws Exception {
		mockMvc.perform(get("/member/invite"))
				.andExpect(model().attributeExists("memberId"))
				.andExpect(model().attributeExists("memberName"))
				.andExpect(model().attributeExists("form"))
				.andExpect(status().is(200))
				.andExpect(view().name("member/inviteFriend"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getShowEnd() throws Exception {
		mockMvc.perform(get("/member/endMembership"))
				.andExpect(status().is(200))
				.andExpect(view().name("member/endMembership"));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void getEnd() throws Exception {
		mockMvc.perform(get("/member/end"))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/login"));
	}

	@Test
	void postPrintPdfInvoice() throws Exception {
		mockMvc.perform(post("/printPdfInvoice")
				.with(user("member").roles("MEMBER")).with(csrf()))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/member/home"));
	}

}
