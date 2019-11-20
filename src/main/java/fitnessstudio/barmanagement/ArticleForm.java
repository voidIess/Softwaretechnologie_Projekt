package fitnessstudio.barmanagement;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public interface ArticleForm {

	@NotEmpty
	String getName();

	@NotEmpty
	String getArt();

	@NotEmpty
	String getDescription();

	@NotEmpty
	@Digits(fraction = 0, integer = 5)
	String getPrice();

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getExpiationDate();

	@NotEmpty
	@Size(min = 1, max = 99, message = "percent of discount from 0-99")
	String getPercentDiscount();

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getStartDiscount();

	@NotEmpty
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	String getEndDiscount();

	@NotEmpty
	@Digits(fraction = 0, integer = 5)
	String getNubmer();

}
