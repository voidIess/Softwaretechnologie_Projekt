package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffRole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RosterDataConverter {

	public static String[] germanWeekDay = new String[]{"Sa.", "So.","Mo.", "Di.", "Mi.", "Do.", "Fr.", };
	public static int[] dayOfWeek = new int[]{2,3,4,5,6,0,1};
	public static final String COUNTER = "Thekenkraft";
	public static final String TRAINER = "Trainer";

	private RosterDataConverter () {}

	public static List<String> getWeekDatesByWeek (int week) {
		List<String> weekDates = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormatWeekDates = new SimpleDateFormat("dd.MM.yyyy");

		if (c.get(Calendar.WEEK_OF_YEAR) <= week) { // Wenn derzeitige Woche größer als die gefordert ist (z.b. 52 und 1) -> Neues Jahr!
			c.set(Calendar.WEEK_OF_YEAR, week);
		} else {
			c.add(Calendar.YEAR,1);
			c.set(Calendar.WEEK_OF_YEAR, week);
		}

		/*for (int i = 2; i<7;i++){
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(germanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}

		for (int i = 0; i<2; i++) {
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(germanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}*/
		for (int i : dayOfWeek) {
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(germanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}

		return weekDates;
	}

	public static List<String> getRoleList(){
		List<String> roles = new ArrayList<>();
		roles.add(COUNTER);
		roles.add(TRAINER);
		return roles;
	}

	public static StaffRole stringToRole (String role) {
		Assert.notNull(role, "Die Rolle darf nicht 'null' sein!");
		if (role.equals(TRAINER)) return StaffRole.TRAINER;
		else if (role.equals(COUNTER)) return StaffRole.COUNTER;
		else throw new IllegalArgumentException("Es gibt diese Rolle nicht!");
	}

	public static String roleToString (StaffRole role) {
		if (role == StaffRole.COUNTER){
			return COUNTER;
		} else {
			return TRAINER;
		}
	}
}
