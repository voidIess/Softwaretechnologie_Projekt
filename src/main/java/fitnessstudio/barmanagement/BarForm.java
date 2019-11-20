package fitnessstudio.barmanagement;

import javax.validation.constraints.NotEmpty;

public interface BarForm {

	@NotEmpty(message = "Wer kauft?")
	String getName();
}
