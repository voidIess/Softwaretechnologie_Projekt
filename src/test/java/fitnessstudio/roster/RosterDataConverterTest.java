package fitnessstudio.roster;

import fitnessstudio.staff.StaffRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit Tests fuer RosterDataConverter
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RosterDataConverterTest {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 * U-3-27
	 */
	@Test
	void testRoleToString() {
		assertThat(RosterDataConverter.roleToString(StaffRole.TRAINER)).isEqualTo("Trainer");
		assertThat(RosterDataConverter.roleToString(StaffRole.COUNTER)).isEqualTo("Thekenkraft");
	}

	/**
	 * U-3-29
	 */
	@Test
	void testGetRoleList() {
		List<String> roles = RosterDataConverter.getRoleList();
		assertThat(roles.contains("Thekenkraft")).isTrue();
		assertThat(roles.contains("Trainer")).isTrue();
		assertThat(roles.size() == 2).isTrue();
	}

	/**
	 * U-3-28
	 */
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
		try {
			role = RosterDataConverter.stringToRole("hidabjsksndjghrs");
			fail("Diese Rolle sollte es nicht geben!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * U-3-30
	 */
	@Test
	void testGetWeekDatesByWeek() {
		Calendar c = Calendar.getInstance();
		List<String> times = RosterDataConverter.getWeekDatesByWeek(c.get(Calendar.WEEK_OF_YEAR));
		assertThat(times.size() == 7).isTrue();
		for (int i = 0; i<7; i++) {
			c.set(Calendar.DAY_OF_WEEK, RosterDataConverter.dayOfWeek[i]);
			System.out.println(sdf.format(c.getTime()));
			try {
				boolean test = rightDate(times.get(i), c);
				assertThat(test).isTrue();
			} catch (Exception e) {
				fail(i + " ist falsch!");
			}
		}
	}

	boolean rightDate(String date, Calendar compare) throws ParseException {
		Calendar c = Calendar.getInstance();
		date = date.substring(4);
		c.setTime(sdf.parse(date));
		return (c.get(Calendar.DAY_OF_YEAR) == compare.get(Calendar.DAY_OF_YEAR)
			&& c.get(Calendar.YEAR) == compare.get(Calendar.YEAR));
	}

}
