package fitnessstudio.member;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;

class MemberControllerIntegrationTests extends AbstractIntegrationTests {

	@Autowired
	MemberController controller;

	@Test
	@WithMockUser(roles = "BOSS")
	void allowsAuthenticatedAccessToController() {
		ExtendedModelMap modelMap = new ExtendedModelMap();
		controller.members(modelMap);

		assertThat(modelMap.get("memberList")).isNotNull();
	}
}
