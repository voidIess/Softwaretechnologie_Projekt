package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffRepository;
import fitnessstudio.staff.StaffRole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class RosterDataConverter {

	private static String[] GermanWeekDay = new String[]{"Sa.", "So.","Mo.", "Di.", "Mi.", "Do.", "Fr.", };
	private  static SimpleDateFormat dateFormatWeekDates = new SimpleDateFormat("dd.MM.yyyy");

	public static List<String> getWeekDatesByWeek (int week) {
		List<String> weekDates = new ArrayList<>();
		Calendar c = Calendar.getInstance();

		if (c.get(Calendar.WEEK_OF_YEAR) <= week) { // Wenn derzeitige Woche größer als die gefordert ist (z.b. 52 und 1) -> Neues Jahr!
			c.set(Calendar.WEEK_OF_YEAR, week);
		} else {
			c.add(Calendar.YEAR,1);
			c.set(Calendar.WEEK_OF_YEAR, week);
		}

		for (int i = 2; i<7;i++){
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(GermanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}

		for (int i = 0; i<2; i++) {
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(GermanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}

		return weekDates;
	}

	public static List<String> getRoleList(){
		List<String> roles = new ArrayList<>();
		roles.add("Thekenkraft");
		roles.add("Trainer");
		return roles;
	}

	public static StaffRole stringToRole (String role) {
		Assert.notNull(role, "Die Rolle darf nicht 'null' sein!");
		if (role.equals("Trainer")) return StaffRole.TRAINER;
		else if (role.equals("Thekenkraft")) return StaffRole.COUNTER;
		else throw new IllegalArgumentException("Es gibt diese Rolle nicht!");
	}

	public static String roleToString (StaffRole role) {
		if (role == StaffRole.COUNTER){
			return "Thekenkraft";
		} else {
			return "Trainer";
		}
	} 	// Konvertiert die Aufgabe des Staffs in einen String mit deutscher Übersetzung
}
