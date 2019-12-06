package fitnessstudio.roster;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RosterEntryForm {

	@NotNull(message = "{roster.error.staff}")
	private final Long staff;

	@NotEmpty(message = "{roster.error.role}")
	private final String role;

	@NotNull(message = "{roster.error.time}") //
	private final List<String> times;

	@NotEmpty(message = "{roster.error.day}") //
	private final String day;


	public RosterEntryForm(Long staff, String role, String day, List<String> times) {
		this.role = role;
		this.staff = staff;
		this.day = day;
		this.times = times;
	}

	public Long getStaff() {
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

