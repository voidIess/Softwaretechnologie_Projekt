package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Formular mit Angaben zum Erstellen eines neuen Mitarbeiters
 */
public interface StaffForm {

	@NotEmpty(message = "Username ist leer.")
	@Size(max= 16, message = "Der Benutzername darf aus maximal 16 Zeichen bestehen.")
	String getUsername();

	@NotEmpty(message = "Email ist leer.")
	String getEmail();

	@NotEmpty(message = "Passwort ist leer.")
	String getPassword();

	@NotEmpty(message = "Vorname ist leer.")
	@Size(max= 32, message = "Der Vorname darf aus maximal 32 Zeichen bestehen.")
	String getFirstName();

	@NotEmpty(message = "Nachname ist leer.")
	@Size(max= 32, message = "Der Nachname darf aus maximal 32 Zeichen bestehen.")
	String getLastName();

	@NotEmpty(message = "Gehalt ist leer.")
	String getSalary();

}
