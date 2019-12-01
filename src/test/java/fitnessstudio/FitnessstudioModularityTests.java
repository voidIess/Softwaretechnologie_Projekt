package fitnessstudio;

import de.olivergierke.moduliths.model.Modules;
import org.junit.jupiter.api.Test;

class FitnessstudioModularityTests {

	@Test
	void assertModularity() {
		Modules.of(Application.class).verify();
	}
}
