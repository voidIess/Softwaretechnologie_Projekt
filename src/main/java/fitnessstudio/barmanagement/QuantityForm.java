package fitnessstudio.barmanagement;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

public interface QuantityForm{

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getExpirationDate();

	@NotEmpty
	@Digits(fraction = 0, integer = 5)
	String getAmount();
}
