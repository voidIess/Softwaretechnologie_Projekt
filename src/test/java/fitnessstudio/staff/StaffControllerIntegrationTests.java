package fitnessstudio.staff;

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
 * Integretationtest für StaffController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StaffControllerIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	StaffManagement staffManagement;

	/**
	 * I-7-01
	 * @throws Exception
	 */
	@Test
	void preventsPublicAccessToStaffs() throws Exception {
		mockMvc.perform(get("/staffs"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	/**
	 * I-7-02
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void staffsIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/staffs"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("staffs"));
	}

	/**
	 * I-7-03
	 * @throws Exception
	 */
	@Test
	void preventsPublicAccessToStaffDetail() throws Exception {
		mockMvc.perform(get("/staff/{id}", 1))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	/**
	 * I-7-04
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void staffDetailIsAccessibleForStaff() throws Exception {
		mockMvc.perform(get("/staffDetail"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("staff"));
	}

	/**
	 * I-7-05
	 * @throws Exception
	 */
	@Test
	void preventsPublicAccessToNewStaff() throws Exception {
		mockMvc.perform(get("/newStaff"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	/**
	 * I-7-06
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "boss", roles = "BOSS")
	void newStaffIsAccessibleForAdmin() throws Exception {
		mockMvc.perform(get("/newStaff"))
				.andExpect(status().isOk()) //
				.andExpect(model().attributeExists("form"))
				.andExpect(model().attributeExists("error"));
	}

	/**
	 * I-7-07
	 * @throws Exception
	 */
	@Test
	void preventPublicAccessForPrintPdfPayslip() throws Exception {
		mockMvc.perform(post("/printPdfPayslip"))
				.andExpect(status().isForbidden());
	}

	/**
	 * I-7-08
	 * @throws Exception
	 */
	@Test
	void postAddStaffTest() throws Exception {
		mockMvc.perform(post("/newStaff?username=cr7&email=cr7@yahoo.com&password=123&firstName=ronaldo&lastName=cristiano&salary=450000").
				with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302)).andExpect(view().name("redirect:/"));
	}

	/**
	 * I-7-09
	 * @throws Exception
	 */
	@Test
	void postAddStaffErrorTest() throws Exception {
		mockMvc.perform(post("/newStaff?username=staff&email=managementTestStaff@email.de&password=123&firstName=ronaldo&lastName=cristiano&salary=450000").
				with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(200)).andExpect(view().name("staff/add_staff"));
	}

	/**
	 * I-7-10
	 * @throws Exception
	 */
	@Test
	void getEditStaffTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mockMvc.perform(get("/staff/edit/" + staff.getStaffId()).with(user("staff").roles("STAFF")))
				.andExpect(status().isOk()).andExpect(view().name("staff/edit_staff"))
				.andExpect(model().attribute("id", staff.getStaffId())).andExpect(model().attributeExists("form")).andExpect(model().size(3));
	}

	/**
	 * I-7-11
	 * @throws Exception
	 */
	@Test
	void getEditStaffErrorTest() throws Exception {
		mockMvc.perform(get("/staff/edit/6969").with(user("staff").roles("STAFF")))
				.andExpect(status().is(200)).andExpect(view().name("error"));
	}

	/**
	 * I-7-12
	 * @throws Exception
	 */
	@Test
	void postEditStaffTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mockMvc.perform(post("/staff/edit/" + staff.getStaffId() + "?username=m10&email=m10@yahoo.com&password=123&firstName=lionel&lastName=messi")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/staff/edit/" + staff.getStaffId()));

	}

	/**
	 * I-7-13
	 * @throws Exception
	 */
	@Test
	void postEditErrorStaffTest() throws Exception {
		mockMvc.perform(post("/staff/edit/6969?username=cr7&email=abcxyz@yahoo.com&password=123&firstName=abc&lastName=xyz")
				.with(user("staff").roles("STAFF"))
				.with(csrf())).andExpect(status().is(200)).andExpect(view().name("error"));
	}

	/**
	 * I-7-14
	 * @throws Exception
	 */
	@Test
	void detailTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mockMvc.perform(get("/staff/" + staff.getStaffId()).with(user("boss").roles("BOSS")))
				.andExpect(status().isOk()).andExpect(view().name("staff/staffDetail"))
				.andExpect(model().attributeExists("form")).andExpect(model().size(2));
	}

	/**
	 * I-7-15
	 * @throws Exception
	 */
	@Test
	void detailErrorTest() throws Exception {
		mockMvc.perform(get("/staff/6969").with(user("boss").roles("BOSS"))).andExpect(status().isOk())
				.andExpect(view().name("error")).andExpect(model().attribute("status", "400"))
				.andExpect(model().attribute("error", "ID NOT FOUND"));
	}

	/**
	 * I-7-16
	 * @throws Exception
	 */
	@Test
	void postEditSalaryTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mockMvc.perform(post("/staff/update/" + staff.getStaffId() + "?salary=300000")
				.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/staffs"));
	}

	/**
	 * I-7-17
	 * @throws Exception
	 */
	@Test
	void postEditSalaryErrorTest() throws Exception {
		mockMvc.perform(post("/staff/update/6969?salary=300000")
				.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(200))
				.andExpect(view().name("error"));
	}

	/**
	 * I-7-18
	 * @throws Exception
	 */
	@Test
	void postPrintPayslip() throws Exception {
		mockMvc.perform(post("/printPdfPayslip")
				.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/staff/staffDetail"));
	}


}
