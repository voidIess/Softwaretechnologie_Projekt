package fitnessstudio.roster;

import fitnessstudio.staff.StaffRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RosterDataConverterTest {

	@Test
	void testRoleToString() {
		assertThat(RosterDataConverter.roleToString(StaffRole.TRAINER)).isEqualTo("Trainer");
		assertThat(RosterDataConverter.roleToString(StaffRole.COUNTER)).isEqualTo("Thekenkraft");
	}

	@Test
	void testGetRoleList() {
		List<String> roles = RosterDataConverter.getRoleList();
		assertThat(roles.contains("Thekenkraft")).isTrue();
		assertThat(roles.contains("Trainer")).isTrue();
		assertThat(roles.size() == 2).isTrue();
	}

	@Test
	void testStringToRole() {
		StaffRole role;
		assertThat(RosterDataConverter.stringToRole("Trainer")).isEqualTo(StaffRole.TRAINER);
		assertThat(RosterDataConverter.stringToRole("Thekenkraft")).isEqualTo(StaffRole.COUNTER);
		try {
			role = RosterDataConverter.stringToRole(null);
			fail("Der String darf nicht null sein.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try{
			role = RosterDataConverter.stringToRole("hidabjsksndjghrs");
			fail("Diese Rolle sollte es nicht geben!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	//TODO: getWeekList testen

}
