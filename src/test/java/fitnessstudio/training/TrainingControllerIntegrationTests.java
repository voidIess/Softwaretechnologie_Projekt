package fitnessstudio.training;

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
 * Integration tests for {@link fitnessstudio.training.TrainingController}.
 *
 * @author Bill Kippe
 */
@SpringBootTest
@AutoConfigureMockMvc
class TrainingControllerIntegrationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	void preventsPublicAccessForTrainingCreate() throws Exception {
		mockMvc.perform(get("/member/training/create"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void trainingCreateIsAccessibleForMember() throws Exception {
		mockMvc.perform(get("/member/training/create"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("staffs", "types", "form", "error"));
	}

	@Test
	void preventsPublicAccessForMemberTrainings() throws Exception {
		mockMvc.perform(get("/member/trainings"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "member", roles = "MEMBER")
	void memberTrainingsIsAccessibleForMember() throws Exception {
		mockMvc.perform(get("/member/trainings"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("trainings"));
	}

	@Test
	void preventsPublicAccessForAdminTrainings() throws Exception {
		mockMvc.perform(get("/admin/trainings"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void adminTrainingsIsAccessibleForStaff() throws Exception {
		mockMvc.perform(get("/admin/trainings"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("trainings", "requestedTrainings"));
	}

	@Test
	void preventsPublicAccessForAuthorizeTrainings() throws Exception {
		mockMvc.perform(get("/admin/training/authorize"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	@WithMockUser(username = "staff", roles = "STAFF")
	void authorizeTrainingsIsAccessibleForStaff() throws Exception {
		mockMvc.perform(get("/admin/training/authorize"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("requestedTrainings"));
	}

	@Test
	void preventsPublicAccessForTrainingAccept() throws Exception {
		mockMvc.perform(get("/training/accept/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventsPublicAccessForTrainingDecline() throws Exception {
		mockMvc.perform(get("/training/decline/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}

	@Test
	void preventsPublicAccessForTrainingEnd() throws Exception {
		mockMvc.perform(get("/training/end/{id}", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
	}
}
