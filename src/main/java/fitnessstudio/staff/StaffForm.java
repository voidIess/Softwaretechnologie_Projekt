package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;

public interface StaffForm {

	@NotEmpty(message = "Username ist leer.")
	String getUsername();

	@NotEmpty(message = "Email ist leer.")
	String getEmail();

	@NotEmpty(message = "Passwort ist leer.")
	String getPassword();

	@NotEmpty(message = "Vorname ist leer.")
	String getFirstName();

	@NotEmpty(message = "Nachname ist leer.")
	String getLastName();

	@NotEmpty(message = "Gehalt ist leer.")
	String getSalary();

}
