package fitnessstudio.studio;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class StudioIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	StudioService studioService;

	@Autowired
	MockMvc mvc;

	/**
	 * U-1-01
	 */
	@Test
	void getStudioTest() {
		assertNotNull(studioService.getStudio());
	}

	/**
	 * U-1-02
	 */
	@Test
	void saveStudioTest() {
		Studio studio2 = new Studio();
		studioService.saveStudio(studio2);
		assertNotEquals(studioService.getStudio(), studio2);
	}

	/**
	 * I-1-01
	 */
	@Test
	void getStudioController() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attributeExists("openingTimes"))
				.andExpect(model().attributeExists("address"))
				.andExpect(model().attributeExists("studioName"))
				.andExpect(model().attributeExists("contractList"))
				.andExpect(model().attributeExists("advertisingBonus")).andExpect(model().size(5));
	}


	/**
	 * I-1-02
	 */
	@Test
	void getEditStudioController() throws Exception {
		mvc.perform(get("/studio").with(user("boss").roles("BOSS"))).andExpect(status().isOk())
				.andExpect(view().name("studio")).andExpect(model().attributeExists("studioForm"))
				.andExpect(model().attribute("studio", studioService.getStudio()))
				.andExpect(model().size(2));
	}

	/**
	 * I-1-03
	 */
	@Test
	void postEditStudioController() throws Exception {
		mvc.perform(post(
				"/studio?advertisingBonus=10&openingTimes=Mo-Fr&address=abcxyz&name=Fitness Final")
				.with(user("boss").roles("BOSS")).with(csrf()))
				.andExpect(status().is(302)).andExpect(view().name("redirect:/"));
	}

	/**
	 * I-1-04
	 */
	@Test
	void postErrorEditStudioController() throws Exception {
		mvc.perform(post(
				"/studio?advertisingBonus=-1&openingTimes=Mo-Fr&address=abcxyz&name=Fitness Second")
				.with(user("boss").roles("BOSS")).with(csrf()))
				.andExpect(status().is(200)).andExpect(view().name("error"));
	}


}
