package fitnessstudio.roster;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class RosterEntryForm {

	@NotEmpty(message = "{roster.error.staff}")
	private final String staff;

	@NotEmpty(message = "{roster.error.role}")
	private final String role;

	//@NotEmpty(message = "{roster.error.time}") //
	private final List<String> times;

	@NotEmpty(message = "{roster.error.day}") //
	private final String day;

	public RosterEntryForm(String staff, String role, String day, List<String> times) {
		this.role = role;
		this.staff = staff;
		this.day = day;
		this.times = times;
	}

	public String getStaff() {
		return staff;
	}

	public String getRole() {
		return role;
	}

	public List<String> getTimes() {
		return times;
	}

	public String getDay() {
		return day;
	}
}
