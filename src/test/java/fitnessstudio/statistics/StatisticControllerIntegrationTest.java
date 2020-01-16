package fitnessstudio.statistics;

import fitnessstudio.AbstractIntegrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class StatisticControllerIntegrationTest extends AbstractIntegrationTests {

	@Autowired
	StatisticController controller;

	/**
	 * I-6-01
	 */
	@Test
	void rejectsUnauthenticatedAccessToController() {
		assertThatExceptionOfType(AuthenticationException.class) //
				.isThrownBy(() -> controller.showAttendanceStatistic(new ExtendedModelMap()));
	}

	/**
	 * I-6-02
	 */
	@Test
	@WithMockUser(roles = "BOSS")
	void allowsAuthenticatedAccessToController() {
		ExtendedModelMap model = new ExtendedModelMap();
		controller.showAttendanceStatistic(model);
		assertThat(model.get("averageTimes")).isNotNull();
		assertThat(model.get("memberAmounts")).isNotNull();
		assertThat(model.get("sellingEarnings")).isNotNull();
	}

}
