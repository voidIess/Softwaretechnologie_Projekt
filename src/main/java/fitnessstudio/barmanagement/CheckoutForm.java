package fitnessstudio.barmanagement;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

public interface CheckoutForm {
	@NotEmpty
	@Digits(fraction = 0, integer = 10)
	String getCustomerId();

	@NotEmpty
		@Digits(fraction = 0, integer = 1)
		@Min(0)
		@Max(1)
	String getPaymentMethod();
}
