package fitnessstudio.barmanagement;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

public interface ArticleForm {

	@NotEmpty
	String getName();

	@NotEmpty
	String getType();

	@NotEmpty
	String getDescription();

	@NotEmpty
	@Digits(fraction = 2, integer = 5)
	String getPrice();

	@NotEmpty
	@Digits(fraction = 0, integer = 5)
	String getSufficientQuantity();

}
