package fitnessstudio.barmanagement;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

public interface CheckoutForm {
	@NotEmpty
	@Digits(fraction = 0, integer = 10)
	String getCustomerId();
}
