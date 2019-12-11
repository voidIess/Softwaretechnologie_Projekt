package fitnessstudio.barmanagement;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public interface DiscountForm {
	@NotEmpty
	@Size(max = 100, message = "percent of discount from 0-100")
	String getPercentDiscount();

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getStartDiscount();

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getEndDiscount();
}
