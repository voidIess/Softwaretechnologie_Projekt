package fitnessstudio.roster;

import com.mysema.commons.lang.Assert;
import fitnessstudio.staff.StaffRole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Dient zum konvertieren und generieren von Dienstplan spezifischen Listen und Informationen
 * @author Markus
 *
 */
public class RosterDataConverter {

	private static final String[] germanWeekDay = new String[]{"Sa.", "So.","Mo.", "Di.", "Mi.", "Do.", "Fr.", };
	static final int[] dayOfWeek = new int[]{2,3,4,5,6,0,1};
	public static final String COUNTER = "Thekenkraft";
	public static final String TRAINER = "Trainer";

	private RosterDataConverter () {}

	/**
	 * Generiert eine Liste der Form TT., (auf Deutsch, z.B. Mo.) DD.MM.YYYY fuer den Header des Dienstplans
	 * anhand einer Woche
	 * @param week Entsprechende Kalenderwoche
	 * @return Liste von Strings mit Datum
	 */
	public static List<String> getWeekDatesByWeek (int week) {
		List<String> weekDates = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		SimpleDateFormat dateFormatWeekDates = new SimpleDateFormat("dd.MM.yyyy");
		// Wenn derzeitige Woche größer als die gefordert ist (z.b. 52 und 1) -> Neues Jahr!
		// Wenn aber z.B. 31.12.2019 in 1. Woche von 2020, dann wäre hier 1 == 1.
		// Und wir würden auf die erste Woche von 2019 setzen
		if (c.get(Calendar.WEEK_OF_YEAR) <= week) {
			c.set(Calendar.WEEK_OF_YEAR, week);
		} else {
			c.add(Calendar.YEAR,1);
			c.set(Calendar.WEEK_OF_YEAR, week);
		}

		for (int i : dayOfWeek) {
			c.set(Calendar.DAY_OF_WEEK, i);
			weekDates.add(germanWeekDay[i] + " " + dateFormatWeekDates.format(c.getTime()));
		}

		return weekDates;
	}

	/**
	 * Gibt eine Liste mit allen vorhanden StaffRoles zurueck
	 * @return Liste aller StaffRoles
	 */
	public static List<String> getRoleList(){
		List<String> roles = new ArrayList<>();
		roles.add(COUNTER);
		roles.add(TRAINER);
		return roles;
	}

	/**
	 * Konvertiert den String (von roleToString) zu StaffRole
	 * @param role String der Rolle (von roleToString)
	 * @return StaffRole zu dem String
	 */
	public static StaffRole stringToRole (String role) {
		Assert.notNull(role, "Die Rolle darf nicht 'null' sein!");
		if (role.equals(TRAINER)){
			return StaffRole.TRAINER;
		} else if (role.equals(COUNTER)){
			return StaffRole.COUNTER;
		} else {
			throw new IllegalArgumentException("Es gibt diese Rolle nicht!");
		}
	}

	/**
	 * Konvertiert eine StaffRole zu einem String
	 * @param role Entsprechende Rolle
	 * @return Rolle als String
	 */
	public static String roleToString (StaffRole role) {
		if (role == StaffRole.COUNTER){
			return COUNTER;
		} else {
			return TRAINER;
		}
	}
}
