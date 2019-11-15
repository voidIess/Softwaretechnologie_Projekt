package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

class RosterEntryForm {

	@NotEmpty(message = "{RegistrationForm.lastName.NotEmpty")
	private final String duration;

	@NotEmpty(message = "{RegistrationForm.loginName.NotEmpty")
	private final String date;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String staffs;

	@NotEmpty(message = "{RegistrationForm.password.NotEmpty}") //
	private final String roles;

	public RosterEntryForm(String roles, String date, String staffs, String duration) {
		this.roles = roles;
		this.duration = duration;
		this.date = date;
		this.staffs = staffs;
	}

	public String getRoles() {
		return roles;
	}

	public String getStaffs() {
		return staffs;
	}

	public String getDuration() {
		return duration;
	}

	public String getDate() {
		return date;
	}
}

