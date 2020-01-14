package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;

/**
 * Formular fuer den Gehalt eines Mitarbeiters
 */
public interface SalaryForm {

	@NotEmpty(message = "Gehalt ist leer.")
	String getSalary();
}
