package fitnessstudio.roster;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.staff.Staff;
import fitnessstudio.staff.StaffManagement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RosterIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	StaffManagement staffManagement;

	@Test
	void defaultRosterTest() throws Exception {
		mvc.perform(get("/roster").with(user("staff").roles("STAFF")))
				.andExpect(status().is(302))

				.andExpect(view().name("redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
	}

	@Test
	void getRosterViewTest() throws Exception {
		mvc.perform(get("/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
				.with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("roster")).andExpect(model().size(6))
				.andExpect(view().name("roster/rosterView"));
	}

	@Test
	void getRosterViewFilteredTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mvc.perform(get("/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "/" + staff.getStaffId())
				.with(user("staff").roles("STAFF"))).andExpect(status().isOk())
				.andExpect(model().attributeExists("filter")).andExpect(model().size(7))
				.andExpect(view().name("roster/rosterView"));
	}

	@Test
	void getNewRosterEntryTest() throws Exception {
		mvc.perform(get("/roster/newRosterEntry/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
				.with(user("staff").roles("STAFF")))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("form")).andExpect(model().size(7))
				.andExpect(view().name("roster/rosterNew"));
	}

	@Test
	void getShowDetailTest() throws Exception {
		mvc.perform(get("/roster/detail/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "/1/1/72")
				.with(user("staff").roles("STAFF")))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("form")).andExpect(model().size(8))
				.andExpect(view().name("roster/rosterDetail"));
	}

	@Test
	void getDeleteTest() throws Exception {
		mvc.perform(get("/roster/detail/delete/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + "/1/1/72")
				.with(user("staff").roles("STAFF")))
				.andExpect(status().is(302))
				.andExpect(view().name("redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
	}

	@Test
	void postEditEntryTest() throws Exception {
		//	mvc.perform(post("/roster/editEntry/72")
		//			.with(user("staff").roles("STAFF")).with(csrf())).andExpect(status().is(302))
		//			.andExpect(view().name("redirect:/roster/" + Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)));
	}

	@Test
	void postCreateNewRosterEntryTest() throws Exception {
		//mvc.perform(post("/roster/newRosterEntry?staff=1&week=1&roles=COUNTER&times=06:00-08:00&day=1")
		//		.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302))
		//		.andExpect(view().name("redirect:/roster/1"));
	}


	@Test
	void postDeleteStaffTest() throws Exception {
		Staff staff = staffManagement.getAllStaffs().iterator().next();
		mvc.perform(post("/staff/delete/" + staff.getStaffId())
				.with(user("boss").roles("BOSS")).with(csrf())).andExpect(status().is(302))
				.andExpect(view().name("redirect:/staffs"));
	}


}
