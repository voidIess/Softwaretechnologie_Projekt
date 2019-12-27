package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;

public interface EditStaffForm {

	@NotEmpty(message = "Vorname ist leer.")
	String getFirstName();

	@NotEmpty(message = "Nachname ist leer.")
	String getLastName();

	@NotEmpty(message = "Email ist leer.")
	String getEmail();


}
