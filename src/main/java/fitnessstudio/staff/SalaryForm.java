package fitnessstudio.staff;

import javax.validation.constraints.NotEmpty;

public interface SalaryForm {

	@NotEmpty(message = "Gehalt ist leer.")
	String getSalary();
}
